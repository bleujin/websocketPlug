package net.ion.websocket.common.listener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.kit.WebSocketServerEvent;

public class ChannelListener implements WebSocketServerListener {

	private final int capacity ;
	private final BlockingQueue<WebSocketServerEvent> queue;

	private WorkerThread thread;


	public ChannelListener(int capacity, WebSocketServerListener... handler) {
		this.capacity = Math.max(capacity, 100) ;
		this.queue = new ArrayBlockingQueue<WebSocketServerEvent>(this.capacity);

		this.thread = new WorkerThread(handler, this);
		this.thread.start() ;
	}

	public void processOpened(WebSocketServerEvent event) {
		putEvent(event.setWhenEventType(WhenEvent.OPEN));
	}

	private void putEvent(WebSocketServerEvent event) {
		try {
			queue.put(event);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}
	}

	public void processPacket(WebSocketServerEvent event) {
		putEvent(event.setWhenEventType(WhenEvent.PROCESS));
	}

	public void processClosed(WebSocketServerEvent event) {
		putEvent(event.setWhenEventType(WhenEvent.CLOSE));
	}

	public WebSocketServerEvent takeEvent() throws InterruptedException {
		return queue.take();
	}

	
	
	
	public ChannelListener(WebSocketServerListener l1, int capacity) {
		this(capacity, l1) ;
	}
	public ChannelListener(WebSocketServerListener l1, WebSocketServerListener l2, int capacity) {
		this(capacity, l1, l2) ;
	}
	public ChannelListener(WebSocketServerListener l1, WebSocketServerListener l2, WebSocketServerListener l3, int capacity) {
		this(capacity, l1, l2, l3) ;
	}
	public ChannelListener(WebSocketServerListener l1, WebSocketServerListener l2, WebSocketServerListener l3, WebSocketServerListener l4, int capacity) {
		this(capacity, l1, l2, l3, l4) ;
	}
	public ChannelListener(WebSocketServerListener l1, WebSocketServerListener l2, WebSocketServerListener l3, WebSocketServerListener l4, WebSocketServerListener l5, int capacity) {
		this(capacity, l1, l2, l3, l4, l5) ;
	}
}
