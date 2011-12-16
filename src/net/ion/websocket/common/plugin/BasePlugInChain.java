//	---------------------------------------------------------------------------
//	jWebSocket - Plug in chain for incoming requests (per server)
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
package net.ion.websocket.common.plugin;

import java.util.List;

import javolution.util.FastList;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.common.api.WebSocketPlugInChain;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;
import net.ion.websocket.common.logging.Logging;

import org.apache.log4j.Logger;

/**
 * Implements the basic chain of plug-ins which is triggered by a server when data packets are received. Each data packet is pushed through the chain and can be processed by the plug-ins.
 * 
 * @author aschulze
 */
public class BasePlugInChain implements WebSocketPlugInChain {

	private static Logger mLog = Logging.getLogger(BasePlugInChain.class);
	private List<WebSocketPlugIn> plugins = new FastList<WebSocketPlugIn>();
	private final WebSocketServer server;

	/**
	 * 
	 * @param server
	 */
	public BasePlugInChain(WebSocketServer server) {
		this.server = server;
	}

	/**
	 * {@inheritDoc}
	 */
	public void engineStarted(WebSocketEngine engine) {
		for (WebSocketPlugIn plugin : getPlugIns()) {
			plugin.engineStarted(engine);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void engineStopped(WebSocketEngine engine) {
		for (WebSocketPlugIn plugin : getPlugIns()) {
			plugin.engineStopped(engine);
		}
	}

	/**
	 * @param connector
	 */
	public void connectorStarted(WebSocketConnector connector) {
		for (WebSocketPlugIn plugin : getPlugIns()) {
			plugin.connectorStarted(connector);
		}
	}

	/**
	 * 
	 * @param connector
	 * @return
	 */
	public PlugInResponse processPacket(WebSocketConnector connector, WebSocketPacket pacaket) {
		PlugInResponse response = new PlugInResponse();
		for (WebSocketPlugIn plugin : getPlugIns()) {
			plugin.processPacket(response, connector, pacaket);
			if (response.isChainAborted()) {
				break;
			}
		}
		return response;
	}

	/**
	 * 
	 * @param connector
	 * @param creason
	 */
	public void connectorStopped(WebSocketConnector connector, CloseReason creason) {
		for (WebSocketPlugIn plugin : getPlugIns()) {
			plugin.connectorStopped(connector, creason);
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<WebSocketPlugIn> getPlugIns() {
		return plugins;
	}

	/**
	 * 
	 * @param plugin
	 */
	public void addPlugIn(WebSocketPlugIn plugin) {
		plugins.add(plugin);
		plugin.setPlugInChain(this);
//		mLog.info(plugin + " added") ;
	}

	/**
	 * 
	 * @param plugin
	 */
	public void removePlugIn(WebSocketPlugIn plugin) {
		plugins.remove(plugin);
		plugin.setPlugInChain(null);
//		mLog.info(plugin + " removed") ;
	}

	/**
	 * @return the server
	 */
	public WebSocketServer getServer() {
		return server;
	}

	public void clear() {
		plugins.clear();
	}
}
