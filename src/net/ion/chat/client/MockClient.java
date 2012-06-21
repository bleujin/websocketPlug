package net.ion.chat.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import net.ion.chat.util.IMessagePacket;
import net.ion.chat.util.NormalMessagePacket;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;
import net.ion.nradon.client.websocket.IResponseMessageHandler;
import net.ion.nradon.client.websocket.WebSocketClient;
import net.ion.nradon.netty.codec.http.websocketx.BinaryWebSocketFrame;
import net.ion.nradon.netty.codec.http.websocketx.TextWebSocketFrame;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class MockClient {
	private WebSocketClient wsClient ;
	private String userName;
	private List<IMessagePacket> messages = ListUtil.newList();

	private EventListenerMap emap ;
	private MockClient(String userName) {
		this.userName = userName;
		this.emap = new EventListenerMap() ;
		this.wsClient = WebSocketClient.create(ChatMessageHandler.create(this, emap)) ;
	}

	public static MockClient newTest() {
		return newTest("anonymous");
	}

	public static MockClient newTest(String userName) {
		return new MockClient(userName);
	}

	public void connect(URI uri) throws Exception {
		URI newURI = makeNewURI(uri);

		wsClient.connect(newURI);
	}

	public void sendMessage(final IMessagePacket packet) throws InterruptedException  {
		messages.add(packet);
		
		wsClient.sendMessage(packet.getFullString()).addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				messages.remove(packet) ;
			}
		}) ;
	}

	public void awaitOnSendComplete() throws InterruptedException {
		while(messages.size() > 0){
			Thread.sleep(50) ;
		}
	}
	
	public void disconnect() {
		try {
			wsClient.disconnect() ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private URI makeNewURI(URI uri) {
		try {
			String userInfo = userName + ":" + RandomUtil.nextRandomString(10);
			return new URI(uri.getScheme(), userInfo, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e.getMessage()) ;
		}
	}

	public void await() throws InterruptedException {
		Thread.sleep(200);
	}

	public void await(int mili) throws InterruptedException {
		Thread.sleep(mili);
	}

	public void onError(Throwable t) {
		t.printStackTrace();
	}

	public void onMessage(IMessagePacket recentMessage) {
		synchronized(this){
			notifyAll() ;
		}
	}

	public IMessagePacket getLastPacket() {
		return emap.getLastPacket();
	}

	public String getUserName() {
		return userName;
	}

	public EventListenerMap getMessageListener() {
		return emap ;
	}


	public void ping() throws InterruptedException {
		sendMessage(NormalMessagePacket.PING) ;
	}


	public synchronized void awaitOnMessage() {
		try {
			wait(3000) ;
		} catch (InterruptedException ignore) {
		}
	}
	
}

class ChatMessageHandler implements IResponseMessageHandler {

	private MockClient toonClient ;
	private EventListenerMap eventListenerMap ;
	private boolean connected ;
	private ChatMessageHandler(MockClient toonClient, EventListenerMap eventListenerMap){
		this.toonClient = toonClient ;
		this.eventListenerMap = eventListenerMap ;
	}
	
	public final static ChatMessageHandler create(MockClient toonClient, EventListenerMap eventListenerMap){
		return new ChatMessageHandler(toonClient, eventListenerMap) ;
	}
	
	public boolean isConnected() {
		return connected;
	}

	public void onClosed() {
		eventListenerMap.onClose() ;
	}

	public void onDisconnected() {
		connected = false ;
	}

	public void onBinMessage(BinaryWebSocketFrame bframe) {
		
	}

	public void onMessage(TextWebSocketFrame textFrame) {
		NormalMessagePacket messagePacket = NormalMessagePacket.load(textFrame.getText());
		eventListenerMap.onMessage(messagePacket) ;
		toonClient.onMessage(messagePacket) ;
	}

	public void onOpen() {
		connected = true ;
	}

	public void onPong() {
		
	}
	
}

