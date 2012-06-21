package net.ion.chat.impl.listener;

import net.ion.chat.api.ChatListener;


public class WorkerThread extends Thread{

	private ChannelListener channel ;
	private ChatListener[] listeners ;
	public WorkerThread(ChatListener[] listeners, ChannelListener channel) {
		this.listeners = (listeners == null || listeners.length == 0) ? new ChatListener[]{ChatListener.NONE} :  listeners ;
		this.channel = channel ;

	}
	
	public void run(){
		while(true){
			try {
				ServerEvent event = channel.takeEvent() ;
				for (ChatListener listener : listeners) {
					event.handleEvent(listener) ;
				}
			} catch (InterruptedException ignore) {
				ignore.printStackTrace();
			}
			
		}
	}
}
