package net.ion.chat.server;

import java.net.SocketAddress;

import net.ion.chat.api.CloseReason;
import net.ion.chat.util.IMessagePacket;
import net.ion.nradon.HttpRequest;

public interface IUserConnector {

	public String getString(String key)  ;

	public Object data(String key)  ;

	public String getEngineId() ;
	
	public IUserConnector send(String msg)  ;
	
	public IUserConnector send(IMessagePacket msg) ;

	public IUserConnector data(String key, Object val)  ;

	public void stopConnector(CloseReason reason) ; 

	public SocketAddress getRemoteHost() ;

	public String getUserId() ;

	public long getAuthId() ;

	public Object getUsername() ;
	
	public HttpRequest httpRequest() ;

	public String getSessionId() ; 

	public String getClientIP() ;

	public void close() ;

	public String getClientPort() ;

	public UserBean getUserBean() ;
	
	public boolean isAuthUser() ;
	
}
