package net.ion.websocket.common.listener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.InfinityThread;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.plugin.MessagePacket;

public class TestChannelListener extends TestBaseWebSocket {


	public void testChannelListener() throws Exception {
		CountListener counter = new CountListener();
		WebSocketServerListener channelListenr = new ChannelListener(3, new WebSocketServerListener[]{counter, counter});
		server.addListener(channelListenr) ;
		server.startServer() ;
		
		SyncMockClient client = SyncMockClient.newTest() ;
		client.connect(uri) ;
		// for (int i = 0; i < 10; i++) {
			client.sendMessage(MessagePacket.PING) ;
		//}
		client.disconnect() ;
		Thread.sleep(500) ;
		//client.wait() ;
		server.stopServer() ;
		assertEquals(1*2 + 2*2, counter.getCount()) ;
	}

}
