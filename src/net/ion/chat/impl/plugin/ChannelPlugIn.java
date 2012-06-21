package net.ion.chat.impl.plugin;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import net.ion.chat.api.BasePlugIn;
import net.ion.chat.api.CloseReason;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.api.ChatPlugIn;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserConnector;
import net.ion.chat.util.IMessagePacket;
import net.ion.radon.core.TreeContext;

public class ChannelPlugIn extends BasePlugIn{

	private final int capacity ;
	private final BlockingQueue<ProcessEvent> queue;

	private WorkerThread thread;


	public ChannelPlugIn(int capacity, ChatPlugIn... plugins) {
		this.capacity = Math.max(capacity, 200) ;
		this.queue = new ArrayBlockingQueue<ProcessEvent>(this.capacity);

		this.thread = new WorkerThread(plugins, this);
		this.thread.start() ;
	}
	
	@Override public void connectorStarted(IUserConnector conn) {
		putEvent(ProcessEvent.connStart(conn));
	}
	@Override public void connectorStopped(IUserConnector conn, CloseReason creason) {
		putEvent(ProcessEvent.connEnd(conn, creason));
	}
	@Override public void engineStopped(TreeContext context) {
		putEvent(ProcessEvent.engineEnd(context));
	}
	@Override public void engineStarted(TreeContext context) {
		putEvent(ProcessEvent.engineEnd(context));
	}
	public void processPacket(PlugInResponse response, IUserConnector conn, IMessagePacket packet) {
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
	
	

	
	
	
	public ChannelPlugIn(ChatPlugIn p1, int capacity) {
		this(capacity, p1) ;
	}
	public ChannelPlugIn(ChatPlugIn p1, ChatPlugIn p2, int capacity) {
		this(capacity, p1, p2) ;
	}
	public ChannelPlugIn(ChatPlugIn p1, ChatPlugIn p2, ChatPlugIn p3, int capacity) {
		this(capacity, p1, p2, p3) ;
	}
	public ChannelPlugIn(ChatPlugIn p1, ChatPlugIn p2, ChatPlugIn p3, ChatPlugIn p4, int capacity) {
		this(capacity, p1, p2, p3, p4) ;
	}
	public ChannelPlugIn(ChatPlugIn p1, ChatPlugIn p2, ChatPlugIn p3, ChatPlugIn p4, ChatPlugIn p5, int capacity) {
		this(capacity, p1, p2, p3, p4, p5) ;
	}
	public ChannelPlugIn(ChatPlugIn p1, ChatPlugIn p2, ChatPlugIn p3, ChatPlugIn p4, ChatPlugIn p5, ChatPlugIn p6, int capacity) {
		this(capacity, p1, p2, p3, p4, p5, p6) ;
	}
	public ChannelPlugIn(ChatPlugIn p1, ChatPlugIn p2, ChatPlugIn p3, ChatPlugIn p4, ChatPlugIn p5, ChatPlugIn p6, ChatPlugIn p7, int capacity) {
		this(capacity, p1, p2, p3, p4, p5, p6, p7) ;
	}

}
