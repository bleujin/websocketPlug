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
package net.ion.websocket.common.plugin;

import java.util.Map;

import javolution.util.FastMap;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.common.api.WebSocketPlugInChain;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;

/**
 *
 * @author aschulze
 */
public abstract class BasePlugIn implements WebSocketPlugIn {

	// TODO: a plug-in should have a name and an id to be uniquely identified in the chain!
	private WebSocketPlugInChain chain = null;

	private Map<String, ? super Object> settings = new FastMap<String, Object>();

	public void engineStarted(WebSocketEngine engine) {}

	public void engineStopped(WebSocketEngine engine) {}

	public void connectorStarted(WebSocketConnector conn) {}

	public void connectorStopped(WebSocketConnector conn, CloseReason creason) {}

	public abstract void processPacket(PlugInResponse response, WebSocketConnector conn, WebSocketPacket packet);


	public void setPlugInChain(WebSocketPlugInChain pchain) {
		this.chain = pchain;
	}

	/**
	 * @return the plugInChain
	 */
	public WebSocketPlugInChain getPlugInChain() {
		return chain;
	}

	/**
	 *
	 * @return
	 */
	public WebSocketServer getServer() {
		WebSocketServer server = null;
		if (chain != null) {
			server = chain.getServer();
		}
		return server;
	}

	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>getConnector</tt> to simplify token plug-in code.
	 * @param aId
	 * @return
	 */
	public WebSocketConnector getConnector(String aId) {
		return (aId != null ? getServer().getConnector(aId) : null);
	}

	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>getServer().getAllConnectors().size()</tt> to simplify token
	 * plug-in code.
	 * @return
	 */
	public int getConnectorCount() {
		return getServer().getAllConnectors().length ;
	}


	/**
	 *
	 * @param key
	 */
	public void removeSetting(String key) {
		if (key != null) {
			settings.remove(key);
		}
	}

	/**
	 *
	 */
	public void clearSettings() {
		settings.clear();
	}

	public String getAsString(String key, String dftValue) {
		String result = getAttribute(key, String.class);
		return (result != null ? result : dftValue);
	}

	
	public <T> T getAttribute(String id, Class<T> clz) {
		Object value = settings.get(id);
		if (value != null && clz.isInstance(value)) {
			return clz.cast(value);
		}
		return null ;
	}

	public <T> void putAttribute(String id, Object obj) {
		settings.put(id, obj);
	}
}
