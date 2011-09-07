package net.ion.websocket.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.plugin.IMessagePacket;
import net.ion.websocket.plugin.MessagePacket;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;

public class SyncMockClient implements WebSocketCallback {
	private WebSocketClient asyncClient;
	private MessagePacket lastPacket = MessagePacket.EMPTY;
	private volatile Status status = Status.INIT;
	private String userName;
	private List<IMessagePacket> messages = ListUtil.newList();

	private EventListenerMap emap = new EventListenerMap();

	private SyncMockClient(String userName) {
		this.userName = userName;
	}

	public static SyncMockClient newTest() {
		return newTest("anonymous");
	}

	public static SyncMockClient newTest(String userName) {
		return new SyncMockClient(userName);
	}

	public synchronized void connect(URI uri) {
		URI newURI = makeNewURI(uri);

		if (getStatus() == Status.CONNECTING)
			return;

		asyncClient = WebSocketClientFactory.newClient(newURI, this);
		asyncClient.connect();
		setStatus(Status.CONNECTING);
		waitOnConnected(3000); // Guarded Suspension
	}

	public synchronized void sendMessage(final IMessagePacket packet) throws WebSocketException {
		messages.add(packet);

		ChannelFuture future = asyncClient.send(new DefaultWebSocketFrame(packet.toRoot().getFullString())).awaitUninterruptibly();
		final SyncMockClient client = this;
		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture arg0) throws Exception {
				messages.remove(packet);
				if (messages.isEmpty())
					client.notifyAll();
			}
		});
	}

	public synchronized void disconnect() {
		while (messages.size() > 0) { // Guarded Suspension
			try {
				wait(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (asyncClient != null)
			asyncClient.disconnect().awaitUninterruptibly(3, TimeUnit.SECONDS);
	}

	private URI makeNewURI(URI uri) {
		try {
			String userInfo = userName + ":" + RandomUtil.nextRandomString(10);
			return new URI(uri.getScheme(), userInfo, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public void await() throws InterruptedException {
		Thread.sleep(200);
	}

	public void await(int mili) throws InterruptedException {
		Thread.sleep(mili);
	}

	public boolean isConnected() {
		return status.isConnected();
	}

	private void setStatus(Status status) {
		this.status = status;
	}

	public final synchronized void onConnect(WebSocketClient client) {
		setStatus(Status.OPEN);
		notifyAll();
	}

	public final void onDisconnect(WebSocketClient client) {
		emap.onClose();
		setStatus(Status.CLOSED);
	}

	public void onError(Throwable t) {
		t.printStackTrace();
	}

	public void onMessage(WebSocketClient client, WebSocketFrame frame) {
		lastPacket = MessagePacket.load(frame.getTextData());
		emap.onMessage(lastPacket);
		synchronized (this) {
			notifyAll();
		}
	}

	public MessagePacket getLastPacket() {
		return lastPacket;
	}

	private void waitOnConnected(int timeout) {
		try {
			wait(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Status getStatus() {
		return status;
	}

	public String getUserName() {
		return userName;
	}

	public EventListenerMap getMessageListener() {
		return emap;
	}

	public synchronized void awaitOnMessage() {
		try {
			wait(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void ping() throws WebSocketException {
		sendMessage(MessagePacket.PING);
	}

}
