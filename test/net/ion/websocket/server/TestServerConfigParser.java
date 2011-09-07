package net.ion.websocket.server;

import java.util.List;

import junit.framework.TestCase;
import net.ion.radon.core.config.XMLConfig;
import net.ion.websocket.common.api.EngineConfiguration;
import net.ion.websocket.common.api.ServerConfiguration;
import net.ion.websocket.common.config.ListenerInfo;
import net.ion.websocket.common.config.PlugInInfo;
import net.ion.websocket.common.config.ServerConfigParser;
import net.ion.websocket.server.context.ServiceContext;

public class TestServerConfigParser extends TestCase{

	private String configPath = "resource/config/test-server-config.xml" ;

	private ServerConfigParser parser ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		XMLConfig root = XMLConfig.create(configPath) ;
		// assertEquals(2, root.firstChild("plugins").children("plugin").size()) ;
		this.parser = ServerConfigParser.parse(root) ;
	}
	
	public void testBaseDir() throws Exception {
		assertEquals("./resource/config/embed-aradon-config.xml", parser.getAradonConfiguration().getConfigPath()) ;
	}
	
	public void testServerConfig() throws Exception {
		ServerConfiguration sconfig =  parser.getServerConfiguration() ;
		
		assertEquals("chatserver0", sconfig.getId()) ;
		assertEquals("Server", sconfig.getName()) ;
	}
	
	public void testEngineConfig() throws Exception {
		EngineConfiguration econfig = parser.getEngineConfiguration(0) ;
		List<String> domains = econfig.getDomains() ;
		
		assertEquals("localhost", domains.get(0)) ;
		assertEquals(16384, econfig.getMaxFramesize()) ;
		assertEquals("Netty", econfig.getName()) ;
		assertEquals(8787, econfig.getPort()) ;
	}
	
	public void testContextConfig() throws Exception {
		ServiceContext sc = parser.getContext() ;
		assertEquals("bleujin@i-on.net", sc.getAttributeObject("my.server.dev")) ;
	}
	
	public void testPluginConfig() throws Exception {
		List<PlugInInfo> plugins = parser.getPlugins() ;
		
		assertEquals(2, plugins.size()) ;
		assertEquals("broad", plugins.get(0).getName()) ;
		assertEquals("log", plugins.get(1).getName()) ;
	}
	
	public void testListenerConfig() throws Exception {
		List<ListenerInfo> plugins = parser.getListener() ;
		
		assertEquals(1, plugins.size()) ;
		assertEquals("debug", plugins.get(0).getName()) ;
		assertEquals("", plugins.get(0).getDescription()) ;
	}
	
}
