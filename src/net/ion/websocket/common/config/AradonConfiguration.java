package net.ion.websocket.common.config;

import net.ion.framework.util.Debug;
import net.ion.framework.util.PathMaker;
import net.ion.radon.core.config.XMLConfig;

public class AradonConfiguration {

	private String configPath ;
	private int portNo ;
	
	private AradonConfiguration(String configPath, int portNum) {
		this.configPath = configPath ;
		this.portNo = portNum ;
		Debug.line(configPath) ;
	}

	public final static AradonConfiguration create(String baseDir, XMLConfig config){
		return new AradonConfiguration( PathMaker.getFilePath(baseDir, config.getString("[@config]")) , config.getInt("[@port]", 0)) ;
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
