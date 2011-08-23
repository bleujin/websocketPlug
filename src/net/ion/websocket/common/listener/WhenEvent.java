package net.ion.websocket.common.listener;

import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.kit.WebSocketServerEvent;

public enum WhenEvent{
	OPEN {
		public void handle(WebSocketServerListener listener, WebSocketServerEvent event) {
			listener.processOpened(event) ;
		}
	} , PROCESS {
		public void handle(WebSocketServerListener listener, WebSocketServerEvent event) {
			listener.processPacket(event) ;
		}
	}, CLOSE{
		public void handle(WebSocketServerListener listener, WebSocketServerEvent event) {
			listener.processClosed(event) ;
		}
	}, UNKNOWN {
		public void handle(WebSocketServerListener listener, WebSocketServerEvent event) {
			;
		}
	};

	public abstract void handle(WebSocketServerListener listener, WebSocketServerEvent event) ;
}

