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

import net.ion.websocket.common.async.IOFuture;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.connector.BaseConnector;
import net.ion.websocket.common.kit.WebSocketProtocolAbstraction;

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
	 * Default reserved name for shared custom variable <tt>username</tt>.
	 */
	public final static String VAR_USERNAME = "$username";
	/**
	 * Default reserved name for shared custom variable <tt>subprot</tt>.
	 */
	public final static String VAR_SUBPROT = "$subprot";
	
	public final static String VAR_REQUEST_URI = "$request_uri" ;
	
	/**
	 * Default reserved name for shared custom variable <tt>version</tt>.
	 */
	public final static String VAR_VERSION = "$version";
	/**
	 * Default name for shared custom variable <tt>nodeid</tt>.
	 */
	public final static String VAR_NODEID = "$nodeid";
	/**
	 * Is connector using SSL encryption?
	 */
	private boolean mIsSSL = false;
	/**
	 * the WebSocket protocol version
	 */
	private int mVersion = CommonConstants.WS_VERSION_DEFAULT;
	/**
	 * Backward reference to the engine of this connector.
	 */
	private WebSocketEngine engine = null;
	/**
	 * Backup of the original request header and it's fields.
	 * TODO: maybe obsolete for the future
	 */
	private RequestHeader header = null;
	/**
	 * Session object for the WebSocket connection.
	 */
	private final WebSocketSession mSession = new WebSocketSession();
	/**
	 * Shared Variables container for this connector.
	 */
	private final Map<String, Object> customVars = new FastMap<String, Object>();

	/**
	 *
	 * @param engine
	 */
	public BaseConnector(WebSocketEngine engine) {
		this.engine = engine;
	}

	@Override
	public void startConnector() {
		engine.connectorStarted(this);
	}

	@Override
	public void stopConnector(CloseReason creason) {
		engine.connectorStopped(this, creason);
	}

	@Override
	public void processPacket(WebSocketPacket packet) {
		engine.processPacket(this, packet);
	}

	@Override
	public void sendPacket(WebSocketPacket packet) {
	}

	@Override
	public IOFuture sendPacketAsync(WebSocketPacket packet) {
		return null;
	}

	@Override
	public WebSocketEngine getEngine() {
		return engine;
	}

	@Override
	public RequestHeader getHeader() {
		return header;
	}

	/**
	 * @param header
	 * the header to set
	 */
	@Override
	public void setHeader(RequestHeader header) {
		// TODO: the sub protocol should be a connector variable! not part of
		// the header!
		this.header = header;
		// TODO: this can be improved, maybe distinguish between header and URL
		// args!
		Map args = header.getArgs();
		if (args != null) {
			String nodeId = (String) args.get("unid");
			if (nodeId != null) {
				setNodeId(nodeId);
				args.remove("unid");
			}
		}
	}

	@Override
	public Object getVar(String key) {
		return customVars.get(key);
	}

	@Override
	public void setVar(String aKey, Object value) {
		customVars.put(aKey, value);
	}

	@Override
	public Boolean getBoolean(String key) {
		return (Boolean) getVar(key);
	}

	@Override
	public boolean getBool(String key) {
		Boolean lBool = getBoolean(key);
		return (lBool != null && lBool);
	}

	@Override
	public void setBoolean(String key, Boolean value) {
		setVar(key, value);
	}

	@Override
	public String getString(String key) {
		return (String) getVar(key);
	}

	@Override
	public void setString(String key, String value) {
		setVar(key, value);
	}

	@Override
	public Integer getInteger(String key) {
		return (Integer) getVar(key);
	}

	@Override
	public void setInteger(String key, Integer value) {
		setVar(key, value);
	}

	@Override
	public void removeVar(String key) {
		customVars.remove(key);
	}

	@Override
	public String generateUID() {
		return null;
	}

	@Override
	public int getRemotePort() {
		return -1;
	}

	@Override
	public InetAddress getRemoteHost() {
		return null;
	}

	@Override
	public String getId() {
		String lNodeId = "" ;
		return ((lNodeId != null && lNodeId.length() > 0) ? lNodeId + "." : "")
				+ String.valueOf(getRemotePort());
	}

	@Override
	public WebSocketSession getSession() {
		return mSession;
	}

	// some convenience methods to easier process username (login-status)
	// and configured unique node id for clusters (independent from tcp port)
	@Override
	public String getUsername() {
		return getString(BaseConnector.VAR_USERNAME);
	}

	@Override
	public void setUsername(String aUsername) {
		setString(BaseConnector.VAR_USERNAME, aUsername);
	}

	@Override
	public void removeUsername() {
		removeVar(BaseConnector.VAR_USERNAME);
	}

	// some convenience methods to easier process subprot (login-status)
	// and configured unique node id for clusters (independent from tcp port)
	@Override
	public String getSubprot() {
		return getString(BaseConnector.VAR_SUBPROT);
	}

	@Override
	public void setSubprot(String aSubprot) {
		setString(BaseConnector.VAR_SUBPROT, aSubprot);
	}

	@Override
	public int getVersion() {
		return mVersion;
	}

	@Override
	public void setVersion(int aVersion) {
		mVersion = aVersion;
	}

	@Override
	public void removeSubprot() {
		removeVar(BaseConnector.VAR_SUBPROT);
	}

	@Override
	public boolean isLocal() {
		// TODO: This has to be updated for the cluster approach
		return true;
	}

	@Override
	public String getNodeId() {
		return getString(BaseConnector.VAR_NODEID);
	}

	@Override
	public void setNodeId(String aNodeId) {
		setString(BaseConnector.VAR_NODEID, aNodeId);
	}

	@Override
	public void removeNodeId() {
		removeVar(BaseConnector.VAR_NODEID);
	}

	@Override
	public boolean isSSL() {
		return mIsSSL;
	}

	@Override
	public void setSSL(boolean isSSL) {
		mIsSSL = isSSL;
	}

	@Override
	public boolean isHixie() {
		return WebSocketProtocolAbstraction.isHixieVersion(getVersion());
	}

	@Override
	public boolean isHybi() {
		return WebSocketProtocolAbstraction.isHybiVersion(getVersion());
	}
}
