//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
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
package net.ion.websocket.server.engine.netty.connector;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.connector.BaseConnector;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.server.engine.netty.NettyEngineHandler;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame;

/**
 * Netty based implementation of the {@code BaseConnector}.
 * 
 * @author puran
 * @version $Id: NettyConnector.java,v 1.5 2011/07/15 07:14:12 bleujin Exp $
 */
public class NettyConnector extends BaseConnector {

	private static Logger log = Logging.getLogger(NettyConnector.class);

	private NettyEngineHandler handler = null;

	/**
	 * The private constructor, netty connector objects are created using static factory method: <tt>getNettyConnector({@code WebSocketEngine}, {@code ChannelHandlerContext})</tt>
	 * 
	 * @param engine
	 *            the websocket engine object
	 * @param theHandlerContext
	 *            the netty engine handler context
	 */
	public NettyConnector(WebSocketEngine engine, NettyEngineHandler handler) {
		super(engine);
		this.handler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startConnector() {
		// DO CONNECTOR SPECIFIC INITIALIZATION HERE....
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopConnector(CloseReason creason) {
		getEngine().connectorStopped(this, creason);
		handler.getChannelHandlerContext().getChannel().close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRemotePort() {
		InetSocketAddress address = (InetSocketAddress) handler.getChannelHandlerContext().getChannel().getRemoteAddress();
		return address.getPort();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InetAddress getRemoteHost() {
		InetSocketAddress address = (InetSocketAddress) handler.getChannelHandlerContext().getChannel().getRemoteAddress();
		return address.getAddress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processPacket(WebSocketPacket packet) {
		// forward the data packet to the engine
		getEngine().processPacket(this, packet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendPacket(WebSocketPacket packet) {
		if (handler.getChannelHandlerContext().getChannel().isConnected() && getEngine().isAlive()) {
			handler.getChannelHandlerContext().getChannel().write(new DefaultWebSocketFrame(packet.getUTF8()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getRemoteHost().getHostAddress()).append(":").append(getRemotePort());
		// TODO: don't hard code. At least use JWebSocketConstants field here.
		String userName = getUsername() ;
		if (userName != null) {
			result.append(" (" + userName + ")");
		}
		return result.toString();
	}
}
