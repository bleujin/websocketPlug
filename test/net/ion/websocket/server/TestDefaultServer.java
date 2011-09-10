package net.ion.websocket.server;

import net.ion.websocket.TestBaseWebSocket;

public class TestDefaultServer extends TestBaseWebSocket{

	
	public void testIsStarted() throws Exception {
		assertEquals(false, server.isAlive()) ;
		server.startServer() ;
		assertEquals(true, server.isAlive()) ;
		
		
	}
	
	public void testConnector() throws Exception {
		assertEquals(0, server.getAllConnectors().length ) ;
		assertEquals(0, server.getPlugInChain().getPlugIns().size()) ;
		assertEquals(0, server.getListeners().size()) ;
		
	}
	
	public void testEngine() throws Exception {
		server.startServer() ;
		
		assertEquals(true, server.getEngine().isAlive()) ;
		server.stopServer() ;
		assertEquals(false, server.getEngine().isAlive()) ;
	}
}
