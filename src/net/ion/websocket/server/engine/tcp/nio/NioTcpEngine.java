//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket NIO Engine
//	Copyright (c) 2011 Innotrade GmbH, jWebSocket.org, Author: Jan Gnezda
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package net.ion.websocket.server.engine.tcp.nio;

import org.apache.log4j.Logger;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.config.EngineConfiguration;
import net.ion.websocket.common.engine.BaseEngine;
import net.ion.websocket.common.kit.*;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.server.engine.tcp.EngineUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * <p>
 * Tcp engine that uses java non-blocking io api to bind to listening port and handle incoming/outgoing packets. There's one 'selector' thread that is responsible only for handling socket operations. Therefore, every packet that should be sent will be firstly queued into concurrent queue, which is continuously processed by selector thread. Since the queue is concurrent, there's no blocking and a
 * call to send method will return immediately.
 * </p>
 * <p>
 * All packets that are received from remote clients are processed in separate worker threads. This way it's possible to handle many clients simultaneously with just a few threads. Add more worker threads to handle more clients.
 * </p>
 * <p>
 * Before making any changes to this source, note this: it is highly advisable to read from (or write to) a socket only in selector thread. Ignoring this advice may result in strange consequences (threads locking or spinning, depending on actual scenario).
 * </p>
 * 
 * @author jang
 */
public class NioTcpEngine extends BaseEngine {

	private static Logger logger = Logging.getLogger(NioTcpEngine.class);
	// TODO: move following constants to settings
	private static final int READ_BUFFER_SIZE = 2048;
	private static final int NUM_WORKERS = 3;
	private static final int READ_QUEUE_MAX_SIZE = Integer.MAX_VALUE;
	private Selector selector;
	private ServerSocketChannel myServerSocketChannel;
	private boolean isRunning;
	private Map<String, Queue<DataFuture>> pendingWrites; // <connector id, data queue>
	private BlockingQueue<ReadBean> pendingReads;
	// worker threads
	private ExecutorService executorService;
	// convenience maps
	private Map<String, SocketChannel> connectorToChannelMap; // <connector id, socket channel>
	private Map<SocketChannel, String> channelToConnectorMap; // <socket channel, connector id>
	private ByteBuffer readBuffer;

	public NioTcpEngine(EngineConfiguration configuration) {
		super(configuration);
	}

	@Override
	public void startEngine() throws WebSocketException {
		try {
			pendingWrites = new ConcurrentHashMap<String, Queue<DataFuture>>();
			pendingReads = new LinkedBlockingQueue<ReadBean>(READ_QUEUE_MAX_SIZE);
			connectorToChannelMap = new ConcurrentHashMap<String, SocketChannel>();
			channelToConnectorMap = new ConcurrentHashMap<SocketChannel, String>();
			readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
			selector = Selector.open();
			myServerSocketChannel = ServerSocketChannel.open();
			myServerSocketChannel.configureBlocking(false);
			ServerSocket socket = myServerSocketChannel.socket();
			socket.bind(new InetSocketAddress(getConfiguration().getPort()));
			myServerSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			isRunning = true;

			// start worker threads
			executorService = Executors.newFixedThreadPool(NUM_WORKERS);
			for (int idx = 0; idx < NUM_WORKERS; idx++) {
				// give an index to each worker thread
				executorService.submit(new ReadWorker(idx));
			}

			// start selector thread
			new Thread(new SelectorThread()).start();
		} catch (IOException e) {
			throw new WebSocketException(e.getMessage(), e);
		}
	}

	@Override
	public void stopEngine(CloseReason closeReason) throws WebSocketException {
		super.stopEngine(closeReason);
		if (selector != null) {
			try {
				isRunning = false;
				selector.wakeup();
				myServerSocketChannel.close();
				selector.close();
				pendingWrites.clear();
				// mPendingReads.notifyAll();
				pendingReads.clear();
				executorService.shutdown();
				logger.info("NIO engine stopped.");
			} catch (IOException ex) {
				throw new WebSocketException(ex.getMessage(), ex);
			}
		}
	}

