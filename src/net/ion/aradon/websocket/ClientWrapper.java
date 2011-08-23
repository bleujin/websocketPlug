package net.ion.aradon.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.websocket.client.CloseListener;
import net.ion.websocket.client.MessageListener;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.plugin.MessagePacket;

public class ClientWrapper implements IClientWrapper {

	private final Map<URI, SyncMockClient> clients = MapUtil.newMap();

	public boolean runClient(String id, String ip, String port) {
		try {
			final URI uri = makeURI(ip, port);

			if (clients.containsKey(uri))
				throw new IllegalStateException(uri + " duplicated.");

			SyncMockClient client = SyncMockClient.newTest(id);
			client.getMessageListener().register(MessageListener.DEBUG_OUT);
			client.getMessageListener().register(new CloseListener() {
				public void onClose(CloseReason creason) {
					clients.remove(uri);
				}
			});

			String userId = "__aradon" ;
			// long authKey = SessionKeyMaker.makeSessionKey(userId, "127.0.0.1");
			// c.connect(new URI("ws://127.0.0.1:9000/" + userId + "/" + authKey ));
			
			client.connect(new URI(uri.toString() + "/" + userId+  "/" + Integer.MAX_VALUE));
			client.sendMessage(MessagePacket.PING);

			clients.put(uri, client);
			return true;
		} catch (URISyntaxException ignore) {
			ignore.printStackTrace();
			return false ;
		} catch (WebSocketException ignore) {
			ignore.printStackTrace();
			return false ;
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
