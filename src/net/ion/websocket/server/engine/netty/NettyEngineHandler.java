//	---------------------------------------------------------------------------
//	jWebSocket - Copyright (c) 2010 jwebsocket.org
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package net.ion.websocket.server.engine.netty;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastMap;
import net.ion.framework.util.StringUtil;
import net.ion.websocket.common.api.EngineConfiguration;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.connector.BaseConnector;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.RawPacket;
import net.ion.websocket.common.kit.RequestHeader;
import net.ion.websocket.common.kit.WebSocketRuntimeException;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.server.URIParser;
import net.ion.websocket.server.engine.netty.connector.NettyConnector;
import net.ion.websocket.server.engine.netty.http.HttpHeaders;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ChildChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameDecoder;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.util.CharsetUtil;

/**
 * Handler class for the <tt>NettyEngine</tt> that recieves the events based on event types and notifies the client connectors. This handler also handles the initial handshaking for WebSocket connection with a appropriate hand shake response. This handler is created for each new connection channel.
 * <p>
 * Once the handshaking is successful after sending the handshake {@code HttpResponse} it replaces the {@code HttpRequestDecoder} and {@code HttpResponseEncoder} from the channel pipeline with {@code WebSocketFrameDecoder} as WebSocket frame data decoder and {@code WebSocketFrameEncoder} as WebSocket frame data encoder. Also it starts the <tt>NettyConnector</tt>.
 * </p>
 * 
 * @author <a href="http://www.purans.net/">Puran Singh</a>
 * @version $Id: NettyEngineHandler.java,v 1.11 2011/08/02 02:43:04 bleujin Exp $
 */
public class NettyEngineHandler extends SimpleChannelUpstreamHandler {

	private static Logger log = Logging.getLogger(NettyEngineHandler.class);

	private NettyEngine engine = null;

	private WebSocketConnector connector = null;

	private ChannelHandlerContext context = null;

	private static final ChannelGroup channels = new DefaultChannelGroup();

	private static final String CONTENT_LENGTH = "Content-Length";

	private static final String ARGS = "args";
	private static final String ORIGIN = "origin";
	private static final String LOCATION = "location";
	private static final String PATH = "path";
	private static final String SEARCH_STRING = "searchString";
	private static final String HOST = "host";

	private static final String SECURE_PROTOCOL = "ws:";
	private static final String NORMAL_PROTOCOL = "ws:";

