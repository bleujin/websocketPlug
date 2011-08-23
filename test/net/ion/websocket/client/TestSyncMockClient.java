package net.ion.websocket.client;

import java.net.URI;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.plugin.EchoPlugin;
import net.ion.websocket.plugin.IMessagePacket;
import net.ion.websocket.plugin.MessagePacket;

public class TestSyncMockClient extends TestBaseWebSocket{

	
	public void testConnect() throws Exception {
		server.startServer() ;
		URI myuri = new URI("ws://127.0.0.1:9000/mytopic/0;timeout=3600000");
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(myuri) ;

		client.disconnect() ;
	}
	
	public void testMistake() throws Exception {
		server.startServer() ;
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(uri) ;
		client.connect(uri) ;
		
		client.disconnect() ;
		assertEquals(Status.CLOSED, client.getStatus()) ;
	}
	
	public void testSendMessage() throws Exception {
		server.getPlugInChain().addPlugIn(new EchoPlugin()) ;
		server.startServer() ;
		
		URI myuri = new URI("ws://127.0.0.1:9000/mytopic/0;timeout=3600000");
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(myuri) ;

		
		final List<String> list = ListUtil.newList() ;
		MessageListener listener = new MessageListener() {
			public void onMessage(IMessagePacket frame) {
				list.add(frame.getFullString()) ;
			}
		};
		client.getMessageListener().register(listener) ;
		client.sendMessage(MessagePacket.PING) ;
		
		client.await(50) ;
		assertEquals(true, list.size() >= 1) ;
		
		client.disconnect() ;
	}
	
	
	public void testClose() throws Exception {
		server.getPlugInChain().addPlugIn(new EchoPlugin()) ;
		server.startServer() ;
		
		URI myuri = new URI("ws://127.0.0.1:9000/mytopic/0;timeout=3600000");
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(myuri) ;

		final List<String> list = ListUtil.newList() ;
		CloseListener listener = new CloseListener() {
			public void onClose(CloseReason creason) {
				list.add(creason.toString()) ;
			}
		};
		client.getMessageListener().register(listener) ;
		
		client.disconnect() ;
		assertEquals(true, list.size() == 1) ;
	}
	
	
}
