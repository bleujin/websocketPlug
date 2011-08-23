package net.ion.websocket.common;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.common.kit.PlugInResponse;
import net.ion.websocket.common.kit.WebSocketRuntimeException;
import net.ion.websocket.plugin.IMessagePacket;

public interface PacketTypeHandler {

	public void handle(WebSocketPlugIn plugin, PlugInResponse response, WebSocketConnector connector, IMessagePacket packet) throws WebSocketRuntimeException ;
}