	public void send(String connectorId, DataFuture future) {
		if (pendingWrites.containsKey(connectorId)) {
			pendingWrites.get(connectorId).add(future);
			// Wake up waiting selector.
			selector.wakeup();
		} else {
			logger.debug("Discarding packet for unattached socket channel, remote client is: " + getConnectors().getById(connectorId).getRemoteHost());
		}
	}

	@Override
	public void connectorStopped(WebSocketConnector conn, CloseReason closeReason) {
		Queue<DataFuture> queue = pendingWrites.remove(conn.getId());
		if (queue != null) {
			queue.clear();
		}

		if (connectorToChannelMap.containsKey(conn.getId())) {
			SocketChannel channel = connectorToChannelMap.remove(conn.getId());
			try {
				channel.socket().close();
				channel.close();
			} catch (Exception ex) {
				logger.error(ex.getClass().getSimpleName() + " (connectorStopped): " + ex.getMessage());
			}
			channelToConnectorMap.remove(channel);
		}

		super.connectorStopped(conn, closeReason);
	}

	/**
	 * Socket operations are permitted only via this thread. Strange behaviour will occur if anything is done to the socket outside of this thread.
	 */
	private class SelectorThread implements Runnable {

		@Override
		public void run() {
			Thread.currentThread().setName("jWebSocket NIO-Engine SelectorThread");

			engineStarted();
			while (isRunning && selector.isOpen()) {
				// check if there's anything to write to any of the clients
				for (String id : pendingWrites.keySet()) {
					if (!pendingWrites.get(id).isEmpty()) {
						connectorToChannelMap.get(id).keyFor(selector).interestOps(SelectionKey.OP_WRITE);
					}
				}

				try {
					// Waits for 500ms for any data from connected clients or for new client connections.
					// We could have indefinite wait (selector.wait()), but it is good to check for 'running' variable
					// fairly often.
					if (selector.select(500) > 0 && isRunning) {
						Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
						while (keys.hasNext()) {
							SelectionKey skey = keys.next();
							keys.remove();
							if (skey.isValid()) {
								try {
									if (skey.isAcceptable()) {
										// accept new client connection
										accept(skey);
									} else {
										if (skey.isReadable()) {
											read(skey);
										}
										if (skey.isWritable()) {
											write(skey);
										}
									}
								} catch (CancelledKeyException lCKEx) {
									// ignore, key was cancelled an instant after isValid() returned true,
									// most probably the client disconnected just at the wrong moment
								}
							}
						}
					} else {
						// nothing happened, continue looping ...
						logger.trace("No data on listen port in 500ms timeout ...");
					}
				} catch (Exception lEx) {
					// something happened during socket operation (select, read or write), just log it
					logger.error("Error during socket operation", lEx);
				}
			}
			engineStopped();
		}
	}

	// this must be called only from selector thread
	private void write(SelectionKey skey) throws IOException {
		SocketChannel socketChannel = (SocketChannel) skey.channel();
		Queue<DataFuture> lQueue = pendingWrites.get(channelToConnectorMap.get(socketChannel));
		while (!lQueue.isEmpty()) {
			DataFuture future = lQueue.peek();
			try {
				ByteBuffer byteBuffer = future.getData();
				socketChannel.write(byteBuffer);
				if (byteBuffer.remaining() > 0) {
					// socket's buffer is full, stop writing for now and leave the remaining
					// data in queue for another round of writing
					break;
				}
			} catch (IOException ex) {
				future.setFailure(ex);
				throw ex;
			}

			future.setSuccess();
			// remove the head element of the queue
			lQueue.poll();
		}

		if (lQueue.isEmpty()) {
			skey.interestOps(SelectionKey.OP_READ);
		}
	}

