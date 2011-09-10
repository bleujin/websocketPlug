package net.ion.websocket.aradon.embed;

import java.util.Timer;
import java.util.TimerTask;

import net.ion.radon.core.let.AbstractServerResource;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.kit.WebSocketException;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;

public class SuicideLet extends AbstractServerResource {

	
	@Get
	public String getMyName(){
		return "Hello " + getInnerRequest().getFormParameter().get("name");
	}
	
	@Delete
	public String suicide(){
		
		long timeoutMili = Math.max(getInnerRequest().getParameterAsInteger("timeout"), 1) * 1000L ;
		
		final WebSocketServer server = getMySectionService().getAradon().getServiceContext().getAttributeObject(WebSocketServer.class.getCanonicalName(), WebSocketServer.class) ;
		new Timer().schedule(new TimerTask(){
			
			@Override public void run() {
				try {
					server.stopServer() ;
				} catch (WebSocketException e) {
					e.printStackTrace();
				}
				System.exit(0) ;
			}
			
		}, timeoutMili) ;
		
		return timeoutMili + "(ms) shutdown.." ;
	}
}
