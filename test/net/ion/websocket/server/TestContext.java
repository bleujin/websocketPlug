package net.ion.websocket.server;

import net.ion.radon.Options;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.server.context.IEndOn;
import net.ion.websocket.server.context.IStartOn;
import net.ion.websocket.server.context.ServiceContext;

public class TestContext extends TestBaseWebSocket{

	public void testOnStart() throws Exception {
		
		OnStartImpl impl = new OnStartImpl();
		server.getContext().putAttribute("onstart", impl) ;
		
		assertEquals(false, impl.isStarted()) ;
		server.startServer();
		assertEquals(true, impl.isStarted()) ;
		server.stopServer();
		assertEquals(false, impl.isStarted()) ;
	}
	
	
	public void testLoadObject() throws Exception {
		ServerNodeRunner srunner = new ServerNodeRunner(new Options(new String[]{"-config:resource/config/hello-config.xml"})) ;
		DefaultServer ds = srunner.getServer() ;

		ServiceContext sc = ds.getContext() ;
		assertEquals("bleujin@i-on.net", sc.getAttributeObject("my.server.dev")) ;
		assertEquals(45, sc.getAttributeObject("my.index.count")) ;
	}
	
	public void testApplicationScope() throws Exception {
		ServerNodeRunner srunner = new ServerNodeRunner(new Options(new String[]{"-config:resource/config/hello-config.xml"})) ;
		DefaultServer ds = srunner.getServer() ;

		ServiceContext sc = ds.getContext() ;
		StringBuffer appScope = (StringBuffer)(sc.getAttributeObject("my.sb.application"));
		assertEquals("Hello", appScope.toString()) ;
		
		appScope.append(" World") ;

		assertEquals("Hello World", ((StringBuffer)(sc.getAttributeObject("my.sb.application"))).toString() ) ;
	}

	public void testRequestScope() throws Exception {
		ServerNodeRunner srunner = new ServerNodeRunner(new Options(new String[]{"-config:resource/config/hello-config.xml"})) ;
		DefaultServer ds = srunner.getServer() ;

		ServiceContext sc = ds.getContext() ;
		StringBuffer reqScope = (StringBuffer)(sc.getAttributeObject("my.sb.request"));
		assertEquals("Hello", reqScope.toString()) ;
		
		reqScope.append(" World") ;

		assertEquals("Hello", ((StringBuffer)(sc.getAttributeObject("my.sb.request"))).toString() ) ;
	}

	
	
}

class OnStartImpl implements IStartOn, IEndOn{

	private boolean started = false ;
	
	public void onStart(ServiceContext serviceContext, WebSocketServer server) {
		this.started = true ;
	}
	
	public boolean isStarted(){
		return started ;
	}

	public void onEnd() {
		this.started = false ;
	}
}
