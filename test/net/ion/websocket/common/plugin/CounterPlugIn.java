package net.ion.websocket.common.plugin;

import java.util.concurrent.atomic.AtomicInteger;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;

public class CounterPlugIn extends BasePlugIn {

	
	private AtomicInteger ccounter = new AtomicInteger();
	private AtomicInteger pcounter = new AtomicInteger();

	public int getProcessCounter() {
		return pcounter.get();
	}
	
	public int getConnectorCounter(){
		return ccounter.get() ;
	}

	@Override public void processPacket(PlugInResponse response, WebSocketConnector connector, WebSocketPacket packet) {
		pcounter.incrementAndGet() ;
	}

	public void connectorStarted(WebSocketConnector conn) {
		ccounter.incrementAndGet() ;
	}

	public void connectorStopped(WebSocketConnector conn, CloseReason creason) {
		ccounter.decrementAndGet() ;
	}

}
