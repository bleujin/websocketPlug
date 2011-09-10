package net.ion.websocket.client;

import java.net.URI;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.plugin.EchoPlugIn;
import net.ion.websocket.plugin.IMessagePacket;
import net.ion.websocket.plugin.MessagePacket;

public class TestClientListener extends TestBaseWebSocket{

	public void testOnMessage() throws Exception {
		server.getPlugInChain().addPlugIn(EchoPlugIn.SELF) ;
		server.startServer() ;
		
		URI myuri = new URI("ws://127.0.0.1:9000/mytopic/0;timeout=3600000");
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(myuri) ;

		
		final List<String> list = ListUtil.newList() ;
		MessageListener listener = new MessageListener() {
			public void onMessage(IMessagePacket frame) {
				Debug.line(frame.getFullString()) ;
				list.add(frame.getFullString()) ;
			}
		};
		client.getMessageListener().register(listener) ;
		client.sendMessage(MessagePacket.PING) ;
		
		client.await(50) ;
		assertEquals(1, list.size()) ;
		
		client.disconnect() ;
	}


	public void testOnClose() throws Exception {
		server.getPlugInChain().addPlugIn(EchoPlugIn.SELF) ;
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
