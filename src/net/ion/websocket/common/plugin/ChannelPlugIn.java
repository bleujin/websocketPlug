package net.ion.websocket.common.plugin;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;

public class ChannelPlugIn extends BasePlugIn{

	private final int capacity ;
	private final BlockingQueue<ProcessEvent> queue;

	private WorkerThread thread;


	public ChannelPlugIn(int capacity, WebSocketPlugIn... plugins) {
		this.capacity = Math.max(capacity, 200) ;
		this.queue = new ArrayBlockingQueue<ProcessEvent>(this.capacity);

		this.thread = new WorkerThread(plugins, this);
		this.thread.start() ;
	}
	
	@Override public void connectorStarted(WebSocketConnector conn) {
		putEvent(ProcessEvent.connStart(conn));
	}
	@Override public void connectorStopped(WebSocketConnector conn, CloseReason creason) {
		putEvent(ProcessEvent.connEnd(conn, creason));
	}
	@Override public void engineStopped(WebSocketEngine engine) {
		putEvent(ProcessEvent.engineEnd(engine));
	}
	@Override public void engineStarted(WebSocketEngine engine) {
		putEvent(ProcessEvent.engineEnd(engine));
	}
	@Override public void processPacket(PlugInResponse response, WebSocketConnector conn, WebSocketPacket packet) {
		putEvent(ProcessEvent.process(response, conn, packet));
	}
	
	
	private void putEvent(ProcessEvent event) {
		try {
			queue.put(event);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}
	}

	public ProcessEvent takeEvent() throws InterruptedException {
		return queue.take();
	}
	
	

	
	
	
	public ChannelPlugIn(WebSocketPlugIn p1, int capacity) {
		this(capacity, p1) ;
	}
	public ChannelPlugIn(WebSocketPlugIn p1, WebSocketPlugIn p2, int capacity) {
		this(capacity, p1, p2) ;
	}
	public ChannelPlugIn(WebSocketPlugIn p1, WebSocketPlugIn p2, WebSocketPlugIn p3, int capacity) {
		this(capacity, p1, p2, p3) ;
	}
	public ChannelPlugIn(WebSocketPlugIn p1, WebSocketPlugIn p2, WebSocketPlugIn p3, WebSocketPlugIn p4, int capacity) {
		this(capacity, p1, p2, p3, p4) ;
	}
	public ChannelPlugIn(WebSocketPlugIn p1, WebSocketPlugIn p2, WebSocketPlugIn p3, WebSocketPlugIn p4, WebSocketPlugIn p5, int capacity) {
		this(capacity, p1, p2, p3, p4, p5) ;
	}
	public ChannelPlugIn(WebSocketPlugIn p1, WebSocketPlugIn p2, WebSocketPlugIn p3, WebSocketPlugIn p4, WebSocketPlugIn p5, WebSocketPlugIn p6, int capacity) {
		this(capacity, p1, p2, p3, p4, p5, p6) ;
	}
	public ChannelPlugIn(WebSocketPlugIn p1, WebSocketPlugIn p2, WebSocketPlugIn p3, WebSocketPlugIn p4, WebSocketPlugIn p5, WebSocketPlugIn p6, WebSocketPlugIn p7, int capacity) {
		this(capacity, p1, p2, p3, p4, p5, p6, p7) ;
	}

}
