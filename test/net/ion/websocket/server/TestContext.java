package net.ion.websocket.server;

import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.server.context.IEndOn;
import net.ion.websocket.server.context.IStartOn;
import net.ion.websocket.server.context.ServiceContext;

public class TestContext extends TestBaseWebSocket{

	public void testOnStart() throws Exception {
		
		OnStartImpl impl = new OnStartImpl();
		server.getContext().putAttribute("onstart", impl) ;
		
		assertEquals(false, impl.isStarted()) ;
		server.startServer();
		assertEquals(true, impl.isStarted()) ;
		server.stopServer();
		assertEquals(false, impl.isStarted()) ;
	}
	
	
	
	
}

class OnStartImpl implements IStartOn, IEndOn{

	private boolean started = false ;
	
	public void onStart(ServiceContext serviceContext, WebSocketServer server) {
		this.started = true ;
	}
	
	public boolean isStarted(){
		return started ;
	}

	public void onEnd() {
		this.started = false ;
	}
}
