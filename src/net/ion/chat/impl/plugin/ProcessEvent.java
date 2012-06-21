package net.ion.chat.impl.plugin;

import net.ion.chat.api.CloseReason;
import net.ion.chat.api.PlugInResponse;
import net.ion.chat.api.ChatPlugIn;
import net.ion.chat.server.IUserConnector;
import net.ion.chat.server.UserConnector;
import net.ion.chat.util.IMessagePacket;
import net.ion.radon.core.TreeContext;

public class ProcessEvent {

	private Type type ;
	private PlugInResponse response;
	private IUserConnector conn;
	private IMessagePacket packet;
	private CloseReason creason ;
	private TreeContext context ;
	
	private enum Type {
		PROCESS{
			public void handle(ProcessEvent pevent, ChatPlugIn plugin){
				plugin.processPacket(pevent.response, pevent.conn, pevent.packet) ;
			}
		}, CONNECTION_START{
			public void handle(ProcessEvent pevent, ChatPlugIn plugin){
				plugin.connectorStarted(pevent.conn) ;
			}
		}, CONNECTION_END{
			public void handle(ProcessEvent pevent, ChatPlugIn plugin){
				plugin.connectorStopped(pevent.conn, pevent.creason) ;
			}
		}, ENGINE_START{
			public void handle(ProcessEvent pevent, ChatPlugIn plugin){
				plugin.engineStarted(pevent.context) ;
			}
		}, ENGINE_END{
			public void handle(ProcessEvent pevent, ChatPlugIn plugin){
				plugin.engineStopped(pevent.context) ;
			}
		} ;
		
		public abstract void handle(ProcessEvent pevent, ChatPlugIn plugin) ;
	}
	
	private ProcessEvent(Type type) {
		this.type = type ;
	}

	public static ProcessEvent process(PlugInResponse response, IUserConnector conn, IMessagePacket packet) {
		ProcessEvent result = new ProcessEvent(Type.PROCESS);
		result.response = response ;
		result.conn = conn ;
		result.packet = packet ;
		return result;
	}

	public static ProcessEvent connStart(IUserConnector conn) {
		ProcessEvent result = new ProcessEvent(Type.CONNECTION_START);
		result.conn = conn ;
		return result;
	}
	public static ProcessEvent connEnd(IUserConnector conn, CloseReason creason) {
		ProcessEvent result = new ProcessEvent(Type.CONNECTION_END);
		result.conn = conn;
		result.creason = creason;
		return result; 
	}
	
	public static ProcessEvent engineStart(TreeContext context){
		ProcessEvent result = new ProcessEvent(Type.ENGINE_START);
		result.context = context;
		return result; 
	}
	public static ProcessEvent engineEnd(TreeContext context){
		ProcessEvent result = new ProcessEvent(Type.ENGINE_END);
		result.context = context;
		return result; 
	}


	
	public void handleEvent(ChatPlugIn plugin) {
		type.handle(this, plugin) ;
	}

}
