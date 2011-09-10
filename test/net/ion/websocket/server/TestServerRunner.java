package net.ion.websocket.server;

import java.net.URI;
import java.util.List;

import javolution.util.FastList;
import junit.framework.TestCase;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.Options;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.common.api.EngineConfiguration;
import net.ion.websocket.common.api.Selector;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.plugin.MessagePacket;
import net.ion.websocket.server.context.ServiceContext;
import net.ion.websocket.server.engine.netty.NettyEngine;

import org.apache.commons.configuration.ConfigurationException;

public class TestServerRunner extends TestCase {

	public void testConnectUriParamter() throws Exception {
		WebSocketServer server = new DefaultServer(TestBaseWebSocket.TEST_CONFIG, new NettyEngine(TEST_ENGINE_CONFIG), ServiceContext.createRoot()) ;
		server.startServer() ;
		
		assertEquals("/{userId}/{sessionId}/{params}", server.getConfiguration().getURIPath());

		SyncMockClient client = SyncMockClient.newTest();
		client.connect(new URI("ws://127.0.0.1:9005/bleujin/12312122/timeout=3600000"));
		client.sendMessage(MessagePacket.PING) ;
		
		Thread.sleep(500) ;
		WebSocketConnector[] conns = server.getAllConnectors() ;
		assertEquals(1, conns.length) ;
		assertEquals("bleujin", conns[0].getString("userId")) ;
		
		WebSocketConnector find = server.findConnector(new Selector(){
			public boolean isTrueCondition(WebSocketConnector conn) {
				return "bleujin".equals(conn.getString("userId"));
			}}) ;
		
		assertTrue(find != null) ;
		assertEquals("bleujin", find.getString("userId")) ;
		
		assertTrue(conns[0] == find) ;
		
		client.disconnect() ;
		server.stopServer() ;
	}
	

	public void xtestNotFoundConfigFile() throws Exception {
		ServerNodeRunner runner = null ;
		try {
			runner = new ServerNodeRunner(new Options(new String[]{"-config:resource/config/server-config"}));
			runner.start();
			fail();
		} catch (ConfigurationException ignore) {
		} finally {
			if (runner != null) runner.stop() ;
		}
	}
	
	public void xtestStart() throws Exception {
		Options option = new Options(new String[]{"-config:resource/config/server-config.xml"}) ;
		ServerNodeRunner runner = new ServerNodeRunner(option);
		runner.start();

		assertEquals("/{userId}/{sessionId}/{params}", runner.getServer().getConfiguration().getURIPath());
		new InfinityThread().startNJoin() ;
	}

	
	private static final EngineConfiguration TEST_ENGINE_CONFIG = new EngineConfiguration() {

		public List<String> getDomains() {
			List<String> domains = new FastList<String>();
			domains.add("localhost");
			return domains;
		}

		public String getJar() {
			return null;
		}

		public int getMaxFramesize() {
			return CommonConstants.DEFAULT_MAX_FRAME_SIZE;
		}

		public int getPort() {
			return 9005;
		}

		public int getTimeout() {
			return CommonConstants.DEFAULT_TIMEOUT;
		}

		public String getId() {
			return "netty0";
		}

		public String getName() {
			return "Netty";
		}
	};
}
