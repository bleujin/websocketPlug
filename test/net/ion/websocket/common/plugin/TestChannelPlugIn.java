package net.ion.websocket.common.plugin;

import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.plugin.MessagePacket;

public class TestChannelPlugIn extends TestBaseWebSocket{

	
	public void testProcess() throws Exception {
		server.startServer() ;
		
		CounterPlugIn cp = new CounterPlugIn() ;
		server.getPlugInChain().addPlugIn(new ChannelPlugIn(100, cp, cp)) ;
		
		SyncMockClient client = SyncMockClient.newTest() ;
		super.connectAsUser(client, "bleujin") ;
		client.sendMessage(MessagePacket.PING) ;
		
		client.await(100) ;
		assertEquals(2, cp.getConnectorCounter()) ;
		assertEquals(2, cp.getProcessCounter()) ;

		client.disconnect() ;
	}
}
