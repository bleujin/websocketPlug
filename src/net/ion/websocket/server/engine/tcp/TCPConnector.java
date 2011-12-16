//	---------------------------------------------------------------------------
//	jWebSocket - TCP Connector
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
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
package net.ion.websocket.server.engine.tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

import net.ion.framework.util.IOUtil;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.async.IOFuture;
import net.ion.websocket.common.connector.BaseConnector;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.RawPacket;
import net.ion.websocket.common.kit.WebSocketFrameType;
import net.ion.websocket.common.kit.WebSocketProtocolAbstraction;
import net.ion.websocket.common.logging.Logging;

/**
 * Implementation of the jWebSocket TCP socket connector.
 * 
 * @author aschulze
 * @author jang
 */
public class TCPConnector extends BaseConnector {

	private static Logger logger = Logging.getLogger(TCPConnector.class);
	private InputStream input = null;
	private OutputStream output = null;
	private Socket clientSocket = null;
	public static final String TCP_LOG = "TCP";
	public static final String SSL_LOG = "SSL";
	private String logInfo = TCP_LOG;
	private boolean isRunning = false;
	private CloseReason closeReason = CloseReason.TIMEOUT;

	/**
	 * creates a new TCP connector for the passed engine using the passed client socket. Usually connectors are instantiated by their engine only, not by the application.
	 * 
	 * @param engine
	 * @param clientSocket
	 */
	public TCPConnector(WebSocketEngine engine, Socket clientSocket) {
		super(engine);
		this.clientSocket = clientSocket;
		setSSL(clientSocket instanceof SSLSocket);
		logInfo = isSSL() ? SSL_LOG : TCP_LOG;
		try {
			input = clientSocket.getInputStream();
			output = clientSocket.getOutputStream();
		} catch (Exception lEx) {
			logger.error(lEx.getClass().getSimpleName() + " instantiating " + getClass().getSimpleName() + ": " + lEx.getMessage());
		}
	}

	@Override
	public void startConnector() {
		int port = -1;
		int timeout = -1;
		try {
			port = clientSocket.getPort();
			timeout = clientSocket.getSoTimeout();
		} catch (Exception lEx) {
		}
		String nodeStr = getNodeId();
		if (nodeStr != null) {
			nodeStr = " (unid: " + nodeStr + ")";
		} else {
			nodeStr = "";
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Starting " + logInfo + " connector" + nodeStr + " on port " + port + " with timeout " + (timeout > 0 ? timeout + "ms" : "infinite") + "");
		}
		ClientProcessor processor = new ClientProcessor(this);
		Thread clientThread = new Thread(processor);
		clientThread.start();
		if (logger.isInfoEnabled()) {
			logger.info("Started " + logInfo + " connector" + nodeStr + " on port " + port + " with timeout " + (timeout > 0 ? timeout + "ms" : "infinite") + "");
		}
	}

	@Override
	public void stopConnector(CloseReason closeReason) {
		logger.debug("Stopping " + logInfo + " connector (" + closeReason.name() + ")...");
		int portNo = clientSocket.getPort();
		this.closeReason = closeReason;
		isRunning = false;

		if (!isHixie()) {
			// Hybi specs demand that client must be notified with CLOSE control message before disconnect
			WebSocketPacket lClose = new RawPacket("BYE");
			lClose.setFrameType(WebSocketFrameType.CLOSE);
			sendPacket(lClose);
		}
		
		IOUtil.closeQuietly(input) ;
		logger.info("Stopped " + logInfo + " connector (" + closeReason.name() + ") on port " + portNo + ".");
	}

	@Override
	public void processPacket(WebSocketPacket dataPacket) {
		// forward the data packet to the engine
		getEngine().processPacket(this, dataPacket);
	}

	@Override
	public synchronized void sendPacket(WebSocketPacket dataPacket) {
		try {
			if (clientSocket.isConnected()) {
				if (isHixie()) {
					sendHixie(dataPacket);
				} else {
					sendHybi(getVersion(), dataPacket);
				}
				output.flush();
			} else {
				logger.error("Trying to send to closed connection: " + getId() + ", " + dataPacket.getUTF8());
			}
		} catch (IOException lEx) {
			logger.error(lEx.getClass().getSimpleName() + " sending data packet: " + lEx.getMessage() + ", data: " + dataPacket.getUTF8());
		}
	}

	@Override
	public IOFuture sendPacketAsync(WebSocketPacket dataPacket) {
		throw new UnsupportedOperationException("Underlying connector:" + getClass().getName() + " doesn't support asynchronous send operation");
	}

	private class ClientProcessor implements Runnable {

		private WebSocketConnector connector = null;

		/**
		 * Creates the new socket listener thread for this connector.
		 * 
		 * @param connector
		 */
		public ClientProcessor(WebSocketConnector connector) {
			this.connector = connector;
		}

