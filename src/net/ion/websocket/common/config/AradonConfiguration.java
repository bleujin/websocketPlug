package net.ion.websocket.common.config;

import net.ion.radon.core.config.XMLConfig;

public class AradonConfiguration {

	private String configPath ;
	private int portNo ;
	
	private AradonConfiguration(String configPath, int portNum) {
		this.configPath = configPath ;
		this.portNo = portNum ;
	}

	public final static AradonConfiguration create(XMLConfig config){
		return new AradonConfiguration(config.getString("[@config]"), config.getInt("[@port]", 0)) ;
	}
	public final static AradonConfiguration test(){
		return new AradonConfiguration("resource/config/aradon-client-config.xml", 9090) ;
	}

	
	public String getConfigPath(){
		return configPath ;
	}
	
	public int getPortNo(){
		return portNo ;
	}
	
}
