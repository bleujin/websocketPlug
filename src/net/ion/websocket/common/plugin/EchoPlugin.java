package net.ion.websocket.common.plugin;

import net.ion.framework.util.Debug;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.kit.PlugInResponse;

public class EchoPlugIn extends BasePlugIn {

	public final static EchoPlugIn SELF = new EchoPlugIn() ; 
	
	@Override public void connectorStarted(WebSocketConnector conn) {
		// conn.sendPacket(MessagePacket.create().put("greeting", "hi").toRoot().forSend()) ;
	}

	
	public void processPacket(PlugInResponse response, WebSocketConnector conn, WebSocketPacket packet) {
		conn.sendPacket(packet) ;
		Debug.line(conn, packet) ;
	}

}
