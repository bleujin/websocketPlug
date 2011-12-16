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
import net.ion.websocket.common.async.IOFuture;
import net.ion.websocket.common.connector.BaseConnector;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.server.engine.netty.NettyEngineHandler;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame;

/**
 * Netty based implementation of the {@code BaseConnector}.
 * 
 * @author puran
 * @version $Id: NettyConnector.java,v 1.6 2011/12/15 06:30:24 bleujin Exp $
 */
public class NettyConnector extends BaseConnector {

	private static Logger log = Logging.getLogger(NettyConnector.class);

	private NettyEngineHandler handler = null;

	/**
	 * The private constructor, netty connector objects are created using static factory method: <tt>getNettyConnector({@code WebSocketEngine}, {@code ChannelHandlerContext})</tt>
	 * 
	 * @param theEngine
	 *            the websocket engine object
	 * @param theHandlerContext
	 *            the netty engine handler context
	 */
	public NettyConnector(WebSocketEngine theEngine, NettyEngineHandler theHandler) {
		super(theEngine);
		this.handler = theHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startConnector() {
		log.debug("Starting Netty connector...");
		// DO CONNECTOR SPECIFIC INITIALIZATION HERE....
		log.info("Started Netty connector on port" + getRemotePort() + ".");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stopConnector(CloseReason aCloseReason) {
		log.debug("Stopping Netty connector (" + aCloseReason.name() + ").....");

		getEngine().connectorStopped(this, aCloseReason);

		if (handler.getChannelHandlerContext().getChannel().isConnected() && getEngine().isAlive()) {
			handler.getChannelHandlerContext().getChannel().close();
		}
		if (log.isInfoEnabled()) {
			log.info("Stopped Netty connector (" + aCloseReason.name() + ") on port " + getRemotePort());
		}
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
	public void processPacket(WebSocketPacket aDataPacket) {
		// forward the data packet to the engine
		getEngine().processPacket(this, aDataPacket);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendPacket(WebSocketPacket aDataPacket) {
		if (handler.getChannelHandlerContext().getChannel().isConnected() && getEngine().isAlive()) {
			handler.getChannelHandlerContext().getChannel().write(new DefaultWebSocketFrame(aDataPacket.getString()));
		}
	}

	public IOFuture sendPacketAsync(WebSocketPacket aDataPacket) {
		if (handler.getChannelHandlerContext().getChannel().isConnected() && getEngine().isAlive()) {
			ChannelFuture internalFuture = handler.getChannelHandlerContext().getChannel().write(new DefaultWebSocketFrame(aDataPacket.getString()));
			return new NIOFuture(this, internalFuture);
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String res = getRemoteHost().getHostAddress() + ":" + getRemotePort();
		// TODO: don't hard code. At least use JWebSocketConstants field here.
		String username = getString("org.jwebsocket.plugins.system.username");
		if (username != null) {
			res += " (" + username + ")";
		}
		return res;
	}
}
