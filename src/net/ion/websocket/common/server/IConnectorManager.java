package net.ion.websocket.common.server;

import net.ion.websocket.common.api.WebSocketConnector;

public interface IConnectorManager {

	public void add(WebSocketConnector connector);

	public WebSocketConnector getById(String id);

	public WebSocketConnector getByUserName(String userName);

	void remove(WebSocketConnector connector);

	public int size();

	public boolean containsById(String connId);

	public WebSocketConnector[] getAllConnectors();

}
