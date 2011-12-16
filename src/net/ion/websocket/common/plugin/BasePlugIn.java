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

import org.json.JSONObject;

import javolution.util.FastMap;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.common.api.WebSocketPlugInChain;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.config.PluginConfiguration;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;

/**
 *
 * @author aschulze
 */
public abstract class BasePlugIn implements WebSocketPlugIn {


	private WebSocketPlugInChain plugChain = null;
	private Map<String, Object> settings = new FastMap<String, Object>();
	private PluginConfiguration pconfig;

	/**
	 * Constructor
	 *
	 * @param aConfiguration
	 *          the plugin configuration
	 */
	public BasePlugIn() {
		this(PluginConfiguration.BLANK) ;
	}
	
	public BasePlugIn(PluginConfiguration pconfig) {
		this.pconfig = pconfig;
		if (pconfig != null) {
			addAllSettings(pconfig.getSettings());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	/*
	@Override
	public void setPluginConfiguration(PluginConfiguration aConfiguration) {
	this.mConfiguration = aConfiguration;
	}
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PluginConfiguration getPluginConfiguration() {
		return pconfig;
	}

	@Override
	public void engineStarted(WebSocketEngine engine){}

	@Override
	public void engineStopped(WebSocketEngine engine){}

	/**
	 *
	 * @param connector
	 */
	@Override
	public void connectorStarted(WebSocketConnector connector){}

	/**
	 *
	 * @param aResponse
	 * @param aConnector
	 * @param aDataPacket
	 */
	@Override
	public abstract void processPacket(PlugInResponse aResponse, WebSocketConnector aConnector, WebSocketPacket aDataPacket);

	/**
	 *
	 * @param connector
	 * @param creason
	 */
	@Override
	public void connectorStopped(WebSocketConnector connector, CloseReason creason){}

	/**
	 *
	 * @param plugInChain
	 */
	@Override
	public void setPlugInChain(WebSocketPlugInChain plugInChain) {
		plugChain = plugInChain;
	}

	/**
	 * @return the plugInChain
	 */
	@Override
	public WebSocketPlugInChain getPlugInChain() {
		return plugChain;
	}

	/**
	 *
	 * @return
	 */
	public WebSocketServer getServer() {
		WebSocketServer lServer = null;
		if (plugChain != null) {
			lServer = plugChain.getServer();
		}
		return lServer;
	}

	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>getUsername</tt> to simplify token plug-in code.
	 *
	 * @param connector
	 * @return
	 */
	public String getUsername(WebSocketConnector connector) {
		return connector.getUsername();
	}

	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>setUsername</tt> to simplify token plug-in code.
	 *
	 * @param connector
	 * @param userName
	 */
	public void setUsername(WebSocketConnector connector, String userName) {
		connector.setUsername(userName);
	}



	/**
	 * Convenience method, just a wrapper for token server method
	 * <tt>getServer().getAllConnectors().size()</tt> to simplify token plug-in
	 * code.
	 *
	 * @return
	 */
	public int getConnectorCount() {
		return getServer().getAllConnectors().length ;
	}

	/**
	 *
	 * @param key
	 * @param value
	 */
	@Override
	public void addString(String key, String value) {
		settings.put(key, value);
	}

	/**
	 * @param settings
	 */
	// @Override
	private void addAllSettings(Map<String, Object> settings) {
		settings.putAll(settings);
	}

	/**
	 *
	 * @param key
	 */
	@Override
	public void removeSetting(String key) {
		if (key != null) {
			settings.remove(key);
		}
	}

	/**
	 *
	 */
	@Override
	public void clearSettings() {
		settings.clear();
	}

	/**
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public String getString(String key, String defaultVal) {
		Object value = settings.get(key);
		String res = null;
		if (value != null && value instanceof String) {
			res = (String) value;
		}
		return (res != null ? res : defaultVal);
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	@Override
	public String getString(String key) {
		return (key != null ? getString(key, null) : null);
	}

	/**
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public JSONObject getJSON(String key, JSONObject defaultVal) {
		Object lValue = settings.get(key);
		JSONObject lRes = null;
		if (lValue != null && lValue instanceof JSONObject) {
			lRes = (JSONObject) lValue;
		}
		return (lRes != null ? lRes : defaultVal);
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	@Override
	public JSONObject getJSON(String key) {
		return (key != null ? getJSON(key, null) : null);
	}

	@Override
	public Map<String, Object> getSettings() {
		return settings;
	}

	/**
	 * @return the id of the plug-in
	 */
	@Override
	public String getId() {
		return pconfig.getId();
	}

	/**
	 * @return the name of the plug-in
	 */
	@Override
	public String getName() {
		return pconfig.getName();
	}
}
