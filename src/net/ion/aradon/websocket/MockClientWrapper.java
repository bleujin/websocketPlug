package net.ion.aradon.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.websocket.client.SyncMockClient;

public class MockClientWrapper implements IClientWrapper {

	private final Map<URI, SyncMockClient> clients = MapUtil.newMap();

	private boolean mockThrow;

	private MockClientWrapper(boolean mockThrow) {
		this.mockThrow = mockThrow;
	}

	public static MockClientWrapper mock() {
		return new MockClientWrapper(true);
	}

	public static MockClientWrapper mockThrow() {
		return new MockClientWrapper(false);
	}

	public boolean runClient(String id, String ip, String port) {
		try {
			final URI uri = makeURI(ip, port);

			if (clients.containsKey(uri))
				throw new IllegalStateException(uri + " duplicated.");
			SyncMockClient client = SyncMockClient.newTest(id);

			clients.put(uri, client);
			return mockThrow;
		} catch (URISyntaxException ignore) {
			ignore.printStackTrace();
			return false;
		}
	}

	private URI makeURI(String ip, String port) throws URISyntaxException {
		String address = "ws://" + ip + ":" + port + "/";
		final URI uri = new URI(address);
		return uri;
	}

	public boolean isEmpty() {
		return clients.size() < 1;
	}

	public Map<URI, SyncMockClient> getServerList() {
		return Collections.unmodifiableMap(clients);
	}

	public URI getURI(String topicId) {
		int index = topicId.hashCode() % clients.size();
		return new ArrayList<URI>(clients.keySet()).get(index);
	}

	public boolean disconnectClient(String ip, String port) {
		try {
			SyncMockClient client = clients.remove(makeURI(ip, port)) ;
			if (client != null) {
				client.disconnect() ;
				return true ;
			} return false ;
			
		} catch (URISyntaxException ignore) {
			ignore.printStackTrace();
			return false ;
		}
	}
}