	// this must be called only from selector thread
	private void accept(SelectionKey skey) throws IOException {
		try {
			SocketChannel lSocketChannel = ((ServerSocketChannel) skey.channel()).accept();
			lSocketChannel.configureBlocking(false);
			lSocketChannel.register(selector, SelectionKey.OP_READ);

			WebSocketConnector lConnector = new NioTcpConnector(this, lSocketChannel.socket().getInetAddress(), lSocketChannel.socket().getPort());
			getConnectors().add(lConnector);
			pendingWrites.put(lConnector.getId(), new ConcurrentLinkedQueue<DataFuture>());
			connectorToChannelMap.put(lConnector.getId(), lSocketChannel);
			channelToConnectorMap.put(lSocketChannel, lConnector.getId());
			logger.info("NIO Client accepted - remote ip: " + lConnector.getRemoteHost());
		} catch (IOException e) {
			logger.warn("Could not accept new client connection");
			throw e;
		}
	}

	// this must be called only from selector thread
	private void read(SelectionKey skey) throws IOException {
		SocketChannel lSocketChannel = (SocketChannel) skey.channel();
		readBuffer.clear();

		int numRead;
		try {
			numRead = lSocketChannel.read(readBuffer);
		} catch (IOException lIOEx) {
			// remote client probably disconnected uncleanly ?
			clientDisconnect(skey);
			return;
		}

		if (numRead == -1) {
			// read channel closed, connection has ended
			clientDisconnect(skey);
			return;
		}

		if (numRead > 0 && channelToConnectorMap.containsKey(lSocketChannel)) {
			String lConnectorId = channelToConnectorMap.get(lSocketChannel);
			ReadBean lBean = new ReadBean();
			lBean.connectorId = lConnectorId;
			lBean.data = Arrays.copyOf(readBuffer.array(), numRead);
			boolean lAccepted = pendingReads.offer(lBean);
			if (!lAccepted) {
				// Read queue is full, discard the packet.
				// This may happen under continuous heavy load (plugins cannot process packets in time) or
				// if all worker threads are locked up (perhaps a rogue plugin is blocking packet processing).
				logger.warn("Engine read queue is full, discarding incoming packet");
			}
		}
	}

	private void clientDisconnect(SelectionKey aKey) throws IOException {
		clientDisconnect(aKey, CloseReason.CLIENT);
	}

	private void clientDisconnect(SelectionKey aKey, CloseReason aReason) throws IOException {
		SocketChannel channel = (SocketChannel) aKey.channel();
		aKey.cancel();
		aKey.channel().close();
		if (channelToConnectorMap.containsKey(channel)) {
			String lId = channelToConnectorMap.remove(channel);
			if (lId != null) {
				connectorToChannelMap.remove(lId);
				connectorStopped(getConnectors().getById(lId), aReason);
			}
		}
	}

	private void clientDisconnect(WebSocketConnector connector) throws IOException {
		clientDisconnect(connector, CloseReason.CLIENT);
	}

	private void clientDisconnect(WebSocketConnector connector, CloseReason reason) throws IOException {
		if (connectorToChannelMap.containsKey(connector.getId())) {
			clientDisconnect(connectorToChannelMap.get(connector.getId()).keyFor(selector), reason);
		}
	}

	private class ReadBean {

		String connectorId;
		byte[] data;
	}

	private class ReadWorker implements Runnable {

		int myId = -1;

		public ReadWorker(int myId) {
			super();
			this.myId = myId;
		}

		@Override
		public void run() {
			Thread.currentThread().setName("jWebSocket NIO-Engine ReadWorker " + this.myId);

			while (isRunning) {
				try {
					final ReadBean readBean = pendingReads.poll(200, TimeUnit.MILLISECONDS);
					if (readBean != null) {
						if (getConnectors().containsById(readBean.connectorId)) {
							final NioTcpConnector lConnector = (NioTcpConnector) getConnectors().getById(readBean.connectorId);
							if (lConnector.getWorkerId() > -1 && lConnector.getWorkerId() != hashCode()) {
								// another worker is right in the middle of packet processing for this connector
								lConnector.setDelayedPacketNotifier(new DelayedPacketNotifier() {

									@Override
									public void handleDelayedPacket() throws IOException {
										doRead(lConnector, readBean);
									}
								});
							} else {
								doRead(lConnector, readBean);
							}
						} else {
							// connector was already closed ...
							logger.debug("Discarding incoming packet, because there's no connector to process it");
						}
					}
				} catch (InterruptedException e) {
					// Ignore this exception -- waiting was interrupted, probably during engine stop ...
					break;
				} catch (Exception e) {
					// uncaught exception during packet processing - kill the worker (todo: think about worker restart)
					logger.error("Unexpected exception during incoming packet processing", e);
					break;
				}
			}
		}

