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
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.async.IOFuture;
import net.ion.websocket.common.connector.BaseConnector;
import net.ion.websocket.common.kit.RawPacket;
import net.ion.websocket.common.kit.WebSocketProtocolAbstraction;
import net.ion.websocket.common.logging.Logging;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import net.ion.websocket.common.kit.WebSocketFrameType;

public class NioTcpConnector extends BaseConnector {

	private static Logger mLog = Logging.getLogger(NioTcpConnector.class);
	private InetAddress remoteAddress;
	private int remotePort;
	private boolean isAfterHandshake;
	private byte[] packetBuffer;
	private int payloadLength = -1;
	private int bufferPosition = -1;
	private WebSocketFrameType frameType = WebSocketFrameType.INVALID;
	private int workerId;
	private DelayedPacketNotifier delayedPacketNotifier;

	public NioTcpConnector(NioTcpEngine engine, InetAddress remoteAddress, int remotePort) {
		super(engine);

		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
		this.isAfterHandshake = false;
		this.workerId = -1;
	}

	@Override
	public void sendPacket(WebSocketPacket packet) {
		sendPacketAsync(packet); // nio engine works asynchronously by default
	}

	@Override
	public IOFuture sendPacketAsync(WebSocketPacket packet) {
		byte[] protocolPacket;
		if (isHixie()) {
			protocolPacket = new byte[packet.getByteArray().length + 2];
			protocolPacket[0] = 0x00;
			System.arraycopy(packet.getByteArray(), 0, protocolPacket, 1, packet.getByteArray().length);
			protocolPacket[protocolPacket.length - 1] = (byte) 0xFF;
		} else {
			protocolPacket = WebSocketProtocolAbstraction.rawToProtocolPacket(getVersion(), packet);
		}

		DataFuture future = new DataFuture(this, ByteBuffer.wrap(protocolPacket));
		((NioTcpEngine) getEngine()).send(getId(), future);
		return future;
	}

	@Override
	public String getId() {
		return String.valueOf(hashCode());
	}

	@Override
	public String generateUID() {
		return remoteAddress.getHostAddress() + '@' + remotePort;
	}

	@Override
	public InetAddress getRemoteHost() {
		return remoteAddress;
	}

	@Override
	public int getRemotePort() {
		return remotePort;
	}

	public void handshakeValidated() {
		isAfterHandshake = true;
	}

	public boolean isAfterHandshake() {
		return isAfterHandshake;
	}

	public boolean isPacketBufferEmpty() {
		return packetBuffer == null;
	}

	public void extendPacketBuffer(byte[] newData, int start, int count) throws IOException {
		if (payloadLength == -1) {
			// packet buffer grows with new data
			if (packetBuffer == null) {
				packetBuffer = new byte[count];
				if (count > 0) {
					System.arraycopy(newData, start, packetBuffer, 0, count);
				}
			} else {
				byte[] newBuffer = new byte[packetBuffer.length + count];
				System.arraycopy(packetBuffer, 0, newBuffer, 0, packetBuffer.length);
				System.arraycopy(newData, start, newBuffer, packetBuffer.length, count);
				packetBuffer = newBuffer;
			}
		} else {
			// packet buffer was already created with the correct length
			System.arraycopy(newData, start, packetBuffer, bufferPosition, count);
			bufferPosition += count;
		}
		notifyWorker();
	}

	public byte[] getPacketBuffer() {
		return packetBuffer;
	}

	public void flushPacketBuffer() {
		byte[] copyBuffer = new byte[packetBuffer.length];
		System.arraycopy(packetBuffer, 0, copyBuffer, 0, packetBuffer.length);

		RawPacket rawPacket = new RawPacket(copyBuffer);
		if (frameType != WebSocketFrameType.INVALID) {
			rawPacket.setFrameType(frameType);
		}
		try {
			getEngine().processPacket(this, rawPacket);

			packetBuffer = null;
			payloadLength = -1;
			frameType = WebSocketFrameType.INVALID;
			workerId = -1;
			notifyWorker();
		} catch (Exception e) {
			mLog.error(e.getClass().getSimpleName() + " in processPacket of connector " + getClass().getSimpleName(), e);
		}
	}

	public void setPayloadLength(int length) {
		payloadLength = length;
		packetBuffer = new byte[length];
		bufferPosition = 0;
	}

	public boolean isPacketBufferFull() {
		return bufferPosition >= payloadLength;
	}

	public void setPacketType(WebSocketFrameType aPacketType) {
		this.frameType = aPacketType;
	}

	public int getWorkerId() {
		return workerId;
	}

	public void setWorkerId(int workerId) {
		this.workerId = workerId;
	}

	public DelayedPacketNotifier getDelayedPacketNotifier() {
		return delayedPacketNotifier;
	}

	public void setDelayedPacketNotifier(DelayedPacketNotifier delayedPacketNotifier) {
		this.delayedPacketNotifier = delayedPacketNotifier;
	}

	private void notifyWorker() throws IOException {
		if (delayedPacketNotifier != null) {
			delayedPacketNotifier.handleDelayedPacket();
			delayedPacketNotifier = null;
		}
	}
}
