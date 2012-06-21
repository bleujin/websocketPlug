package net.ion.chat.impl.plugin;

import net.ion.chat.api.ChatPlugIn;


public class WorkerThread extends Thread{

	private ChannelPlugIn channel ;
	private ChatPlugIn[] plugins ;
	public WorkerThread(ChatPlugIn[] plugins, ChannelPlugIn channel) {
		this.plugins = plugins ;
		this.channel = channel ;

	}
	
	public void run(){
		while(true){
			try {
				ProcessEvent event = channel.takeEvent() ;
				for (ChatPlugIn plugin : plugins) {
					event.handleEvent(plugin) ;
				}
			} catch (InterruptedException ignore) {
				ignore.printStackTrace();
			}
			
		}
	}
}
