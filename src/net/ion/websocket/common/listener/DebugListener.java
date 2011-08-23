package net.ion.websocket.common.listener;

import java.util.concurrent.atomic.AtomicInteger;

import net.ion.framework.util.Debug;
import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.kit.WebSocketServerEvent;

public class DebugListener implements WebSocketServerListener {

	private AtomicInteger count = new AtomicInteger() ;
	public void processClosed(WebSocketServerEvent event) {
		count.incrementAndGet() ;
		Debug.line(getClass().getCanonicalName(), "process Closed", event);
	}

	public void processOpened(WebSocketServerEvent event) {
		count.incrementAndGet() ;
		Debug.line(getClass().getCanonicalName(), "process Opened", event);
	}

	public void processPacket(WebSocketServerEvent event) {
		count.incrementAndGet() ;
		Debug.line(getClass().getCanonicalName(), "process Packet", event, event.getPacket());
	}

	public int getCount() {
		return count.get() ;
	}
}
