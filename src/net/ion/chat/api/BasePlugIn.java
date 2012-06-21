package net.ion.chat.api;

import net.ion.chat.handler.ChatEngine;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserConnector;
import net.ion.framework.util.Closure;
import net.ion.radon.core.TreeContext;

public abstract class BasePlugIn implements ChatPlugIn, ChatConstants{
	
	private ChatEngine engine ;
	public void setEngine(ChatEngine engine){
		this.engine = engine ;
	}
	
	public ChatEngine getEngine(){
		return this.engine ;
	} 
	
	public void engineStopped(TreeContext context) {}
	public void engineStarted(TreeContext context) {}
	
	
	public void connectorStarted(IUserConnector conn){}
	public void connectorStopped(IUserConnector conn, CloseReason creason){}
	
	public void each(Closure<IUserConnector> clos){
		engine.eachConn(clos) ;
	}
}
