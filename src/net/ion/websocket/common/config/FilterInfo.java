package net.ion.websocket.common.config;

import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.core.config.XMLConfig;
import net.ion.websocket.common.api.WebSocketFilter;
import net.ion.websocket.server.context.ConfigCreator;

import org.apache.commons.configuration.ConfigurationException;

public class FilterInfo {

	private final String name ;
	private final WebSocketFilter filter ;
	private final String description ;
	
	private FilterInfo(String name, String description, WebSocketFilter filter){
		this.name = name ;
		this.description = description ;
		this.filter = filter ;
	}
	
	public final static FilterInfo create(XMLConfig config) throws ConfigurationException, InstanceCreationException{
		WebSocketFilter filter = (WebSocketFilter) ConfigCreator.createConfiguredInstance(config.firstChild("configured-object"))  ;
		
		return new FilterInfo(config.getAttributeValue("name"), config.getString("description"), filter) ;
	}

	public String getName() {
		return name ;
	} 
	
	public String getDescription() {
		return description ;
	}
	
	public WebSocketFilter getFilter(){
		return filter ;
	}
	
}
