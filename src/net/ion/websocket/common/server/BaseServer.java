//	---------------------------------------------------------------------------
//	jWebSocket - Basic server (dispatcher)
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
package net.ion.websocket.common.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ion.websocket.common.async.IOFuture;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.websocket.common.api.Selector;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketFilterChain;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketPlugInChain;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.config.ServerConfiguration;
import net.ion.websocket.common.connector.BaseConnector;
import net.ion.websocket.common.filter.BaseFilterChain;
import net.ion.websocket.common.kit.BroadcastOptions;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.common.kit.WebSocketServerEvent;
import net.ion.websocket.common.plugin.BasePlugInChain;
import net.ion.websocket.plugin.IMessagePacket;
import net.ion.websocket.server.ConnectorManager;
import net.ion.websocket.server.context.ServiceContext;

/**
 * The implementation of the basic websocket server. A server is the central instance which either processes incoming data from the engines directly or routes it to the chain of plug-ins. Each server maintains a FastMap of underlying engines. An application can instantiate multiple servers to process different kinds of data packets.
 * 
 * @author aschulze
 */
public abstract class BaseServer implements WebSocketServer {

	private WebSocketEngine engine;
	private List<WebSocketServerListener> listeners = new FastList<WebSocketServerListener>();
	private final ServerConfiguration config;
	protected WebSocketPlugInChain plugChain;
	protected WebSocketFilterChain filterChain;
	private ServiceContext context;

	protected BaseServer(ServerConfiguration config) {
		this(ServiceContext.createRoot(), config);
	}

	protected BaseServer(ServiceContext context, ServerConfiguration config) {
		this.context = context;
		this.config = config;
	}

	protected void init(BasePlugInChain plugChain, BaseFilterChain filterChain) {
		this.plugChain = plugChain;
		this.filterChain = filterChain;
	}

	public void setEngine(WebSocketEngine engine) {
		this.engine = engine;
		this.engine.setServer(this);
	}

	/**
	 * {@inheritDoc }
	 */
	public void startServer() throws WebSocketException {
		// this method is supposed to be overwritten by descending classes.
	}

	public boolean isAlive() {
		// this method is supposed to be overwritten by descending classes.
		return false;
	}

	public void stopServer() throws WebSocketException {
		// this method is supposed to be overwritten by descending classes.

	}

	public void engineStarted(WebSocketEngine engine) {
		// this method is supposed to be overwritten by descending classes.
		// e.g. to notify the overlying appplications or plug-ins
		// about the engineStarted event
	}

	public void engineStopped(WebSocketEngine engine) {
		// this method is supposed to be overwritten by descending classes.
		// e.g. to notify the overlying appplications or plug-ins
		// about the engineStopped event
	}

	public void connectorStarted(WebSocketConnector connector) {
		// this method is supposed to be overwritten by descending classes.
		// e.g. to notify the overlying appplications or plug-ins
		// about the connectorStarted event
		WebSocketServerEvent event = new WebSocketServerEvent(connector, this);
		for (WebSocketServerListener listener : listeners) {
			listener.processOpened(event);
		}
	}

	public void connectorStopped(WebSocketConnector connector, CloseReason creason) {
		// this method is supposed to be overwritten by descending classes.
		// e.g. to notify the overlying appplications or plug-ins
		// about the connectorStopped event
		WebSocketServerEvent event = new WebSocketServerEvent(connector, this);
		for (WebSocketServerListener listener : listeners) {
			listener.processClosed(event);
		}
	}

	public void processPacket(WebSocketEngine engine, WebSocketConnector connector, WebSocketPacket packet) {
		; // noaction
	}

	public void sendPacket(WebSocketConnector connector, WebSocketPacket packet) {
		// send a data packet to the passed connector
		connector.sendPacket(packet);
	}
	
    @Override
    public IOFuture sendPacketAsync(WebSocketConnector aConnector, WebSocketPacket aDataPacket) {
        // send a data packet to the passed connector
        return aConnector.sendPacketAsync(aDataPacket);
    }
    
