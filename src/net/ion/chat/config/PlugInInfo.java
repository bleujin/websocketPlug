package net.ion.chat.config;

import net.ion.chat.api.ChatPlugIn;
import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.core.config.ConfigCreator;
import net.ion.radon.core.config.XMLConfig;

import org.apache.commons.configuration.ConfigurationException;

public class PlugInInfo {

	private final String name ;
	private final ChatPlugIn plugin ;
	private final String description ;
	
	private PlugInInfo(String name, String description, ChatPlugIn plugin){
		this.name = name ;
		this.description = description ;
		this.plugin = plugin ;
	}
	
	public final static PlugInInfo create(XMLConfig config) throws ConfigurationException, InstanceCreationException{
		ChatPlugIn plugin = (ChatPlugIn)ConfigCreator.createConfiguredInstance(config.firstChild("configured-object"))  ;
		
		return new PlugInInfo(config.getAttributeValue("name"), config.getString("description"), plugin) ;
	}

	public String getName() {
		return name ;
	} 
	
	public String getDescription() {
		return description ;
	}
	
	public ChatPlugIn getPlugIn(){
		return plugin ;
	}
	
}
