package net.ion.chat.api;

public class CloseReason {

	public final static CloseReason CLIENT = new CloseReason("CLIENT") ;
	public final static CloseReason SHUTDOWN = new CloseReason("SHUTDOWN") ;
	public final static CloseReason DOPPELGANGER = new CloseReason("DOPPELGANGER") ;
	public final static CloseReason TIMEOUT = new CloseReason("TIMEOUT") ;

	public static CloseReason define(String msg) {
		return new CloseReason(msg);
	}
	
	private String msg ;
	private CloseReason(String msg){
		this.msg = msg ;
	} 
	
	public String getMessage() {
		return this.msg ;
	}
	
}
