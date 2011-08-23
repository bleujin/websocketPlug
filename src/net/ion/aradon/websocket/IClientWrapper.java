package net.ion.aradon.websocket;

import java.net.URI;
import java.util.Map;

import net.ion.websocket.client.SyncMockClient;

public interface IClientWrapper {

	public boolean runClient(String id, String ip, String port) ;

	public boolean isEmpty();

	public URI getURI(String topicId);

	public Map<URI, SyncMockClient> getServerList() ;
	
	public boolean disconnectClient(String ip, String port);
}
