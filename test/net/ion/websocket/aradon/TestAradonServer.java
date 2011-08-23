package net.ion.websocket.aradon;

import net.ion.radon.core.Aradon;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.common.config.AradonConfiguration;
import net.ion.websocket.common.plugin.AllBroadCastPlugIn;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

public class TestAradonServer extends TestBaseWebSocket{

	public void testAradonRun() throws Exception {
		EmbedAradonServer runner = new EmbedAradonServer(AradonConfiguration.test());
		runner.init() ;
		Aradon aradon = runner.getAradon()  ;
		Request request = new Request(Method.GET, "riap://component/") ;
		
		Response res = aradon.handle(request) ;
		assertEquals(200, res.getStatus().getCode()) ;
	}
	
	
	public void testRun() throws Exception {
		EmbedAradonServer mr = new EmbedAradonServer(AradonConfiguration.test());
		mr.startAradon(9005) ;
		server.getContext().putAttribute("aradon.monitor", mr) ;
		server.getPlugInChain().addPlugIn(new AllBroadCastPlugIn()); 
		
		new net.ion.radon.InfinityThread().startNJoin() ;
	}
	
}