	public NettyEngineHandler(NettyEngine engine) {
		this.engine = engine;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelBound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		this.setContext(ctx);
		super.channelBound(ctx, e);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		this.setContext(ctx);
		super.channelClosed(ctx, e);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		this.setContext(ctx);
		super.channelConnected(ctx, e);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		// remove the channel
		channels.remove(e.getChannel());

		this.setContext(ctx);
		super.channelDisconnected(ctx, e);

		engine.connectorStopped(connector, CloseReason.CLIENT);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelInterestChanged(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		this.setContext(ctx);
		super.channelInterestChanged(ctx, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		this.setContext(ctx);
		super.channelOpen(ctx, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelUnbound(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		this.setContext(ctx);
		super.channelUnbound(ctx, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void childChannelClosed(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
		this.setContext(ctx);
		super.childChannelClosed(ctx, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void childChannelOpen(ChannelHandlerContext ctx, ChildChannelStateEvent e) throws Exception {
		this.setContext(ctx);
		super.childChannelOpen(ctx, e);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		this.setContext(ctx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent arg1) throws Exception {
		this.setContext(ctx);
		super.handleUpstream(ctx, arg1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		this.setContext(ctx);

		Object msg = e.getMessage();
		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	/**
	 * private method that sends the handshake response for WebSocket connection
	 * 
	 * @param ctx
	 *            the channel context
	 * @param req
	 *            http request object
	 * @param res
	 *            http response object
	 */
	private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
		// Generate an error page if response status code is not OK (200).
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * Check if the request header has Keep-Alive
	 * 
	 * @param req
	 *            the http request object
	 * @return {@code true} if keep-alive is set in the header {@code false} otherwise
	 */
	private boolean isKeepAlive(HttpRequest req) {
		String keepAlive = req.getHeader(HttpHeaders.Values.KEEP_ALIVE);
		if (keepAlive != null && keepAlive.length() > 0) {
			return true;
		} else {
			// TODO: Keep-Alive value is like 'timeout=15, max=500'
			return false;
		}
	}

	/**
	 * Set the content length in the response
	 * 
	 * @param res
	 *            the http response object
	 * @param readableBytes
	 *            the length of the bytes
	 */
	private void setContentLength(HttpResponse res, int readableBytes) {
		res.setHeader(CONTENT_LENGTH, readableBytes);
	}

	/**
	 * private method that handles the web socket frame data, this method is used only after the WebSocket connection is established.
	 * 
	 * @param ctx
	 *            the channel handler context
	 * @param msg
	 *            the web socket frame data
	 */
	private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame msg) throws WebSocketRuntimeException {
		try {
			String textData = "";
			if (msg.isBinary()) { // TODO: handle binary data

			} else if (msg.isText()) {
				textData = msg.getTextData();
			} else {
				throw new WebSocketRuntimeException("Frame Doesn't contain any type of data");
			}
			engine.processPacket(connector, new RawPacket(textData));
		} catch (UnsupportedEncodingException e) {
			throw new WebSocketRuntimeException(e.getMessage());
		}
	}

	/**
	 * Handles the initial HTTP request for handshaking if the http request contains Upgrade header value as WebSocket then this method sends the handshake response and also fires the events on client connector.
	 * 
	 * @param ctx
	 *            the channel handler context
	 * @param req
	 *            the request message
	 */
	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) {
		// Allow only GET methods.
		if (req.getMethod() != HttpMethod.GET) {
			sendHttpResponse(ctx, req, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
			return;
		}
		// Serve the WebSocket handshake request.
		if (HttpHeaders.Values.UPGRADE.equalsIgnoreCase(req.getHeader(HttpHeaders.Names.CONNECTION)) && HttpHeaders.Values.WEBSOCKET.equalsIgnoreCase(req.getHeader(HttpHeaders.Names.UPGRADE))) {
			// Create the WebSocket handshake response.
			HttpResponse response = null;
			try {
				response = constructHandShakeResponse(req, ctx);
			} catch (NoSuchAlgorithmException e) {
				// better to close the channel
				log.debug("Channel is disconnected");
				ctx.getChannel().close();
			}

			// write the response
			ctx.getChannel().write(response);

			channels.add(ctx.getChannel());

			// since handshaking is done, replace the encoder/decoder with web socket data frame encoder/decoder
			ChannelPipeline p = ctx.getChannel().getPipeline();
			p.remove("aggregator");
			p.replace("decoder", "jwsdecoder", new WebSocketFrameDecoder(getMaxFrameSize(engine.getConfiguration())));
			p.replace("encoder", "jwsencoder", new WebSocketFrameEncoder());

			// if the WebSocket connection URI is wss then start SSL TLS
			// handshaking
			if (req.getUri().startsWith(SECURE_PROTOCOL)) {
				// Get the SslHandler in the current pipeline.
				final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
				// Get notified when SSL handshake is done.
				ChannelFuture handshakeFuture = sslHandler.handshake();
				handshakeFuture.addListener(new SecureWebSocketConnectionListener(sslHandler));
			}
			// initialize the connector
			connector = initializeConnector(ctx, req);
			return;
		}

		// Send an error page otherwise.
		sendHttpResponse(ctx, req, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
	}

	private int getMaxFrameSize(EngineConfiguration config) {
		if (config == null || config.getMaxFramesize() <= 1) {
			return CommonConstants.DEFAULT_MAX_FRAME_SIZE;
		} else {
			return config.getMaxFramesize();
		}
	}

	/**
	 * Constructs the <tt>HttpResponse</tt> object for the handshake response
	 * 
	 * @param req
	 *            the http request object
	 * @param ctx
	 *            the channel handler context
	 * @return the http handshake response
	 * @throws NoSuchAlgorithmException
	 */
	private HttpResponse constructHandShakeResponse(HttpRequest req, ChannelHandlerContext ctx) throws NoSuchAlgorithmException {
		HttpResponse res = new DefaultHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, "Web Socket Protocol Handshake"));
		// req.getHeader(Names.CONTENT_LOCATION)
		res.addHeader(HttpHeaders.Names.UPGRADE, HttpHeaders.Values.WEBSOCKET);
		res.addHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.UPGRADE);

		
		// Fill in the headers and contents depending on handshake method.
		if (req.containsHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY1) && req.containsHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY2)) {
			// New handshake method with a challenge:
			res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_ORIGIN, req.getHeader(HttpHeaders.Names.ORIGIN));
			res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_LOCATION, getWebSocketLocation(SECURE_PROTOCOL, req));
			String protocol = req.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL);
			if (protocol != null) {
				res.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL, protocol);
			}

			// Calculate the answer of the challenge.
			String key1 = req.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY1);
			String key2 = req.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY2);
			int a = (int) (Long.parseLong(key1.replaceAll("[^0-9]", "")) / key1.replaceAll("[^ ]", "").length());
			int b = (int) (Long.parseLong(key2.replaceAll("[^0-9]", "")) / key2.replaceAll("[^ ]", "").length());
			long c = req.getContent().readLong();
			ChannelBuffer input = ChannelBuffers.buffer(16);
			input.writeInt(a);
			input.writeInt(b);
			input.writeLong(c);
			ChannelBuffer output = ChannelBuffers.wrappedBuffer(MessageDigest.getInstance("MD5").digest(input.array()));
			res.setContent(output);
		} else {
			// Old handshake method with no challenge:
			res.addHeader(HttpHeaders.Names.WEBSOCKET_ORIGIN, req.getHeader(HttpHeaders.Names.ORIGIN));
			res.addHeader(HttpHeaders.Names.WEBSOCKET_LOCATION, getWebSocketLocation(NORMAL_PROTOCOL, req));
			String protocol = req.getHeader(HttpHeaders.Names.WEBSOCKET_PROTOCOL);
			if (protocol != null) {
				res.addHeader(HttpHeaders.Names.WEBSOCKET_PROTOCOL, protocol);
			}
		}
		return res;

	}

