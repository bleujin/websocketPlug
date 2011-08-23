package net.ion.websocket.common.listener;

import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.common.kit.WebSocketServerEvent;

public class WorkerThread extends Thread{

	private ChannelListener channel ;
	private WebSocketServerListener[] listeners ;
	public WorkerThread(WebSocketServerListener[] listeners, ChannelListener channel) {
		this.listeners = listeners ;
		this.channel = channel ;

	}
	
	public void run(){
		while(true){
			try {
				WebSocketServerEvent event = channel.takeEvent() ;
				for (WebSocketServerListener listener : listeners) {
					event.handleEvent(listener) ;
				}
			} catch (InterruptedException ignore) {
				ignore.printStackTrace();
			}
			
		}
	}
}
