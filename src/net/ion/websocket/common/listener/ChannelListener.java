package net.ion.websocket.common.listener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.ecs.xhtml.applet;
import org.apache.ecs.xhtml.li;

import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.kit.WebSocketServerEvent;

public class ChannelListener implements WebSocketServerListener {

	private final int capacity ;
	private final BlockingQueue<ServerEvent> queue;

	private WorkerThread thread;


	public ChannelListener(int capacity, WebSocketServerListener... handler) {
		this.capacity = Math.max(capacity, 100) ;
		this.queue = new ArrayBlockingQueue<ServerEvent>(this.capacity);

		this.thread = new WorkerThread(handler, this);
		this.thread.start() ;
	}

	public void processOpened(WebSocketServerEvent event) {
		putEvent(ServerEvent.opened(event));
	}

	private void putEvent(ServerEvent sevent) {
		try {
			queue.put(sevent);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}
	}

	public void processPacket(WebSocketServerEvent event, WebSocketPacket packet) {
		putEvent(ServerEvent.process(event, packet));
	}

	public void processClosed(WebSocketServerEvent event) {
		putEvent(ServerEvent.closed(event));
	}

	public ServerEvent takeEvent() throws InterruptedException {
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

enum WhenEvent{
	OPEN , PROCESS , CLOSE, UNKNOWN ;
}

class ServerEvent {

	private WebSocketServerEvent event ;
	private WebSocketPacket packet ;
	private WhenEvent wevent ;
	private ServerEvent(WebSocketServerEvent event, WebSocketPacket packet, WhenEvent wevent) {
		this.event = event ;
		this.packet = packet ;
		this.wevent = wevent ;
	}

	public static ServerEvent process(WebSocketServerEvent event, WebSocketPacket packet) {
		return new ServerEvent(event, packet, WhenEvent.CLOSE);
	}

	public static ServerEvent closed(WebSocketServerEvent event) {
		return new ServerEvent(event, WebSocketPacket.BLANK, WhenEvent.CLOSE);
	}

	public static ServerEvent opened(WebSocketServerEvent event) {
		return new ServerEvent(event, WebSocketPacket.BLANK, WhenEvent.OPEN);
	}

	public WebSocketPacket getPacket(){
		return packet ;
	}
	
	public WebSocketServerEvent getEvent(){
		return event ;
	}

	public void handleEvent(WebSocketServerListener listener) {
		if (this.wevent == WhenEvent.CLOSE){
			listener.processClosed(event) ;
		} else if (this.wevent == WhenEvent.OPEN){
			listener.processOpened(event) ;
		} else if (this.wevent == WhenEvent.OPEN){
			listener.processPacket(event, packet) ;
		}
	}
	
}
