package net.ion.chat.util;

public class PlainMessagePacket implements IMessagePacket{


	private String msg ;
	public PlainMessagePacket(String msg){
		this.msg = msg ;
	}
	
	public final static PlainMessagePacket create(String msg){
		return new PlainMessagePacket(msg) ;
	}
	

	public String getFullString() {
		return msg;
	}

	public String getString(String path) {
		return msg;
	}

	public int getInt(String path, int dftvalue){
		return dftvalue ;
	}
	
	public boolean has(String path) {
		return false;
	}

	public IMessagePacket toRoot() {
		return this;
	}
	
	public boolean isPing() {
		return "".equals(msg) ;
	}


}
