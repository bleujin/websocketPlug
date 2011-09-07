package net.ion.websocket.client;

import java.net.URI;

import net.ion.websocket.TestBaseWebSocket;

public class TestSyncMockClient extends TestBaseWebSocket{

	
	public void testConnect() throws Exception {
		server.startServer() ;
		URI myuri = new URI("ws://127.0.0.1:9000/mytopic/0;timeout=3600000");
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(myuri) ;

		client.disconnect() ;
	}
	
	public void testDisconnect() throws Exception {
		server.startServer() ;
		URI myuri = new URI("ws://127.0.0.1:9000/mytopic/0;timeout=3600000");
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(myuri) ;

		assertEquals(true, client.isConnected()) ;
		client.disconnect() ;
		assertEquals(false, client.isConnected()) ;
	}
	
}