		private void doRead(NioTcpConnector connector, ReadBean readBean) throws IOException {
			connector.setWorkerId(hashCode());
			if (connector.isAfterHandshake()) {
				boolean lIsHixie = connector.isHixie();
				if (lIsHixie) {
					readHixie(readBean.data, connector);
				} else {
					// assume that #02 and #03 are the same regarding packet processing
					readHybi(readBean.data, connector);
				}
			} else {
				// todo: consider ssl connections
				Map headers = WebSocketHandshake.parseC2SRequest(readBean.data, false);
				byte[] response = WebSocketHandshake.generateS2CResponse(headers);
				RequestHeader reqHeader = EngineUtils.validateC2SRequest(headers, logger);
				if (response == null || reqHeader == null) {
					if (logger.isDebugEnabled()) {
						logger.warn("TCPEngine detected illegal handshake.");
					}
					// disconnect the client
					clientDisconnect(connector);
				}

				send(connector.getId(), new DataFuture(connector, ByteBuffer.wrap(response)));
				int timeout = reqHeader.getTimeout(getConfiguration().getTimeout());
				if (timeout > 0) {
					connectorToChannelMap.get(readBean.connectorId).socket().setSoTimeout(timeout);
				}
				connector.handshakeValidated();
				connector.setHeader(reqHeader);
				connector.startConnector();
			}
			connector.setWorkerId(-1);
		}
	}

