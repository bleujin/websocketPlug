package net.ion.websocket.server;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.SetUtil;
import net.ion.websocket.common.api.WebSocketConnector;

public class FoundResult {

	private Set<String> allTargets;
	private Map<String, WebSocketConnector> found = MapUtil.newMap();
	private Set<String> notFoundUser = SetUtil.newSet() ;
	
	private FoundResult(Set<String> allTargets) {
		this.allTargets = allTargets;
	}

	public static FoundResult create(Set<String> allTargets) {
		return new FoundResult(allTargets);
	}

	void addReuslt(String userId, WebSocketConnector conn) {
		if (conn == null) {
			notFoundUser.add(userId) ;
		} else {
			found.put(userId, conn);
		}
	}

	public Set<String> getNotExistUsers() {
		return notFoundUser ;
	}

	public WebSocketConnector[] getExistConnector() {
		return found.values().toArray(new WebSocketConnector[0]) ;
	}

}
