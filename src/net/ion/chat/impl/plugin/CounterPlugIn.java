package net.ion.chat.impl.plugin;

import java.util.concurrent.atomic.AtomicInteger;

import net.ion.chat.api.BasePlugIn;
import net.ion.chat.api.CloseReason;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserConnector;
import net.ion.chat.util.IMessagePacket;
import net.ion.framework.util.Debug;

public class CounterPlugIn extends BasePlugIn {
	
	private AtomicInteger ccounter = new AtomicInteger();
	private AtomicInteger pcounter = new AtomicInteger();

	public int getProcessCounter() {
		return pcounter.get();
	}
	
	public int getConnectorCounter(){
		return ccounter.get() ;
	}

	public void processPacket(PlugInResponse response, IUserConnector connector, IMessagePacket packet) {
		pcounter.incrementAndGet() ;
	}

	public void connectorStarted(IUserConnector conn) {
		ccounter.incrementAndGet() ;
	}

	public void connectorStopped(IUserConnector conn, CloseReason creason) {
		ccounter.decrementAndGet() ;
	}

}