	public void broadcastPacket(WebSocketConnector source, WebSocketPacket packet, BroadcastOptions option) {
		for (WebSocketConnector connector : getAllConnectors()) {
			if (!source.equals(connector) || option.isSenderIncluded()) {
				if (option.isAsync()) {
					sendPacketAsync(connector, packet);
				} else {
					sendPacket(connector, packet);
				}
			}
		}
	}

	public WebSocketEngine getEngine() {
		return engine;
	}

	public WebSocketConnector getConnector(final String aId) {
		return findConnector(new Selector() {
			public boolean isTrueCondition(WebSocketConnector connector) {
				return aId.equals(connector.getId());
			}
		});
	}

	public WebSocketConnector findConnector(Selector selector) {
		for (WebSocketConnector connector : getAllConnectors()) {
			if (selector.isTrueCondition(connector)) {
				return connector;
			}
		}
		return null;
	}

	public List<WebSocketConnector> findConnectors(Selector selector) {
		List<WebSocketConnector> result = ListUtil.newList();
		for (WebSocketConnector connector : getAllConnectors()) {
			if (selector.isTrueCondition(connector)) {
				result.add(connector);
			}
		}
		return Collections.unmodifiableList(result);
	}

	public Map<String, WebSocketConnector> selectConnectors(Map<String, Object> aFilter) {
		Map<String, WebSocketConnector> lClients = new FastMap<String, WebSocketConnector>().shared();
		for (WebSocketConnector lConnector : getConnectors().getAllConnectors()) {
			boolean lMatch = true;
			for (String lKey : aFilter.keySet()) {
				Object lVarVal = lConnector.getVar(lKey);
				lMatch = (lVarVal != null);
				if (lMatch) {
					Object lFilterVal = aFilter.get(lKey);
					if (lVarVal instanceof String && lFilterVal instanceof String) {
						lMatch = ((String) lVarVal).matches((String) lFilterVal);
					} else if (lVarVal instanceof Boolean) {
						lMatch = ((Boolean) lVarVal).equals((Boolean) lFilterVal);
					} else {
						lMatch = lVarVal.equals(lFilterVal);
					}
					if (!lMatch) {
						break;
					}
				}
			}
			if (lMatch) {
				lClients.put(lConnector.getId(), lConnector);
			}
		}
		// return (FastMap)(lClients.unmodifiable());
		return lClients;
	}

	public String getId() {
		return config.getId();
	}

	public ServerConfiguration getConfiguration() {
		return config;
	}

	public WebSocketPlugInChain getPlugInChain() {
		return plugChain;
	}

	public WebSocketFilterChain getFilterChain() {
		return filterChain;
	}

	public void addListener(WebSocketServerListener aListener) {
		listeners.add(aListener);
	}

	public void removeListener(WebSocketServerListener aListener) {
		listeners.remove(aListener);
	}

	/**
	 * @return the listeners
	 */
	public List<WebSocketServerListener> getListeners() {
		return Collections.unmodifiableList(listeners);
	}

	public WebSocketConnector[] getAllConnectors() {
		return getConnectors().getAllConnectors();
	}

	public void removeConnector(WebSocketConnector connector) {
		getConnectors().remove(connector);
		// connector.stopConnector(CloseReason.SERVER) ;
	}

	public void addConnector(WebSocketConnector connector) {
		getConnectors().add(connector);
	}

	public void sendPacket(Selector selector, IMessagePacket packet) {
		for (WebSocketConnector conn : findConnectors(selector)) {
			this.sendPacket(conn, packet.forSend());
		}
	}

	public void broadcastPacket(IMessagePacket packet) {
		for (WebSocketConnector connector : getAllConnectors()) {
			sendPacket(connector, packet.forSend());
		}
	}

	public ServiceContext getContext() {
		return this.context;
	}

	public abstract IConnectorManager getConnectors();

}
