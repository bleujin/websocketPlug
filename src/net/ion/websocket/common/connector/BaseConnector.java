//	---------------------------------------------------------------------------
//	jWebSocket - Base Connector Implementation
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
package net.ion.websocket.common.connector;

import java.net.InetAddress;
import java.util.Map;

import javolution.util.FastMap;
import net.ion.websocket.common.api.ObjectId;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.RequestHeader;
import net.ion.websocket.common.kit.WebSocketSession;

/**
 * Provides the basic implementation of the jWebSocket connectors. The {@code BaseConnector} is supposed to be used as ancestor for the connector implementations like e.g. the {@code TCPConnector} or the {@code NettyConnector }.
 * 
 * @author aschulze
 */
public class BaseConnector implements WebSocketConnector {

	/**
	 * Default name for shared custom variable <tt>username</tt>.
	 */
	public final static String VAR_USERNAME = "$username";
	public final static String VAR_REQUEST_URI = "$request_uri";
	/**
	 * Default name for shared custom variable <tt>nodeid</tt>.
	 */
	public final static String VAR_NODEID = "$nodeid";
	private WebSocketEngine engine = null;
	private RequestHeader header = null;
	private final WebSocketSession session = new WebSocketSession();
	private final Map<String, Object> customVars = new FastMap<String, Object>();

	/**
	 * 
	 * @param aEngine
	 */
	public BaseConnector(WebSocketEngine aEngine) {
		engine = aEngine;
	}

	public void startConnector() {
		if (engine != null) {
			engine.connectorStarted(this);
		}
	}

	public void stopConnector(CloseReason aCloseReason) {
		if (engine != null) {
			engine.connectorStopped(this, aCloseReason);
		}
	}

	public void processPacket(WebSocketPacket aDataPacket) {
		if (engine != null) {
			engine.processPacket(this, aDataPacket);
		}
	}

	public void sendPacket(WebSocketPacket aDataPacket) {
	}

	public WebSocketEngine getEngine() {
		return engine;
	}

	public RequestHeader getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(RequestHeader header) {
		// TODO: the sub protocol should be a connector variable! not part of the header!
		this.header = header;

		// TODO: this can be improved, maybe distinguish between header and URL args!
		Map lArgs = header.getArgs();
		String lNodeId = (String) lArgs.get("unid");
		if (lNodeId != null) {
			setNodeId(lNodeId);
			lArgs.remove("unid");
		}
	}

	public Object getVar(String aKey) {
		return customVars.get(aKey);
	}

	public void setVar(String aKey, Object aValue) {
		customVars.put(aKey, aValue);
	}

	public Boolean getBoolean(String aKey) {
		return (Boolean) getVar(aKey);
	}

	public boolean getBool(String aKey) {
		Boolean lBool = getBoolean(aKey);
		return (lBool != null && lBool);
	}

	public void setBoolean(String aKey, Boolean aValue) {
		setVar(aKey, aValue);
	}

	public String getString(String aKey) {
		return (String) getVar(aKey);
	}

	public void setString(String aKey, String aValue) {
		setVar(aKey, aValue);
	}

	public Integer getInteger(String aKey) {
		return (Integer) getVar(aKey);
	}

	public void setInteger(String aKey, Integer aValue) {
		setVar(aKey, aValue);
	}

	public void removeVar(String aKey) {
		customVars.remove(aKey);
	}

	public String generateUID() {
		return new ObjectId().toString();
	}

	public int getRemotePort() {
		return -1;
	}

	public InetAddress getRemoteHost() {
		return null;
	}

	public String getId() {
		return String.valueOf(getRemotePort());
	}

	/*
	 * Returns the session for the websocket connection.
	 * 
	 * @return
	 */
	public WebSocketSession getSession() {
		return session;
	}

	// some convenience methods to easier process username (login-status)
	// and configured unique node id for clusters (independent from tcp port)
	/**
	 * 
	 * @return
	 */
	public String getUsername() {
		return getString(BaseConnector.VAR_USERNAME);
	}

	/**
	 * 
	 * @param aUsername
	 */
	public void setUsername(String aUsername) {
		setString(BaseConnector.VAR_USERNAME, aUsername);
	}

	/**
	 *
	 */
	public void removeUsername() {
		removeVar(BaseConnector.VAR_USERNAME);
	}

	/**
	 * 
	 * @return
	 */
	public String getNodeId() {
		return getString(BaseConnector.VAR_NODEID);
	}

	/**
	 * 
	 * @param aNodeId
	 */
	public void setNodeId(String aNodeId) {
		setString(BaseConnector.VAR_NODEID, aNodeId);
	}

	/**
	 *
	 */
	public void removeNodeId() {
		removeVar(BaseConnector.VAR_NODEID);
	}
}