		@Override
		public void run() {
			WebSocketEngine engine = getEngine();
			ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
			Thread.currentThread().setName("WebSocket TCP-Connector " + getId());
			try {
				// start client listener loop
				isRunning = true;

				// call connectorStarted method of engine
				engine.connectorStarted(connector);

				if (isHixie()) {
					readHixie(outputBuffer, engine);
				} else {
					readHybi(getVersion(), outputBuffer, engine);
				}

				// call client stopped method of engine
				// (e.g. to release client from streams)
				engine.connectorStopped(connector, closeReason);

				input.close();
				output.close();
				clientSocket.close();

			} catch (Exception ex) {
				// ignore this exception for now
				logger.error("(close) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
		}

		private void readHixie(ByteArrayOutputStream buffer, WebSocketEngine engine) throws IOException {
			while (isRunning) {
				try {
					int in = input.read();
					// start of frame
					if (in == 0x00) {
						buffer.reset();
						// end of frame
					} else if (in == 0xFF) {
						RawPacket lPacket = new RawPacket(buffer.toByteArray());
						try {
							engine.processPacket(connector, lPacket);
						} catch (Exception lEx) {
							logger.error(lEx.getClass().getSimpleName() + " in processPacket of connector " + connector.getClass().getSimpleName() + ": " + lEx.getMessage());
						}
						buffer.reset();
					} else if (in < 0) {
						closeReason = CloseReason.CLIENT;
						isRunning = false;
						// any other byte within or outside a frame
					} else {
						buffer.write(in);
					}
				} catch (SocketTimeoutException ex) {
					logger.error("(timeout) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					closeReason = CloseReason.TIMEOUT;
					isRunning = false;
				} catch (Exception ex) {
					logger.error("(other) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					closeReason = CloseReason.SERVER;
					isRunning = false;
				}
			}
		}

		private void readHybi(int version, ByteArrayOutputStream buffer, WebSocketEngine engine) throws IOException {
			WebSocketFrameType frameType;
			// utilize data input stream, because it has convenient methods for reading
			// signed/unsigned bytes, shorts, ints and longs
			DataInputStream dataInput = new DataInputStream(input);

			while (isRunning) {
				try {
					WebSocketPacket packet = WebSocketProtocolAbstraction.protocolToRawPacket(getVersion(), input);

					if (packet == null) {
						logger.debug("Processing client 'disconnect'...");
						closeReason = CloseReason.CLIENT;
						isRunning = false;
					} else if (WebSocketFrameType.TEXT.equals(packet.getFrameType())) {
						logger.debug("Processing 'text' frame...");
						engine.processPacket(connector, packet);
					} else if (WebSocketFrameType.PING.equals(packet.getFrameType())) {
						if (logger.isDebugEnabled()) {
							logger.debug("Processing 'ping' frame...");
						}
						WebSocketPacket pong = new RawPacket("");
						pong.setFrameType(WebSocketFrameType.PONG);
						sendPacket(pong);
					} else if (WebSocketFrameType.CLOSE.equals(packet.getFrameType())) {
						logger.debug("Processing 'close' frame...");
						closeReason = CloseReason.CLIENT;
						isRunning = false;
						// As per spec, server must respond to CLOSE with acknowledgment CLOSE (maybe
						// this should be handled higher up in the hierarchy?)
						WebSocketPacket closePacket = new RawPacket("");
						closePacket.setFrameType(WebSocketFrameType.CLOSE);
						sendPacket(closePacket);
					} else {
						logger.debug("Processing unknown frame type '" + packet.getFrameType() + "'...");
					}
				} catch (SocketTimeoutException ex) {
					logger.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
					closeReason = CloseReason.TIMEOUT;
					isRunning = false;
				} catch (Exception ex) {
					logger.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
					closeReason = CloseReason.SERVER;
					isRunning = false;
				}
			}
		}
	}

	@Override
	public String generateUID() {
		String uid = clientSocket.getInetAddress().getHostAddress() + "@" + clientSocket.getPort();
		return uid;
	}

	@Override
	public int getRemotePort() {
		return clientSocket.getPort();
	}

	@Override
	public InetAddress getRemoteHost() {
		return clientSocket.getInetAddress();
	}

	@Override
	public String toString() {
		// TODO: Show proper IPV6 if used
		String res = getId() + " (" + getRemoteHost().getHostAddress() + ":" + getRemotePort();
		String username = getUsername();
		if (username != null) {
			res += ", " + username;
		}
		return res + ")";
	}

	private void sendHixie(WebSocketPacket dataPacket) throws IOException {
		if (dataPacket.getFrameType() == WebSocketFrameType.BINARY) {
			// each packet is enclosed in 0xFF<length><data>
			// TODO: for future use! Not yet finally spec'd in IETF drafts!
			output.write(0xFF);
			byte[] ba = dataPacket.getByteArray();
			// TODO: implement multi byte length!
			output.write(ba.length);
			output.write(ba);
		} else {
			// each packet is enclosed in 0x00<data>0xFF
			output.write(0x00);
			output.write(dataPacket.getByteArray());
			output.write(0xFF);
		}
	}

	// TODO: implement fragmentation for packet sending
	private void sendHybi(int version, WebSocketPacket dataPacket) throws IOException {
		byte[] packetByte = WebSocketProtocolAbstraction.rawToProtocolPacket(version, dataPacket);
		output.write(packetByte);
	}
}
