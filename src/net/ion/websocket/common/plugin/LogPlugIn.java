package net.ion.websocket.common.plugin;

import net.ion.framework.util.Debug;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.kit.PlugInResponse;

public class LogPlugIn extends BasePlugIn  {


	@Override
	public void processPacket(PlugInResponse response, WebSocketConnector connector, WebSocketPacket packet) {
		Debug.debug(getClass().getCanonicalName(), connector.getId(), connector.getUsername(), packet.getUTF8()) ;
	}
}
