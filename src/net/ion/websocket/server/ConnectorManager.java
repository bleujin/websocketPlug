package net.ion.websocket.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.server.IConnectorManager;

public class ConnectorManager implements IConnectorManager{
	private final Map<String, WebSocketConnector> connById = MapUtil.newMap(); // MapUtil.newMap();
	private final Map<String, WebSocketConnector> connByUserName = MapUtil.newMap(); // MapUtil.newMap() ;
	private final Map<String, WebSocketConnector> connByDefined= MapUtil.newMap();
	
	private final String definedProperty ;
	public ConnectorManager(String defindProperty){
		this.definedProperty = defindProperty ;
	}
	
	public void add(WebSocketConnector connector){
		connById.put(connector.getId(), connector);
		connByUserName.put(connector.getUsername(), connector) ;
		connByDefined.put(connector.getString(definedProperty), connector) ;
	}
	
	public Collection<WebSocketConnector> values() {
		return Collections.unmodifiableCollection(connById.values());
	}

	public WebSocketConnector getById(String id){
		return connById.get(id) ;
	}
	
	public WebSocketConnector getByUserName(String userName){
		return connByUserName.get(userName) ;
	}
	
	public void remove(WebSocketConnector connector){
		connById.remove(connector.getId()) ;
		connByUserName.remove(connector.getUsername()) ;
		connByDefined.remove(connector.getString(definedProperty)) ;
	}

	public int size() {
		return connById.size();
	}

	public boolean containsById(String connId) {
		return connById.containsKey(connId);
	}

	public WebSocketConnector[] getAllConnectors() {
		return values().toArray(new WebSocketConnector[0]);
	}
	
	public Map<String, WebSocketConnector> getDefinedMap(){
		return Collections.unmodifiableMap(connByDefined) ;
	}
}
