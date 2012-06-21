package net.ion.chat.server;

import java.net.SocketAddress;

import net.ion.chat.api.CloseReason;
import net.ion.chat.api.ChatConstants;
import net.ion.chat.handler.ChatEngine;
import net.ion.chat.util.IMessagePacket;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.WebSocketConnection;

public class UserConnector implements IUserConnector {

	private ChatEngine engine ;
	private WebSocketConnection conn ;
	private UserConnector(ChatEngine engine, WebSocketConnection conn) {
		this.engine = engine ;
		this.conn = conn ;
	}

	public final static UserConnector create(ChatEngine talkEngine, WebSocketConnection conn){
		return new UserConnector(talkEngine, conn) ;
	}
	
	public boolean equals(Object obj){
		if (obj instanceof UserConnector){
			UserConnector that = (UserConnector) obj ;
			return this.conn.equals(that.conn) ;
		} 
		return false ;
	}
	
	public int hashCode(){
		return conn.hashCode() ;
	}

	public String getString(String key) {
		return ObjectUtil.toString(conn.data(key));
	}

	public Object data(String key) {
		return conn.data(key);
	}

	public String getEngineId(){
		return engine.getId() ;
	}
	
	public UserConnector send(String msg) {
		conn.send(msg) ;
		return this ;
	}
	
	public UserConnector send(IMessagePacket msg){
		return send(msg.getFullString()) ;
	}

	public UserConnector data(String key, Object val) {
		conn.data(key, val) ;
		return this ;
	}

	public void stopConnector(CloseReason reason) {
		engine.onClose(conn) ;
	}

	public SocketAddress getRemoteHost() {
		return conn.httpRequest().remoteAddress() ;
	}

	public String getUserId() {
		return getString(ChatConstants.VAR_USERID);
	}

	public long getAuthId() {
		return Long.parseLong(getString(ChatConstants.VAR_AUTHID));
	}

	public Object getUsername() {
		return getString(ChatConstants.VAR_USERNAME);
	}
	
	public HttpRequest httpRequest(){
		return conn.httpRequest() ;
	}

	public String getSessionId() {
		return getString(ChatConstants.VAR_SESSIONID);
	}

	public String getClientIP() {
		String ipExpr = httpRequest().remoteAddress().toString() ;
		return StringUtil.substringBetween(ipExpr, "/", ":");
	}

	public void close() {
		conn.close() ;
	}

	public String getClientPort() {
		String ipExpr = httpRequest().remoteAddress().toString() ;
		return StringUtil.substringAfter(ipExpr, ":");
	}

	public UserBean getUserBean() {
		return (UserBean)conn.data(UserBean.class.getCanonicalName()) ; 
	}

	public boolean isAuthUser(){
		return true ;		
	}
	
}
