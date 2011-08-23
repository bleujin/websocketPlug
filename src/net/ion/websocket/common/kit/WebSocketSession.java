/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ion.websocket.common.kit;

import net.ion.websocket.common.api.ObjectId;

/**
 * 
 * @author aschulze
 */
public class WebSocketSession {

	private String sessionId = null;

	public WebSocketSession() {
		this(new ObjectId().toString()) ;
	}

	public WebSocketSession(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
