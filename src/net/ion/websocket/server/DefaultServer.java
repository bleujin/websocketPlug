package net.ion.websocket.server;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ion.framework.util.Debug;
import net.ion.framework.util.InstanceCreationException;
import net.ion.framework.util.MapUtil;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.common.CommonEnum.SubProtocol;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.common.api.WebSocketPlugInChain;
import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.config.ServerConfiguration;
import net.ion.websocket.common.filter.BaseFilterChain;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;
import net.ion.websocket.common.kit.RequestHeader;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.common.kit.WebSocketServerEvent;
import net.ion.websocket.common.plugin.BasePlugInChain;
import net.ion.websocket.common.server.BaseServer;
import net.ion.websocket.common.server.IConnectorManager;
import net.ion.websocket.server.context.ServiceContext;
import net.ion.websocket.server.engine.netty.NettyEngine;

public class DefaultServer extends BaseServer {

	
	private final ConnectorManager connectors = new ConnectorManager("userId");

	public DefaultServer(WebSocketEngine engine) {
		this(new DefaultServerConfiguration(), engine, ServiceContext.createRoot());
	}

	public DefaultServer(ServerConfiguration config, WebSocketEngine engine, ServiceContext context) {
		super(context, config);
		init(new BasePlugInChain(this), new BaseFilterChain(this));
		setEngine(engine);
	}

	public final static DefaultServer test() {
		return new DefaultServer(TestBaseWebSocket.TEST_CONFIG, NettyEngine.test(), ServiceContext.createRoot());
	}

	@Override
	public void processPacket(WebSocketEngine engine, WebSocketConnector connector, WebSocketPacket packet) {

		if (!isAllowedProtocol(connector))
			return;

		PlugInResponse response = getPlugInChain().processPacket(connector, packet);

		List<WebSocketServerListener> listeners = getListeners();
		WebSocketServerEvent event = new WebSocketServerEvent(connector, this);
		for (WebSocketServerListener listener : listeners) {
			listener.processPacket(event, packet);
		}
	}

	private boolean isAllowedProtocol(WebSocketConnector connector) {
		RequestHeader lHeader = connector.getHeader();
		String lFormat = (lHeader != null ? lHeader.getFormat() : null);

		return (lFormat != null && (CommonConstants.WS_FORMAT_TEXT.equals(lFormat)) || CommonConstants.WS_FORMAT_JSON.equals(lFormat));
	}

	/**
	 * removes a plugin from the plugin chain of the server.
	 * 
	 * @param plugin
	 */
	public void removePlugIn(WebSocketPlugIn plugin) {
		getPlugInChain().removePlugIn(plugin);
	}

	@Override
	public void engineStarted(WebSocketEngine engine) {
		getPlugInChain().engineStarted(engine);
	}

	@Override
	public void engineStopped(WebSocketEngine engine) {
		getPlugInChain().engineStopped(engine);
	}

	@Override
	public void connectorStarted(WebSocketConnector connector) {
		super.connectorStarted(connector);

		// FilterResponse response = getFilterChain().processPacketIn(connector);
		// if (response.isRejected()) {
		// connector.sendPacket(response.getMessage());
		// connector.stopConnector(CloseReason.SERVER);
		// return;
		// }
		addConnector(connector);
		getPlugInChain().connectorStarted(connector);
	}

	@Override
	public void connectorStopped(WebSocketConnector connector, CloseReason reason) {
		// notify plugins that a connector has stopped, i.e. a client was disconnected.
		if (connector == null)
			return;

		super.connectorStopped(connector, reason);

		getPlugInChain().connectorStopped(connector, reason);

		// getFilterChain().processPacketOut(connector);

		removeConnector(connector);
	}

	@Override
	public WebSocketPlugInChain getPlugInChain() {
		return super.getPlugInChain();
	}

	@Override
	public void startServer() throws WebSocketException {
		try {
			getEngine().startEngine();
			getContext().onStart(this);
		} catch (InstanceCreationException e) {
			throw new WebSocketException(e.getMessage());
		}
	}

	@Override
	public void stopServer() throws WebSocketException {
		try {
			if (getEngine().isAlive())
				getEngine().stopEngine(CloseReason.SERVER);

			getContext().onEnd();
		} catch (InstanceCreationException e) {
			throw new WebSocketException(e.getMessage());
		}
	}

	@Override
	public boolean isAlive() {
		return getEngine().isAlive();
	}
	
	public FoundResult findConnectorByUserId(Set<String> userIds) {
		FoundResult result = FoundResult.create(userIds) ;
		Map<String, WebSocketConnector> definedMap = getConnectors().getDefinedMap() ;
		for (String userId : userIds) {
			WebSocketConnector found = definedMap.get(userId) ;
			result.addReuslt(userId, found);
		}
		
		return result;
	}

	
	public final ConnectorManager getConnectors(){
		return connectors ;
	}



}
