package net.ion.chat.exception;

public class ChatRuntimeException extends RuntimeException{

	private static final long serialVersionUID = 8497236386802904374L;

	private ChatRuntimeException(Exception ex) {
		super(ex) ;
	}

	private ChatRuntimeException(Exception ex, String msg) {
		super(msg, ex) ;
	}

	public ChatRuntimeException(String msg) {
		super(msg) ;
	}

	public static ChatRuntimeException create(Exception ex){
		return new ChatRuntimeException(ex) ;
	}

	public static ChatRuntimeException create(Exception ex, String msg){
		return new ChatRuntimeException(ex, msg) ;
	}
	
	public static ChatRuntimeException create(String msg){
		return new ChatRuntimeException(msg) ;
	}
	
	

}
