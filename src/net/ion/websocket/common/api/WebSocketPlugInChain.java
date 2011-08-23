//	---------------------------------------------------------------------------
//	jWebSocket - PlugInChain Interface
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

import java.util.List;

import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;


/**
 * A plug-in chain maintains a map of plug-ins. A server in the jWebSocket model
 * usually does not process data packets directly but forwards them to a chain
 * of plug-ins. The plug-in chain then forwards the data packet to each plug-in
 * until the first plug-in aborts or breaks the chain by returning the
 * corresponding PlugInResponse.
 * @author aschulze
 */
public interface WebSocketPlugInChain {

	/**
	 * is called by the server when the engine has been started. Usually the
	 * implementations iterate through the chain of plug-ins and call their
	 * <tt>engineStarted</tt> method of each plug-in to notify them about the
	 * "engine started" event. This event is useful when a plug-in needs to be
	 * initialized before first usage.
	 * @param engine The jWebSocket engine that has just started.
	 */
	void engineStarted(WebSocketEngine engine);

	/**
	 * is called by the server when the engine has been stopped. Usually the
	 * implementations iterate through the chain of plug-ins and call their
	 * <tt>engineStopped</tt> method of each plug-in to notify them about the
	 * "engine stopped" event. This event is useful when a plug-in needs to be
	 * cleaned up after usage.
	 * @param engine The jWebSocket engine that has just stopped.
	 */
	void engineStopped(WebSocketEngine engine);

	/**
	 * is called by the server when a new connector has been started,
	 * i.e. a new client has connected. Usually the implementations iterate
	 * through the chain of plug-ins and call their <tt>connectorStarted</tt>
	 * method of each plug-in to notify them about the connect event.
	 * @param connector The connector that has just started.
	 */
	void connectorStarted(WebSocketConnector connector);

	/**
	 * is called when a data packet from a client was received and has to be
	 * processed. Usually the implementations iterate through the chain of 
	 * plug-ins and call the <tt>processPacket</tt> method of each plug-in to
	 * notify them about the incoming packet.
	 * @param connector The connector from which the data packet was received.
	 * @param packet The data packet which was received.
	 * @return PlugInResponse specifies whether to continue or abort the processing of the plug-in chain.
	 */
	PlugInResponse processPacket(WebSocketConnector connector, WebSocketPacket packet);

	/**
	 * is called by the server when a connector has been stopped,
	 * i.e. a client has disconnected.  Usually the implementations iterate
	 * through the chain of plug-ins and call the <tt>connectorStopped</tt>
	 * method of the plug-ins to notify them about the disconnect event.
	 * @param connector The connector that has just stopped.
	 * @param creason Specifies why a connection has closed. Please refer to CloseReason documentation.
	 */
	void connectorStopped(WebSocketConnector connector, CloseReason creason);

	/**
	 * returns the list of the plug-ins within this plug-in chain.
	 * @return List of plug-ins.
	 */
	List<WebSocketPlugIn> getPlugIns();

	/**
	 * appends a plug-in to the plug-in chain. All subsequent incoming data packet
	 * will be forwarded to that plug-in too.
	 * @param plugin Plug-in to be added from the plug-in chain.
	 */
	void addPlugIn(WebSocketPlugIn plugin);

	/**
	 * removes a plug-in from the plug-in chain. All subsequent incoming data
	 * packet will not be forwarded to that plug-in any more.
	 * @param plugin Plug-in to be removed from the plug-in chain.
	 */
	void removePlugIn(WebSocketPlugIn plugin);

	WebSocketServer getServer();
	
	void clear() ;
	

}
