package net.ion.websocket.common.plugin;

import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.PlugInResponse;

public class ProcessEvent {

	private Type type ;
	private PlugInResponse response;
	private WebSocketConnector conn;
	private WebSocketPacket packet;
	private CloseReason creason ;
	private WebSocketEngine engine ;
	
	private enum Type {
		PROCESS{
			public void handle(ProcessEvent pevent, WebSocketPlugIn plugin){
				plugin.processPacket(pevent.response, pevent.conn, pevent.packet) ;
			}
		}, CONNECTION_START{
			public void handle(ProcessEvent pevent, WebSocketPlugIn plugin){
				plugin.connectorStarted(pevent.conn) ;
			}
		}, CONNECTION_END{
			public void handle(ProcessEvent pevent, WebSocketPlugIn plugin){
				plugin.connectorStopped(pevent.conn, pevent.creason) ;
			}
		}, ENGINE_START{
			public void handle(ProcessEvent pevent, WebSocketPlugIn plugin){
				plugin.engineStarted(pevent.engine) ;
			}
		}, ENGINE_END{
			public void handle(ProcessEvent pevent, WebSocketPlugIn plugin){
				plugin.engineStopped(pevent.engine) ;
			}
		} ;
		
		public abstract void handle(ProcessEvent pevent, WebSocketPlugIn plugin) ;
	}
	
	private ProcessEvent(Type type) {
		this.type = type ;
	}

	public static ProcessEvent process(PlugInResponse response, WebSocketConnector conn, WebSocketPacket packet) {
		ProcessEvent result = new ProcessEvent(Type.PROCESS);
		result.response = response ;
		result.conn = conn ;
		result.packet = packet ;
		return result;
	}

	public static ProcessEvent connStart(WebSocketConnector conn) {
		ProcessEvent result = new ProcessEvent(Type.CONNECTION_START);
		result.conn = conn ;
		return result;
	}
	public static ProcessEvent connEnd(WebSocketConnector conn, CloseReason creason) {
		ProcessEvent result = new ProcessEvent(Type.CONNECTION_END);
		result.conn = conn;
		result.creason = creason;
		return result; 
	}
	
	public static ProcessEvent engineStart(WebSocketEngine engine){
		ProcessEvent result = new ProcessEvent(Type.ENGINE_START);
		result.engine = engine;
		return result; 
	}
	public static ProcessEvent engineEnd(WebSocketEngine engine){
		ProcessEvent result = new ProcessEvent(Type.ENGINE_END);
		result.engine = engine;
		return result; 
	}


	
	public void handleEvent(WebSocketPlugIn plugin) {
		type.handle(this, plugin) ;
	}

}
