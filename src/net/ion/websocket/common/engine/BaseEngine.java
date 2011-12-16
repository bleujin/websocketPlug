//	---------------------------------------------------------------------------
//	jWebSocket - Base Engine Implementation
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
package net.ion.websocket.common.engine;

import java.util.Map;

import javolution.util.FastMap;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.config.EngineConfiguration;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.common.server.IConnectorManager;
import net.ion.websocket.server.ConnectorManager;

/**
 * Provides the basic implementation of the jWebSocket engines. The {@code BaseEngine} is supposed to be used as ancestor for the engine implementations like e.g. the {@code TCPEngine} or the {@code NettyEngine}.
 * 
 * @author aschulze
 */
public class BaseEngine implements WebSocketEngine {

	private WebSocketServer server = null;
	private int sessionTimeout = CommonConstants.DEFAULT_TIMEOUT;
	private EngineConfiguration econfig;

	public BaseEngine(EngineConfiguration econfig) {
		this.econfig = econfig;
	}

	public void startEngine() throws WebSocketException {
		// this method will be overridden by engine implementations.
		// The implementation will notify server that the engine has started
		// Don't do this here: engineStarted();
	}

	public void stopEngine(CloseReason creason) throws WebSocketException {
		try {
			// stop all connectors of this engine
			for (WebSocketConnector conn : getConnectors().getAllConnectors()) {
				conn.stopConnector(creason);
			}
		} catch (Exception ex) {
			// log.info("Exception on sleep " + ex.getMessage());
		}
		// this method will be overridden by engine implementations.
		// The implementation will notify server that the engine has stopped
		// Don't do this here: engineStopped();
	}

	public void engineStarted() {
		// notify servers that the engine has started
		server.engineStarted(this);
	}

	public void engineStopped() {
		// notify servers that the engine has stopped
		server.engineStopped(this);
	}

	public void connectorStarted(WebSocketConnector connector) {
		// notify servers that a connector has started
		server.connectorStarted(connector);
	}

	public void connectorStopped(WebSocketConnector connector, CloseReason creason) {
		server.connectorStopped(connector, creason);
	}

	public boolean isAlive() {
		return false;
	}

	public void processPacket(WebSocketConnector connector, WebSocketPacket packet) {
		server.processPacket(this, connector, packet);
	}

	public void sendPacket(WebSocketConnector connector, WebSocketPacket packet) {
		connector.sendPacket(packet);
	}

	public void broadcastPacket(WebSocketConnector source, WebSocketPacket packet) {
		for (WebSocketConnector connector : getConnectors().getAllConnectors()) {
			connector.sendPacket(packet);
		}
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeOut) {
		this.sessionTimeout = sessionTimeOut;
	}

	public int getMaxFrameSize() {
		return econfig.getMaxFramesize();
	}

	public WebSocketConnector getConnectorByRemotePort(int remotePort) {
		for (WebSocketConnector connector : server.getConnectors().getAllConnectors()) {
			if (connector.getRemotePort() == remotePort) {
				return connector;
			}
		}
		return null;
	}

	public WebSocketServer getServer() {
		return server; // (FastMap) (servers.unmodifiable());
	}

	public void setServer(WebSocketServer server) {
		this.server = server;
	}

	public void removeServer(WebSocketServer server) {
		this.server = null;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return econfig.getId();
	}

	public EngineConfiguration getConfiguration() {
		return econfig;
	}
	
	public IConnectorManager getConnectors(){
		return server.getConnectors() ;
	}
}
