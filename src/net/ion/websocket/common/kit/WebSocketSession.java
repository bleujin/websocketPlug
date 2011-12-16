/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ion.websocket.common.kit;

/**
 *
 * @author aschulze
 */
public class WebSocketSession {

	private String sessionId = null;

	public WebSocketSession() {
	}

	public WebSocketSession(String aSessionId) {
		sessionId = aSessionId;
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
