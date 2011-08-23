package net.ion.websocket.common.config;

import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.core.config.XMLConfig;
import net.ion.websocket.common.api.WebSocketServerListener;
import net.ion.websocket.server.context.ConfigCreator;

import org.apache.commons.configuration.ConfigurationException;

public class ListenerInfo {
	private final String name ;
	private final WebSocketServerListener listener ;
	private final String description ;
	
	private ListenerInfo(String name, String description, WebSocketServerListener listener){
		this.name = name ;
		this.description = description ;
		this.listener = listener ;
	}
	
	public final static ListenerInfo create(XMLConfig config) throws ConfigurationException, InstanceCreationException{
		WebSocketServerListener plugin = (WebSocketServerListener) ConfigCreator.createConfiguredInstance(config.firstChild("configured-object"))  ;
		
		return new ListenerInfo(config.getAttributeValue("name"), config.getString("description"), plugin) ;
	}

	public String getName() {
		return name ;
	} 
	
	public String getDescription() {
		return description ;
	}
	
	public WebSocketServerListener getListener(){
		return listener ;
	}
	
}
