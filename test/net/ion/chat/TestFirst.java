package net.ion.chat;

import net.ion.chat.handler.ChatEngine;
import net.ion.chat.server.ChatServer;
import net.ion.framework.util.InfinityThread;
import junit.framework.TestCase;

public class TestFirst extends TestCase{

	
	public void testRun() throws Exception {
		ChatEngine engine = ChatEngine.createWithServerConfig() ;
		
		ChatServer cs = ChatServer.create(engine) ;
		cs.start();
		
		new InfinityThread().startNJoin() ;
	}
}
