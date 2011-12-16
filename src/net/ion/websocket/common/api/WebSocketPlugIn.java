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

import java.util.Map;

import org.json.JSONObject;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketPlugInChain;
import net.ion.websocket.common.config.PluginConfiguration;

import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;

/**
 *
 * @author aschulze
 */
public interface WebSocketPlugIn {

	/**
	 * returns the id of the plug-in.
	 * @return
	 */
	String getId();

	/**
	 * returns the name of the plug-in.
	 * @return
	 */
	String getName();

	/**
	 * is called by the server when the engine has been started.
	 *
	 * @param engine
	 */
	void engineStarted(WebSocketEngine engine);

	/**
	 * is called by the server when the engine has been stopped.
	 *
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
	 * @param dataPacket
	 */
	void processPacket(PlugInResponse response, WebSocketConnector connector, WebSocketPacket dataPacket);

	/**
	 *
	 * @param connector
	 * @param creason
	 */
	void connectorStopped(WebSocketConnector connector, CloseReason creason);

	/**
	 *
	 * @param plugChain
	 */
	void setPlugInChain(WebSocketPlugInChain plugChain);

	/**
	 * @return the plugInChain
	 */
	WebSocketPlugInChain getPlugInChain();

	/**
	 * Set the plugin configuration
	 *
	 * @param configuration
	 *          the plugin configuration object to set
	 */
	// void setPluginConfiguration(PluginConfiguration configuration);
	/**
	 * Returns the plugin configuration object based on the configuration file
	 * values
	 *
	 * @return the plugin configuration object
	 */
	PluginConfiguration getPluginConfiguration();

	/**
	 *
	 * @param key
	 * @param value
	 */
	void addString(String key, String value);

	/**
	 *
	 *
	 * @param aSettings
	 */
	// void addAllSettings(Map<String, String> aSettings);
	/**
	 *
	 * @param key
	 */
	void removeSetting(String key);

	/**
	 *
	 */
	void clearSettings();

	/**
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	String getString(String key, String defaultVal);

	/**
	 *
	 * @param key
	 * @return
	 */
	String getString(String key);

	/**
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	JSONObject getJSON(String key, JSONObject defaultVal);

	/**
	 *
	 * @param key
	 * @return
	 */
	JSONObject getJSON(String key);

	/**
	 *
	 * @return
	 */
	Map<String, Object> getSettings();
}
