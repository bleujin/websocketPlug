package net.ion.websocket.server;

import junit.framework.TestCase;
import net.ion.radon.Options;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.TreeContext;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

public class TestEmbedAradon extends TestCase{

	
	private ServerNodeRunner runner ; 
	@Override protected void setUp() throws Exception {
		super.setUp();
		runner = new ServerNodeRunner(new Options(new String[]{"-config:resource/config/server-config.xml"}));
		runner.start();
	}
	
	@Override protected void tearDown() throws Exception {
		runner.stop() ;
		super.tearDown();
	}
	

	public void testConfirmAradon() throws Exception {
		Aradon aradon = runner.getAradon() ;
		SectionService ss = aradon.getChildService("") ;
		
		assertEquals(3, ss.getChildren().size()) ;
	}
	
	
	public void testAradonContext() throws Exception {
		Aradon aradon = runner.getAradon() ;
		TreeContext context =  aradon.getServiceContext() ;
		
		assertEquals("bleujin@i-on.net", context.getAttributeObject("let.contact.email", String.class)) ;
	}
	
	
	public void testMonitorLet() throws Exception {
		Aradon aradon = runner.getAradon() ;
		Request request = new Request(Method.GET, "riap://component/monitor") ;
		
		Response response = aradon.handle(request) ;
		assertEquals(200, response.getStatus().getCode()) ;
	}
	
	
	

}
