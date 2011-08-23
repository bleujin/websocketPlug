package net.ion.websocket.common.plugin;

import net.ion.websocket.common.api.WebSocketPlugIn;

public class WorkerThread extends Thread{

	private ChannelPlugIn channel ;
	private WebSocketPlugIn[] plugins ;
	public WorkerThread(WebSocketPlugIn[] plugins, ChannelPlugIn channel) {
		this.plugins = plugins ;
		this.channel = channel ;

	}
	
	public void run(){
		while(true){
			try {
				ProcessEvent event = channel.takeEvent() ;
				for (WebSocketPlugIn plugin : plugins) {
					event.handleEvent(plugin) ;
				}
			} catch (InterruptedException ignore) {
				ignore.printStackTrace();
			}
			
		}
	}
}
