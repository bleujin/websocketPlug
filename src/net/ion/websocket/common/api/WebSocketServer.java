//	---------------------------------------------------------------------------
//	jWebSocket - Server API
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.async.IOFuture;
import net.ion.websocket.common.config.ServerConfiguration;

import net.ion.websocket.common.kit.BroadcastOptions;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.common.server.IConnectorManager;
import net.ion.websocket.plugin.IMessagePacket;
import net.ion.websocket.server.ConnectorManager;
import net.ion.websocket.server.context.ServiceContext;

/**
 * Specifies the API of the jWebSocket server core and its capabilities. Each
 * server can be bound to one or multiple engines. Each engine can drive or more
 * servers above. The servers usually are not supposed to directly implement any
 * business logic - except for very small or special non token based
 * applications. For applications it is recommended to implement them in
 * plug-ins based on the token server.
 * 
 * @author aschulze
 * @version $Id: WebSocketServer.java,v 1.12 2011/12/15 06:30:21 bleujin Exp $
 */
public interface WebSocketServer {

	/**
	 * Starts the server and all underlying engines.
	 * 
	 * @throws WebSocketException
	 */
	void startServer() throws WebSocketException;

	/**
	 * States if at least one of the engines is still running.
	 * 
	 * @return Boolean state if at least one of the underlying engines is still
	 *         running.
	 */
	boolean isAlive();

	/**
	 * Stops the server and all underlying engines.
	 * 
	 * @throws WebSocketException
	 */
	void stopServer() throws WebSocketException;

	/**
	 * Adds a new engine to the server.
	 * 
	 * @param engine
	 *            to be added to the server.
	 */
	void setEngine(WebSocketEngine engine);

	
	ServerConfiguration getConfiguration()  ;

	
	WebSocketEngine getEngine() ;
	
	
	/**
	 * Is called from the underlying engine when the engine is started.
	 * 
	 * @param engine
	 */
	void engineStarted(WebSocketEngine engine);

	/**
	 * Is called from the underlying engine when the engine is stopped.
	 * 
	 * @param engine
	 */
	void engineStopped(WebSocketEngine engine);

	/**
	 * Notifies the application that a client connector has been started.
	 * 
	 * @param connecotr
	 *            the new connector that has been instantiated.
	 */
	void connectorStarted(WebSocketConnector connecotr);

	/**
	 * Notifies the application that a client connector has been stopped.
	 * 
	 * @param connector
	 * @param creason
	 */
	void connectorStopped(WebSocketConnector connector, CloseReason creason);

	/**
	 * Is called when the underlying engine received a packet from a connector.
	 * 
	 * @param engine
	 * @param connector
	 * @param packet
	 */
	void processPacket(WebSocketEngine engine, WebSocketConnector connector, WebSocketPacket packet);

	/**
	 * Sends a packet to a certain connector.
	 * 
	 * @param connector
	 * @param packet
	 */
	void sendPacket(WebSocketConnector connector, WebSocketPacket packet);

	IOFuture sendPacketAsync(WebSocketConnector aConnector, WebSocketPacket aDataPacket);

	/**
	 * Broadcasts a datapacket to all connectors.
	 * 
	 * @param source
	 * @param packet
	 * @param options
	 */
	void broadcastPacket(WebSocketConnector source, WebSocketPacket packet, BroadcastOptions options);

	/**
	 * Returns the unique ID of the server. Because the jWebSocket model
	 * supports multiple servers based on one or more engines (drivers) each
	 * server has its own ID so that it can be addressed properly.
	 * 
	 * @return String Unique ID of the Server.
	 */
	String getId();

	/**
	 * Returns the plugin chain for the server .
	 * 
	 * @return the plugInChain
	 */
	WebSocketPlugInChain getPlugInChain();

	/**
	 * Returns the filter chain for the server.
	 * 
	 * @return the filterChain
	 */
	WebSocketFilterChain getFilterChain();

	/**
	 * 
	 * @param listener
	 */
	void addListener(WebSocketServerListener listener);

	/**
	 * 
	 * @param listener
	 */
	void removeListener(WebSocketServerListener listener);

	/**
	 * Returns the list of listeners for the server.
	 * 
	 * @return the filterChain
	 */
	List<WebSocketServerListener> getListeners();

	/**
	 * 
	 * @param connector
	 */
	
	WebSocketConnector getConnector(String id);

	IConnectorManager getConnectors();

	WebSocketConnector[] getAllConnectors() ;

	void addConnector(WebSocketConnector connector) ;

	void removeConnector(WebSocketConnector connector);

	void broadcastPacket(IMessagePacket packet);

	void sendPacket(Selector selector, IMessagePacket packet);

	ServiceContext getContext();

	WebSocketConnector findConnector(Selector selector);
	
	List<WebSocketConnector> findConnectors(Selector selector) ;

}
