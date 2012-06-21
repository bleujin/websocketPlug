package net.ion.chat.impl.plugin;

import net.ion.chat.api.BasePlugIn;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserConnector;
import net.ion.chat.util.IMessagePacket;

public class EchoPlugIn extends BasePlugIn {

	@Override public void connectorStarted(IUserConnector conn) {
		// conn.send(JSONMessagePacket.create().put("greeting", "hi").toRoot()) ;
	}

	
	public void processPacket(PlugInResponse response, IUserConnector conn, IMessagePacket packet) {
		conn.send(packet) ;
	}

}