	/**
	 * One message may consist of one or more (fragmented message) protocol packets. The spec is currently unclear whether control packets (ping, pong, close) may be intermingled with fragmented packets of another message. For now I've decided to not implement such packets 'swapping', and therefore reading fails miserably if a client sends control packets during fragmented message read. TODO:
	 * follow next spec drafts and add support for control packets inside fragmented message if needed.
	 * <p>
	 * Structure of packets conforms to the following scheme (copied from spec):
	 * </p>
	 * 
	 * <pre>
	 *  0                   1                   2                   3
	 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-------+-+-------------+-------------------------------+
	 * |M|R|R|R| opcode|R| Payload len |    Extended payload length    |
	 * |O|S|S|S|  (4)  |S|     (7)     |             (16/63)           |
	 * |R|V|V|V|       |V|             |   (if payload len==126/127)   |
	 * |E|1|2|3|       |4|             |                               |
	 * +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
	 * |     Extended payload length continued, if payload len == 127  |
	 * + - - - - - - - - - - - - - - - +-------------------------------+
	 * |                               |         Extension data        |
	 * +-------------------------------+ - - - - - - - - - - - - - - - +
	 * :                                                               :
	 * +---------------------------------------------------------------+
	 * :                       Application data                        :
	 * +---------------------------------------------------------------+
	 * </pre>
	 * 
	 * RSVx bits are ignored (reserved for future use). TODO: add support for extension data, when extensions will be defined in the specs.
	 * 
	 * <p>
	 * Read section 4.2 of the spec for detailed explanation.
	 * </p>
	 */
	private void readHybi(byte[] buffer, NioTcpConnector connector) throws IOException {
		try {
			if (connector.isPacketBufferEmpty()) {
				// begin normal packet read
				int flags = buffer[0];
				// determine fragmentation
				boolean fragmented = (0x01 & flags) == 0x01;
				// shift 4 bits to skip the first bit and three RSVx bits
				int lOpcode = flags >> 4;
				WebSocketFrameType frameType = WebSocketProtocolAbstraction.opcodeToFrameType(connector.getVersion(), lOpcode);

				int payloadStartIndex = 2;

				if (frameType == WebSocketFrameType.INVALID) {
					// Could not determine packet type, ignore the packet.
					// Maybe we need a setting to decide, if such packets should abort the connection?
					logger.trace("Dropping packet with unknown type: " + lOpcode);
				} else {
					connector.setPacketType(frameType);
					// Ignore first bit. Payload length is next seven bits, unless its value is greater than 125.
					long payLoadLen = buffer[1] >> 1;
					if (payLoadLen == 126) {
						// following two bytes are acutal payload length (16-bit unsigned integer)
						payLoadLen = (buffer[2] << 8) + buffer[3];
						payloadStartIndex = 4;
					} else if (payLoadLen == 127) {
						// Following eight bytes are actual payload length (64-bit unsigned integer),
						// but that's ridiculously big number for an array size - in fact, such big arrays are
						// unsupported in Java. Feel free to make an array of int arrays to support that. I won't
						// do it, because it's too much work and it's just plain stupid for clients to send
						// such giant packets. So, if payload size is greater than Integer.MAX_VALUE, client will
						// be disconnected.
						payLoadLen = ((long) buffer[2] << 56) + ((long) (buffer[3] & 255) << 48) + ((long) (buffer[4] & 255) << 40) + ((long) (buffer[5] & 255) << 32) + ((long) (buffer[6] & 255) << 24) + ((buffer[7] & 255) << 16) + ((buffer[8] & 255) << 8) + ((buffer[9] & 255));
						if (payLoadLen > Integer.MAX_VALUE) {
							clientDisconnect(connector);
							return;
						}
						payloadStartIndex = 10;
					}

					if (payLoadLen > 0) {
						connector.setPayloadLength((int) payLoadLen);
						connector.extendPacketBuffer(buffer, payloadStartIndex, buffer.length - payloadStartIndex);
					}
				}

				if (frameType == WebSocketFrameType.PING) {
					// As per spec, server must respond to PING with PONG (maybe
					// this should be handled higher up in the hierarchy?)
					WebSocketPacket pong = new RawPacket(connector.getPacketBuffer());
					pong.setFrameType(WebSocketFrameType.PONG);
					connector.sendPacket(pong);
				} else if (frameType == WebSocketFrameType.CLOSE) {
					// As per spec, server must respond to CLOSE with acknowledgment CLOSE (maybe
					// this should be handled higher up in the hierarchy?)
					WebSocketPacket closePacket = new RawPacket(connector.getPacketBuffer());
					closePacket.setFrameType(WebSocketFrameType.CLOSE);
					connector.sendPacket(closePacket);
					clientDisconnect(connector, CloseReason.CLIENT);
				}
			} else {
				connector.extendPacketBuffer(buffer, 0, buffer.length);
			}

			if (connector.isPacketBufferFull()) {
				// Packet was read, pass it forward.
				connector.flushPacketBuffer();
			}
		} catch (Exception e) {
			logger.error("(other) " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
			clientDisconnect(connector, CloseReason.SERVER);
		}
	}

	private void readHixie(byte[] buffer, NioTcpConnector connector) throws IOException {
		try {
			int start = 0;
			if (connector.isPacketBufferEmpty() && buffer[0] == 0x00) {
				// start of packet
				start = 1;
			}

			boolean stop = false;
			int count = buffer.length;
			for (int i = start; i < buffer.length; i++) {
				if (buffer[i] == (byte) 0xFF) {
					// end of packet
					count = i - start;
					stop = true;
					break;
				}
			}

			if (start + count > buffer.length) {
				// ignore -> broken packet (perhaps client disconnected in middle of sending
			} else {
				if (connector.isPacketBufferEmpty() && buffer.length == 1) {
					connector.extendPacketBuffer(buffer, 0, 0);
				} else {
					connector.extendPacketBuffer(buffer, start, count);
				}
			}

			if (stop) {
				connector.flushPacketBuffer();
			}
		} catch (Exception e) {
			logger.error("Error while processing incoming packet", e);
			clientDisconnect(connector, CloseReason.SERVER);
		}
	}
}
