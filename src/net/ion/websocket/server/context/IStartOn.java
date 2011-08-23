package net.ion.websocket.server.context;

import net.ion.websocket.common.api.WebSocketServer;

public interface IStartOn {

	public void onStart(ServiceContext serviceContext, WebSocketServer server)  ;
}
