package net.ion.websocket.common.config;

import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.core.config.XMLConfig;
import net.ion.websocket.common.api.WebSocketPlugIn;
import net.ion.websocket.server.context.ConfigCreator;

import org.apache.commons.configuration.ConfigurationException;

public class PlugInInfo {

	private final String name ;
	private final WebSocketPlugIn plugin ;
	private final String description ;
	
	private PlugInInfo(String name, String description, WebSocketPlugIn plugin){
		this.name = name ;
		this.description = description ;
		this.plugin = plugin ;
	}
	
	public final static PlugInInfo create(XMLConfig config) throws ConfigurationException, InstanceCreationException{
		WebSocketPlugIn plugin = (WebSocketPlugIn)ConfigCreator.createConfiguredInstance(config.firstChild("configured-object"))  ;
		
		return new PlugInInfo(config.getAttributeValue("name"), config.getString("description"), plugin) ;
	}

	public String getName() {
		return name ;
	} 
	
	public String getDescription() {
		return description ;
	}
	
	public WebSocketPlugIn getPlugIn(){
		return plugin ;
	}
	
}
