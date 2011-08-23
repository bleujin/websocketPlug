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
package net.ion.websocket.common.kit;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.listener.WhenEvent;
import net.ion.websocket.common.server.BaseServer;


/**
 *
 * @author aschulze
 */
public class WebSocketServerEvent {

	private final WebSocketConnector source ;
	private final PlugInResponse response ;
	private final WebSocketServer server ;
	private final WebSocketPacket packet ;
	private WhenEvent when;

	private WebSocketServerEvent(WebSocketConnector source, PlugInResponse response, WebSocketServer server) {
		this(source, response, server, WebSocketPacket.BLANK) ;
	}
	
	private WebSocketServerEvent(WebSocketConnector source, PlugInResponse response, WebSocketServer server, WebSocketPacket packet) {
		this.source = source;
		this.response = response ;
		this.server = server;
		this.packet = packet ;
		this.when = WhenEvent.UNKNOWN ;
	}

	public static WebSocketServerEvent create(WebSocketConnector connector, PlugInResponse response, WebSocketServer server) {
		return new WebSocketServerEvent(connector, response, server);
	}
	
	public static WebSocketServerEvent create(WebSocketConnector connector, BaseServer server) {
		return create(connector, PlugInResponse.BLANK, server);
	}

	public static WebSocketServerEvent create(WebSocketConnector connector, PlugInResponse response, WebSocketServer server, WebSocketPacket packet) {
		return new WebSocketServerEvent(connector, response, server, packet);
	}

	public WebSocketServerEvent setWhenEventType(WhenEvent when){
		this.when = when ;
		return this ;
	}

	public String getSessionId() {
		return source.getSession().getSessionId();
	}

	/**
	 * @return the session
	 */
	public WebSocketSession getSession() {
		return source.getSession();
	}

	/**
	 * @return the server
	 */
	public WebSocketServer getServer() {
		return server;
	}

	/**
	 * @return the connector
	 */
	public WebSocketConnector getSourceConnector() {
		return source;
	}

	/**
	 *
	 * @param packet
	 */
	public WebSocketPacket getPacket() {
		return packet;
	}

	public WhenEvent getWhen() {
		return when ;
	}

	public void handleEvent(WebSocketServerListener handler) {
		getWhen().handle(handler, this) ;
	}

}
