package net.ion.chat.util;


public interface IMessagePacket {

	boolean has(String path) ;
	
	String getString(String path) ;
	
	int getInt(String path, int dftvalue) ;
	
	String getFullString();

	IMessagePacket toRoot();
	
	boolean isPing() ;
}
