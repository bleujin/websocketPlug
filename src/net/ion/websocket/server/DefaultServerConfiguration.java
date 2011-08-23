package net.ion.websocket.server;

import net.ion.websocket.common.api.ServerConfiguration;

public 
class DefaultServerConfiguration implements ServerConfiguration {

	private String uriPath = "" ;
	
	public String getJar() {
		return null;
	}

	public String getId() {
		return "ts0";
	}

	public String getName() {
		return "Server";
	}

	public String getURIPath() {
		return uriPath;
	}
	
	public void testURIPath(String uriPath){
		this.uriPath = uriPath ;
	}
}
