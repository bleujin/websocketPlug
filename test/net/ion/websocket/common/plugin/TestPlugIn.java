package net.ion.websocket.common.plugin;

import java.util.ArrayList;
import java.util.List;

import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.common.PacketConstant;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.plugin.MessagePacket;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;

public class TestPlugIn extends TestBaseWebSocket implements PacketConstant {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// server.getPlugInChain().addPlugIn(new DebugPlugIn());
	}
	
	public void testCounterPlugIn() throws Exception {
		CounterPlugIn counter = new CounterPlugIn();
		server.getPlugInChain().addPlugIn(counter);
		server.startServer() ;

		// clientRun("{command:'Login',userid:'bleujin'}");
		SyncMockClient client = SyncMockClient.newTest();
		client.connect(uri);
		client.sendMessage(MessagePacket.PING);
		client.disconnect();
		
		client.await(100) ;
		assertEquals(1, counter.getProcessCounter());
	}


	public void testMultiClient() throws Exception {
		CounterPlugIn counter = new CounterPlugIn();
		server.getPlugInChain().addPlugIn(counter);
		server.startServer() ;

		List<Thread> clients = new ArrayList<Thread>();
		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread() {
				public void run() {
					try {
						MessagePacket mp = MessagePacket.create().inner(BODY).put("greeting", "Hi").toRoot();
						SyncMockClient mock = SyncMockClient.newTest();
						mock.connect(uri);
						mock.sendMessage(mp);
						mock.disconnect();
					} catch (WebSocketException e) {
						e.printStackTrace();
					}
				}
			};

			clients.add(thread);
		}

		CollectionUtils.forAllDo(clients, new Closure() {
			public void execute(Object t) {
				((Thread) t).start();
			}
		});
		for (Thread thread : clients) {
			thread.join();
		}

		Thread.sleep(200) ;
		assertEquals(true, counter.getProcessCounter() > 8);
	}

}
