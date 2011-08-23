//	---------------------------------------------------------------------------
//	jWebSocket - Basic PlugIn Class
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
package net.ion.websocket.common.api;

import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;

/**
 *
 * @author aschulze
 */
public interface WebSocketPlugIn {

	// TODO: a plug-in should have a name and an id to be uniquely identified in the chain!

	/**
	 * is called by the server when the engine has been started.
	 * @param engine
	 */
	void engineStarted(WebSocketEngine engine);

	/**
	 * is called by the server when the engine has been stopped.
	 * @param engine
	 */
	void engineStopped(WebSocketEngine engine);

	/**
	 *
	 * @param connector
	 */
	void connectorStarted(WebSocketConnector connector);

	/**
	 *
	 * @param response
	 * @param connector
	 * @param packet
	 */
	void processPacket(PlugInResponse response, WebSocketConnector connector, WebSocketPacket packet);

	/**
	 *
	 * @param connector
	 * @param creason
	 */
	void connectorStopped(WebSocketConnector connector, CloseReason creason);

	/**
	 *
	 * @param pluginChain
	 */
	void setPlugInChain(WebSocketPlugInChain pluginChain);

	/**
	 * @return the plugInChain
	 */
	WebSocketPlugInChain getPlugInChain();



	/**
	 *
	 * @param key
	 */
	void removeSetting(String key);

	/**
	 *
	 */
	void clearSettings();

	public <T> T getAttribute(String id, Class<T> clz)  ;

	public <T> void putAttribute(String id, Object obj) ;

	WebSocketServer getServer();
}
