package net.ion.chat.config;

import net.ion.chat.api.ChatListener;
import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.core.config.ConfigCreator;
import net.ion.radon.core.config.XMLConfig;

import org.apache.commons.configuration.ConfigurationException;

public class ListenerInfo {
	private final String name ;
	private final ChatListener listener ;
	private final String description ;
	
	private ListenerInfo(String name, String description, ChatListener listener){
		this.name = name ;
		this.description = description ;
		this.listener = listener ;
	}
	
	public final static ListenerInfo create(XMLConfig config) throws ConfigurationException, InstanceCreationException{
		ChatListener plugin = (ChatListener) ConfigCreator.createConfiguredInstance(config.firstChild("configured-object"))  ;
		
		return new ListenerInfo(config.getAttributeValue("name"), config.getString("description"), plugin) ;
	}

	public String getName() {
		return name ;
	} 
	
	public String getDescription() {
		return description ;
	}
	
	public ChatListener getListener(){
		return listener ;
	}
	
}
