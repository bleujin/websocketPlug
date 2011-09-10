package net.ion.websocket.common.listener;

import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.plugin.MessagePacket;

public class TestListener extends TestBaseWebSocket{

	
	public void testListener() throws Exception {
		CountListener listener = new CountListener();
		server.addListener(listener) ;
		server.startServer() ;
		
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(uri) ;
		client.sendMessage(MessagePacket.PING) ;
		client.disconnect() ;
		client.await(500) ;
		server.stopServer() ;
		assertEquals(3, listener.getCount()) ;
	}

}
