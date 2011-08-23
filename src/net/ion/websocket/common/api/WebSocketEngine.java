//	---------------------------------------------------------------------------
//	jWebSocket - Engine API
//	Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
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
import net.ion.websocket.common.kit.WebSocketException;

/**
 * Specifies the API for jWebSocket engines. An engine maintains multiple
 * connectors. The engine does neither parse nor process the data packets but
 * only passes them from either an underlying connector to the above server(s)
 * or from one of the higher level servers to one or more connectors of a
 * particular engine.
 *
 * @author Alexander Schulze
 * @author Puran Singh
 * @version $Id: WebSocketEngine.java 2010-03-03
 */
public interface WebSocketEngine {

	/**
	 * Returns the unique id of the engine. Because the jWebSocket model
	 * supports multiple engines as a kind of drivers for the servers on top
	 * of it each engine has its own Id so that it can be addressed properly.
	 *
	 * @return String
	 */
	String getId();

	/**
	 * Starts the engine. Usually an engine is implemented as a thread which
	 * waits for new clients to be connected via a WebSocketConnector. So here
	 * usually the listener threads for incoming connections are instantiated.
	 *
	 * @throws WebSocketException
	 */
	void startEngine() throws WebSocketException;

	/**
	 * Stops the engine. Here usually first all connected clients are stopped
	 * and afterwards the listener threads for incoming new clients is stopped
	 * as well.
	 *
	 * @param creason
	 * @throws WebSocketException
	 */
	void stopEngine(CloseReason creason) throws WebSocketException;

	/**
	 * Is called after the web socket engine has been started sucessfully.
	 * Here usually the engine notifies the server(s) above that the engine
	 * is started.
	 */
	void engineStarted();

	/**
	 * Is called after the web socket engine has (been) stopped sucessfully.
	 * Here usually the engine notifies the server(s) above that the engine
	 * has stopped.
	 */
	public void engineStopped();

	/**
	 * Is called after a new client has connected. Here usually the engine
	 * notifies the server(s) above that a new connection has been established.
	 *
	 * @param connector
	 */
	void connectorStarted(WebSocketConnector connector);

	/**
	 * Is called after a new client has disconnected. Here usually the engine
	 * notifies the server(s) above that a connection has been closed.
	 *
	 * @param connector
	 * @param reason
	 */
	void connectorStopped(WebSocketConnector connector, CloseReason reason);

	/**
	 * Returns the TCP connector identified by its remote port number or
	 * {@code null} if there's no client connector to the port passed.
	 *
	 * @param remotePort the remote TCP port searched for.
	 * @return WebSocketConnector that matches the given remote port or {@code null} if no connector matches the remote port.
	 */
	WebSocketConnector getConnectorByRemotePort(int remotePort);

	/**
	 * Returns {@code true} if the engine is running or {@code false} otherwise.
	 * The alive status usually represents the state of the main engine listener
	 * thread.
	 *
	 * @return true or false based on the server status
	 */
	boolean isAlive();

	/**
	 * Processes an incoming data packet from a certain connector. The
	 * implementation of the engine usually simply passes the packets to the
	 * server(s) of the overlying communication tier.
	 *
	 * @param connector
	 * @param packet
	 */
	void processPacket(WebSocketConnector connector, WebSocketPacket packet);

	/**
	 * Sends a data packet to a certain connector.
	 *
	 * @param connector
	 * @param packet
	 */
	void sendPacket(WebSocketConnector connector, WebSocketPacket packet);

	/**
	 * Broadcasts a data packet to all connectors. Usually the implementation
	 * simply iterates through the list of connectors and calls their sendPacket
	 * method.
	 *
	 * @param source
	 * @param packet
	 */
	void broadcastPacket(WebSocketConnector source, WebSocketPacket packet);

	/**
	 * Returns a list of all servers that are currenly bound to this engine.
	 * This list of servers is maintained by the engine and should not be
	 * manipulated by the application.
	 *
	 * @return List of servers bound to the engine.
	 */
	WebSocketServer getServer();

	/**
	 * Registers a server at the engine so that the engine is able to notify
	 * the server in case of new connections and incoming data packets from
	 * a connector. This method is not supposed to be called directly from the
	 * application.
	 *
	 * @param server
	 */
	void setServer(WebSocketServer server);

	/**
	 * Unregisters a server from the engine so that the engine won't notify
	 * the server in case of new connections or incoming data packets from
	 * a connector. This method is not supposed to be called directly from the
	 * application.
	 *
	 * @param server
	 */
	void removeServer(WebSocketServer server);

	/**
	 * This method might be removed in future, instead use <tt>getConfiguration()</tt>
	 * to get the engine configuration.
	 *
	 * Returns the default session timeout for this engine. The session timeout
	 * is applied if no specific session timeout per connector is passed.
	 * Basically each connector can optionally use his own session timeout.
	 *
	 * @return int The default session timeout in milliseconds.
	 */
	@Deprecated
	int getSessionTimeout();

	/**
	 * This method might be removed in future, instead use <tt>getConfiguration()</tt>
	 * to get the engine configuration.
	 *
	 * Sets the default session timeout for this engine. The session timeout
	 * is applied if no specific session timeout per connector is passed.
	 * Basically each connector can optionally use his own session timeout.
	 *
	 * @param aSessionTimeout The default session timeout in milliseconds.
	 */
	@Deprecated
	void setSessionTimeout(int aSessionTimeout);

	/**
	 * This method might be removed in future, instead use <tt>getConfiguration()</tt>
	 * to get the engine configuration.
	 *
	 * Returns the maximum frame size in bytes, If the client
	 * sends a frame size larger than this maximum value, the socket connection
	 * will be closed.
	 *
	 * @return the max frame size value
	 */
	@Deprecated
	int getMaxFrameSize();

	/**
	 * Returns the configuration for the engine.
	 * @return the engine configuration object
	 */
	EngineConfiguration getConfiguration();
}