	/**
	 * Initialize the {@code NettyConnector} after initial handshaking is successfull.
	 * 
	 * @param ctx
	 *            the channel handler context
	 * @param req
	 *            the http request object
	 */
	private WebSocketConnector initializeConnector(ChannelHandlerContext ctx, HttpRequest req) {

		RequestHeader header = getRequestHeader(req);
		int sessionTimeout = header.getTimeout(CommonConstants.DEFAULT_TIMEOUT);
		if (sessionTimeout > 0) {
			ctx.getChannel().getConfig().setConnectTimeoutMillis(sessionTimeout);
		}
		// create connector
		WebSocketConnector conn = new NettyConnector(engine, this);
		conn.setHeader(header);

		conn.startConnector();

		setUserInfo(conn, req) ;
		setURIAttribute(conn, req) ;
		// allow descendant classes to handle connector started event
		engine.connectorStarted(conn);
		
		return conn;

	}

	private void setURIAttribute(WebSocketConnector conn, HttpRequest req) {
		Map<String, String> patternValues = URIParser.parse(req.getUri(), engine.getServer().getConfiguration().getURIPath()) ;
		
		for (Entry<String, String> pvalue : patternValues.entrySet()) {
			conn.setString(pvalue.getKey() , pvalue.getValue()) ;
		}
		conn.setString(BaseConnector.VAR_REQUEST_URI, req.getUri()) ;
	}

	private void setUserInfo(WebSocketConnector conn, HttpRequest req) {
		String userInfo = req.getHeader(CommonConstants.XUSER_INFO);
		if (StringUtil.isBlank(userInfo)) return ;
		
		String[] connectInfo = StringUtil.split(userInfo, "/:;") ;
		
		if (connectInfo.length == 1) {
			conn.setUsername(connectInfo[0]) ;
		} else if (connectInfo.length == 2){
			conn.setUsername(connectInfo[0]) ;
			conn.getSession().setSessionId(connectInfo[1]) ;
		}
	}

	/**
	 * Construct the request header to save it in the connector
	 * 
	 * @param req
	 *            the http request header
	 * @return the request header
	 */
	private RequestHeader getRequestHeader(HttpRequest req) {
		RequestHeader header = new RequestHeader();
		FastMap<String, String> argMap = new FastMap<String, String>();
		String searchString = "";
		String path = req.getUri();

		// isolate search string
		int pos = path.indexOf(CommonConstants.PATHARG_SEPARATOR);
		if (pos >= 0) {
			searchString = path.substring(pos + 1);
			if (searchString.length() > 0) {
				String[] args = searchString.split(CommonConstants.ARGARG_SEPARATOR);
				for (int i = 0; i < args.length; i++) {
					String[] keyValuePair = args[i].split(CommonConstants.KEYVAL_SEPARATOR, 2);
					if (keyValuePair.length == 2) {
						argMap.put(keyValuePair[0], keyValuePair[1]);
					}
				}
			}
		}
		// set default sub protocol if none passed
		if (argMap.get("prot") == null) {
			argMap.put("prot", CommonConstants.SUB_PROT_DEFAULT);
		}
		header.put(ARGS, argMap);
		header.put(ORIGIN, req.getHeader(HttpHeaders.Names.ORIGIN));
		header.put(LOCATION, getWebSocketLocation(NORMAL_PROTOCOL, req));
		header.put(PATH, req.getUri());

		header.put(SEARCH_STRING, searchString);
		header.put(HOST, req.getHeader(HttpHeaders.Names.HOST));
		return header;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
		super.writeComplete(ctx, e);
	}

	/**
	 * Returns the web socket location URL
	 * 
	 * @param req
	 *            the http request object
	 * @return the location url string
	 */
	private String getWebSocketLocation(String protocol, HttpRequest req) {
		// TODO: fix this URL for wss: (secure)
		String location = protocol + "//" + req.getHeader(HttpHeaders.Names.HOST) + req.getUri();
		return location;
	}

	/**
	 * Returns the channel context
	 * 
	 * @return the channel context
	 */
	public ChannelHandlerContext getChannelHandlerContext() {
		return getContext();
	}

	public void setContext(ChannelHandlerContext context) {
		this.context = context;
	}

	public ChannelHandlerContext getContext() {
		return context;
	}

	/**
	 * Listener class for SSL TLS handshake completion.
	 */
	private static final class SecureWebSocketConnectionListener implements ChannelFutureListener {

		private final SslHandler sslHandler;

		SecureWebSocketConnectionListener(SslHandler sslHandler) {
			this.sslHandler = sslHandler;
		}

		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				// that means SSL handshaking is done.
			} else {
				future.getChannel().close();
			}
		}
	}
}