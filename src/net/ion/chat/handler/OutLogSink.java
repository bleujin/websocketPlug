package net.ion.chat.handler;

import static net.ion.nradon.helpers.Hex.toHex;

import java.net.SocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.ion.chat.api.ChatConstants;
import net.ion.chat.config.EngineConfiguration;
import net.ion.chat.util.IMessagePacket;
import net.ion.chat.util.ChatMessage;
import net.ion.chat.util.URIParser;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.EventSourceConnection;
import net.ion.nradon.EventSourceHandler;
import net.ion.nradon.HttpRequest;
import net.ion.nradon.WebSocketConnection;
import net.ion.nradon.handler.logging.LogSink;

public class OutLogSink implements LogSink, Snooper, EventSourceHandler {

	private final String[] dataValuesToLog;
	private EngineConfiguration econfig;

	private boolean trouble = false;
	private List<EventSourceConnection> econns = ListUtil.newList();
	private final ExecutorService webThread = Executors.newSingleThreadExecutor();

	public OutLogSink(EngineConfiguration econfig, String... dataValuesToLog) {
		this.econfig = econfig;
		this.dataValuesToLog = dataValuesToLog;
	}

	public void httpStart(HttpRequest request) {
		// custom(request, "HTTP-START", null);
	}

	public void httpEnd(HttpRequest request) {
		// custom(request, "HTTP-END", null); // TODO: Time request
	}

	public void webSocketConnectionOpen(WebSocketConnection connection) {
		setAttributeAtConnection(connection);
		custom(connection.httpRequest(), "WEB-SOCKET-" + connection.version() + "-OPEN", null);
	}

	private void setAttributeAtConnection(WebSocketConnection conn) {
		HttpRequest req = conn.httpRequest();
		Map<String, String> patternValues = URIParser.parse(req.uri(), econfig.getURIPath());

		for (Entry<String, String> pvalue : patternValues.entrySet()) {
			conn.data(pvalue.getKey(), pvalue.getValue());
		}
		for (String key : req.queryParamKeys()) {
			conn.data(key, req.queryParam(key));
		}

		conn.data(ChatConstants.VAR_SESSIONID, conn.httpRequest().remoteAddress().toString());
	}

	public void webSocketConnectionClose(WebSocketConnection conn) {
		custom(conn.httpRequest(), "WEB-SOCKET-" + conn.version() + "-CLOSE", null);
	}

	public void webSocketInboundData(WebSocketConnection conn, String data) {
		if (ignoreMessage(ChatMessage.create(data), (String) conn.data(ChatConstants.VAR_USERID)))
			return;
		custom(conn.httpRequest(), "WEB-SOCKET-" + conn.version() + "-IN-STRING", data);
	}

	public void webSocketOutboundData(WebSocketConnection conn, String data) {
		if (ignoreMessage(ChatMessage.create(data), (String) conn.data(ChatConstants.VAR_USERID)))
			return;
		custom(conn.httpRequest(), "WEB-SOCKET-" + conn.version() + "-OUT-STRING", data);
	}

	private boolean ignoreMessage(IMessagePacket msg, String sender) {
		if (msg.isPing()){
			return true;
		}
		String receiver = msg.getString("head/receiver");
		if (StringUtil.isBlank(receiver) || sender.equals(receiver))
			return true;

		return false;
	}

	public void webSocketInboundData(WebSocketConnection conn, byte[] data) {
		custom(conn.httpRequest(), "WEB-SOCKET-" + conn.version() + "-IN-HEX", toHex(data));
	}

	public void webSocketInboundPong(WebSocketConnection conn, String message) {
		// custom(conn.httpRequest(), "WEB-SOCKET-" + conn.version() + "-IN-PONG", message);
	}

	public void webSocketOutboundPing(WebSocketConnection conn, String message) {
		// custom(conn.httpRequest(), "WEB-SOCKET-" + conn.version() + "-PING", message);
	}

	public void webSocketOutboundData(WebSocketConnection conn, byte[] data) {
		custom(conn.httpRequest(), "WEB-SOCKET-" + conn.version() + "-OUT-HEX", toHex(data));
	}

	public void error(HttpRequest request, Throwable error) {
		custom(request, "ERROR-OPEN", error.toString());
	}

	public void custom(HttpRequest request, String action, String data) {
		if (trouble) {
			return;
		}
		formatLogEntry(request, action, data);
	}

	public void eventSourceConnectionOpen(EventSourceConnection conn) {
		// custom(conn.httpRequest(), "EVENT-SOURCE-OPEN", null);
	}

	public void eventSourceConnectionClose(EventSourceConnection conn) {
		// custom(conn.httpRequest(), "EVENT-SOURCE-CLOSE", null);
	}

	public void eventSourceOutboundData(EventSourceConnection conn, String data) {
		// 
	}

	private void formatLogEntry(HttpRequest request, String action, String data) {
		
		Debug.debug(request, action, data) ;
	}

	private long cumulativeTimeOfRequest(HttpRequest request) {
		return System.currentTimeMillis() - request.timestamp();
	}

	protected String address(SocketAddress address) {
		return address.toString();
	}

	public Tracer getTracer(String userId) {
		return Tracer.NONE ;
	}

	private void setAttributeAtEventConnection(EventSourceConnection conn) {
		HttpRequest req = conn.httpRequest();
		Map<String, String> patternValues = URIParser.parse(req.uri(), "/{$serviceName}/{$userId}");

		for (Entry<String, String> pvalue : patternValues.entrySet()) {
			conn.data(pvalue.getKey(), pvalue.getValue());
		}
		for (String key : req.queryParamKeys()) {
			conn.data(key, req.queryParam(key));
		}

		conn.data(ChatConstants.VAR_SESSIONID, conn.httpRequest().remoteAddress().toString());
	}

	public void onOpen(EventSourceConnection econn) throws Exception {
		setAttributeAtEventConnection(econn);
		econns.add(econn);
	}

	public void onClose(EventSourceConnection econn) throws Exception {
		econns.remove(econn);
	}

	public void webSocketInboundPing(WebSocketConnection connection, byte[] msg) {
	}

	public void webSocketInboundPong(WebSocketConnection connection, byte[] msg) {
	}

	public void webSocketOutboundPing(WebSocketConnection connection, byte[] msg) {
	}

	public void webSocketOutboundPong(WebSocketConnection connection, byte[] msg) {
	}
	
	
}

