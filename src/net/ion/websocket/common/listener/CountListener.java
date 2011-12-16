package net.ion.websocket.common.listener;

import java.util.concurrent.atomic.AtomicInteger;

import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.kit.WebSocketServerEvent;

public class CountListener implements WebSocketServerListener {

	private AtomicInteger count = new AtomicInteger() ;
	public void processClosed(WebSocketServerEvent event) {
		count.incrementAndGet() ;
	}

	public void processOpened(WebSocketServerEvent event) {
		count.incrementAndGet() ;
	}

	public void processPacket(WebSocketServerEvent event, WebSocketPacket packet) {
		count.incrementAndGet() ;
	}

	public int getCount() {
		return count.get() ;
	}
}
