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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastMap;
import net.ion.framework.util.StringUtil;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.config.EngineConfiguration;
import net.ion.websocket.common.connector.BaseConnector;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.RawPacket;
import net.ion.websocket.common.kit.RequestHeader;
import net.ion.websocket.common.kit.WebSocketRuntimeException;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.common.server.BaseServer;
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

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

/**
 * Handler class for the <tt>NettyEngine</tt> that recieves the events based on event types and notifies the client connectors. This handler also handles the initial handshaking for WebSocket connection with a appropriate hand shake response. This handler is created for each new connection channel.
 * <p>
 * Once the handshaking is successful after sending the handshake {@code HttpResponse} it replaces the {@code HttpRequestDecoder} and {@code HttpResponseEncoder} from the channel pipeline with {@code WebSocketFrameDecoder} as WebSocket frame data decoder and {@code WebSocketFrameEncoder} as WebSocket frame data encoder. Also it starts the <tt>NettyConnector</tt>.
 * </p>
 * 
 * @author <a href="http://www.purans.net/">Puran Singh</a>
 * @version $Id: NettyEngineHandler.java,v 1.13 2011/12/15 06:30:19 bleujin Exp $
 */
public class NettyEngineHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = Logging.getLogger(NettyEngineHandler.class);
	private NettyEngine engine = null;
	private WebSocketConnector connector = null;
	private ChannelHandlerContext context = null;
	private static final ChannelGroup channels = new DefaultChannelGroup();
	private static final String CONTENT_LENGTH = "Content-Length";

	/*
	 * Removed by Alex because these constants now are maintained in RequestHeader private static final String ARGS = "args"; private static final String ORIGIN = "origin"; private static final String LOCATION = "location"; private static final String PATH = "path"; private static final String SEARCH_STRING = "searchString"; private static final String HOST = "host";
	 */

	public NettyEngineHandler(NettyEngine engine) {
		this.engine = engine;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelBound(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
		this.context = context;
		super.channelBound(context, event);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelClosed(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
		this.context = context;
		super.channelClosed(context, event);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelConnected(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
		this.context = context;
		// Get the SslHandler in the current pipeline.
		final SslHandler sslHandler = context.getPipeline().get(SslHandler.class);
		// Get notified when SSL handshake is done.

		// Added by Alex to prevent exceptions
		// TODO: Fix this exceptions on connect!
		// ADD-START
		if (sslHandler != null) {// ADD-END
			try {
				ChannelFuture future = sslHandler.handshake();
				future.addListener(new SecureWebSocketConnectionListener(sslHandler));
			} catch (Exception es) {
				es.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelDisconnected(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
		logger.debug("Channel is disconnected");
		// remove the channel
		channels.remove(event.getChannel());

		this.context = context;
		super.channelDisconnected(context, event);
		engine.connectorStopped(connector, CloseReason.CLIENT);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void channelInterestChanged(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
		this.context = context;
		super.channelInterestChanged(context, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
		this.context = context;
		super.channelOpen(context, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelUnbound(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
		this.context = context;
		super.channelUnbound(context, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void childChannelClosed(ChannelHandlerContext context, ChildChannelStateEvent event) throws Exception {
		this.context = context;
		super.childChannelClosed(context, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void childChannelOpen(ChannelHandlerContext context, ChildChannelStateEvent event) throws Exception {
		this.context = context;
		super.childChannelOpen(context, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) throws Exception {
		this.context = context;
		logger.debug("Channel is disconnected : " + event.getCause().getLocalizedMessage());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleUpstream(ChannelHandlerContext context, ChannelEvent event) throws Exception {
		this.context = context;
		super.handleUpstream(context, event);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
		this.context = context;
		logger.debug("message received in the engine handler");
		Object lMsg = event.getMessage();
		if (lMsg instanceof HttpRequest) {
			handleHttpRequest(context, (HttpRequest) lMsg);
		} else if (lMsg instanceof WebSocketFrame) {
			handleWebSocketFrame(context, (WebSocketFrame) lMsg);
		}
	}

	/**
	 * private method that sends the handshake response for WebSocket connection
	 * 
	 * @param context
	 *            the channel context
	 * @param request
	 *            http request object
	 * @param response
	 *            http response object
	 */
	private void sendHttpResponse(ChannelHandlerContext context, HttpRequest request, HttpResponse response) {
		// Generate an error page if response status code is not OK (200).
		if (response.getStatus().getCode() != 200) {
			response.setContent(ChannelBuffers.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(response, response.getContent().readableBytes());
		}
		// Send the response and close the connection if necessary.
		ChannelFuture future = context.getChannel().write(response);
		if (!isKeepAlive(request) || response.getStatus().getCode() != 200) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	/**
	 * Check if the request header has Keep-Alive
	 * 
	 * @param request
	 *            the http request object
	 * @return {@code true} if keep-alive is set in the header {@code false} otherwise
	 */
	private boolean isKeepAlive(HttpRequest request) {
		String keepAlive = request.getHeader(HttpHeaders.Values.KEEP_ALIVE);
		if (keepAlive != null && keepAlive.length() > 0) {
			return true;
		} else {
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
	private void setContentLength(HttpResponse response, int readableBytes) {
		response.setHeader(CONTENT_LENGTH, readableBytes);
	}

	/**
	 * private method that handles the web socket frame data, this method is used only after the WebSocket connection is established.
	 * 
	 * @param context
	 *            the channel handler context
	 * @param msgFrame
	 *            the web socket frame data
	 */
	private void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame msgFrame) throws WebSocketRuntimeException {
		String textData = "";
		if (msgFrame.isBinary()) {
			// TODO: handle binary data
		} else if (msgFrame.isText()) {
			textData = msgFrame.getTextData();
		} else {
			throw new WebSocketRuntimeException("Frame Doesn't contain any type of data");
		}
		engine.processPacket(connector, new RawPacket(textData));
	}

	/**
	 * Handles the initial HTTP request for handshaking if the http request contains Upgrade header value as WebSocket then this method sends the handshake response and also fires the events on client connector.
	 * 
	 * @param context
	 *            the channel handler context
	 * @param req
	 *            the request message
	 */
	private void handleHttpRequest(ChannelHandlerContext context, HttpRequest request) {
		// Allow only GET methods.
		if (request.getMethod() != HttpMethod.GET) {
			sendHttpResponse(context, request, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
			return;
		}
		// Serve the WebSocket handshake request.
		if (HttpHeaders.Values.UPGRADE.equalsIgnoreCase(request.getHeader(HttpHeaders.Names.CONNECTION)) && HttpHeaders.Values.WEBSOCKET.equalsIgnoreCase(request.getHeader(HttpHeaders.Names.UPGRADE))) {
			// Create the WebSocket handshake response.
			HttpResponse response = null;
			try {
				response = constructHandShakeResponse(request, context);
			} catch (NoSuchAlgorithmException lNSAEx) {
				// better to close the channel
				logger.debug("Channel is disconnected");
				context.getChannel().close();
			}

			// write the response
			context.getChannel().write(response);

			channels.add(context.getChannel());

			// since handshaking is done, replace the encoder/decoder with
			// web socket data frame encoder/decoder
			ChannelPipeline pipeLine = context.getChannel().getPipeline();
			pipeLine.remove("aggregator");
			EngineConfiguration econfig = engine.getConfiguration();
			if (econfig == null || econfig.getMaxFramesize() == 0) {
				pipeLine.replace("decoder", "jwsdecoder", new WebSocketFrameDecoder(CommonConstants.DEFAULT_MAX_FRAME_SIZE));
			} else {
				pipeLine.replace("decoder", "jwsdecoder", new WebSocketFrameDecoder(econfig.getMaxFramesize()));
			}
			pipeLine.replace("encoder", "jwsencoder", new WebSocketFrameEncoder());

			// if the WebSocket connection URI is wss then start SSL TLS handshaking
			if (request.getUri().startsWith("wss:")) {
				// Get the SslHandler in the current pipeline.
				final SslHandler sslHandler = context.getPipeline().get(SslHandler.class);
				// Get notified when SSL handshake is done.
				ChannelFuture lHandshakeFuture = sslHandler.handshake();
				lHandshakeFuture.addListener(new SecureWebSocketConnectionListener(sslHandler));
			}
			// initialize the connector
			connector = initializeConnector(context, request);

			return;
		}

		// Send an error page otherwise.
		sendHttpResponse(context, request, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
	}

	/**
	 * Constructs the <tt>HttpResponse</tt> object for the handshake response
	 * 
	 * @param request
	 *            the http request object
	 * @param context
	 *            the channel handler context
	 * @return the http handshake response
	 * @throws NoSuchAlgorithmException
	 */
	private HttpResponse constructHandShakeResponse(HttpRequest request, ChannelHandlerContext context) throws NoSuchAlgorithmException {
		HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(101, "Web Socket Protocol Handshake"));
		response.addHeader(HttpHeaders.Names.UPGRADE, HttpHeaders.Values.WEBSOCKET);
		response.addHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.UPGRADE);

		// Fill in the headers and contents depending on handshake method.
		if (request.containsHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY1) && request.containsHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY2)) {
			// New handshake method with a challenge:
			response.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_ORIGIN, request.getHeader(HttpHeaders.Names.ORIGIN));
			response.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_LOCATION, getWebSocketLocation(request));
			String lProtocol = request.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL);
			// Added by Alex 2010-10-25:
			// fallback for FlashBridge (which sends "WebSocket-Protocol"
			// instead of "Sec-WebSocket-Protocol"
			if (lProtocol != null) {
				response.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL, lProtocol);
			} else {
				lProtocol = request.getHeader(HttpHeaders.Names.WEBSOCKET_PROTOCOL);
				if (lProtocol != null) {
					response.addHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL, lProtocol);
				}
			}
			// Calculate the answer of the challenge.
			String key1 = request.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY1);
			String key2 = request.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_KEY2);
			int aint = (int) (Long.parseLong(key1.replaceAll("[^0-9]", "")) / key1.replaceAll("[^ ]", "").length());
			int bint = (int) (Long.parseLong(key2.replaceAll("[^0-9]", "")) / key2.replaceAll("[^ ]", "").length());
			long clong = request.getContent().readLong();
			ChannelBuffer lInput = ChannelBuffers.buffer(16);
			lInput.writeInt(aint);
			lInput.writeInt(bint);
			lInput.writeLong(clong);
			ChannelBuffer lOutput = ChannelBuffers.wrappedBuffer(MessageDigest.getInstance("MD5").digest(lInput.array()));
			response.setContent(lOutput);
		} else {
			// Old handshake method with no challenge:
			// lResp.addHeader(HttpHeaders.Names.WEBSOCKET_ORIGIN, aReq.getHeader(HttpHeaders.Names.ORIGIN));
			response.addHeader(HttpHeaders.Names.WEBSOCKET_ORIGIN, "http://localhost:8787");
			response.addHeader(HttpHeaders.Names.WEBSOCKET_LOCATION, getWebSocketLocation(request));
			String protocol = request.getHeader(HttpHeaders.Names.WEBSOCKET_PROTOCOL);
			if (protocol != null) {
				response.addHeader(HttpHeaders.Names.WEBSOCKET_PROTOCOL, protocol);
			}
		}
		return response;

	}

	/**
	 * Initialize the {@code NettyConnector} after initial handshaking is successfull.
	 * 
	 * @param context
	 *            the channel handler context
	 * @param req
	 *            the http request object
	 */
	private WebSocketConnector initializeConnector(ChannelHandlerContext context, HttpRequest request) {

		RequestHeader header = getRequestHeader(request);
		int sessionTimeout = header.getTimeout(CommonConstants.DEFAULT_TIMEOUT);
		if (sessionTimeout > 0) {
			context.getChannel().getConfig().setConnectTimeoutMillis(sessionTimeout);
		}
		// create connector
		WebSocketConnector newConn = new NettyConnector(engine, this);
		newConn.setHeader(header);

		engine.getConnectors().add(newConn);
		newConn.startConnector();
		// allow descendant classes to handle connector started event

		setUserInfo(newConn, request);
		setURIAttribute(newConn, request);

		engine.connectorStarted(newConn);
		return newConn;

	}

	private void setURIAttribute(WebSocketConnector conn, HttpRequest req) {
		Map<String, String> patternValues = URIParser.parse(req.getUri(), engine.getServer().getConfiguration().getURIPath());

		for (Entry<String, String> pvalue : patternValues.entrySet()) {
			conn.setString(pvalue.getKey(), pvalue.getValue());
		}
		conn.setString(BaseConnector.VAR_REQUEST_URI, req.getUri());
	}

	private void setUserInfo(WebSocketConnector conn, HttpRequest req) {
		String userInfo = req.getHeader(CommonConstants.XUSER_INFO);
		if (StringUtil.isBlank(userInfo))
			return;

		String[] connectInfo = StringUtil.split(userInfo, "/:;");

		if (connectInfo.length == 1) {
			conn.setUsername(connectInfo[0]);
		} else if (connectInfo.length == 2) {
			conn.setUsername(connectInfo[0]);
			conn.getSession().setSessionId(connectInfo[1]);
		}
	}

	/**
	 * Construct the request header to save it in the connector
	 * 
	 * @param request
	 *            the http request header
	 * @return the request header
	 */
	private RequestHeader getRequestHeader(HttpRequest request) {
		RequestHeader header = new RequestHeader();
		Map<String, String> args = new FastMap<String, String>();
		String searchString = "";
		String path = request.getUri();

		// isolate search string
		int pos = path.indexOf(CommonConstants.PATHARG_SEPARATOR);
		if (pos >= 0) {
			searchString = path.substring(pos + 1);
			if (searchString.length() > 0) {
				String[] lKeyValPairs = searchString.split(CommonConstants.ARGARG_SEPARATOR);
				for (int lIdx = 0; lIdx < lKeyValPairs.length; lIdx++) {
					String[] lKeyVal = lKeyValPairs[lIdx].split(CommonConstants.KEYVAL_SEPARATOR, 2);
					if (lKeyVal.length == 2) {
						args.put(lKeyVal[0], lKeyVal[1]);
						logger.debug("arg" + lIdx + ": " + lKeyVal[0] + "=" + lKeyVal[1]);
					}
				}
			}
		}

		// set default sub protocol if none passed
		// if no sub protocol given in request header,
		// try to get it from arguments
		String subProt = request.getHeader(HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL);
		if (subProt == null) {
			subProt = args.get(RequestHeader.WS_PROTOCOL);
		}
		if (subProt == null) {
			subProt = CommonConstants.WS_SUBPROT_DEFAULT;
		}
		header.put(RequestHeader.URL_ARGS, args);
		header.put(RequestHeader.WS_ORIGIN, request.getHeader(HttpHeaders.Names.ORIGIN));
		header.put(RequestHeader.WS_LOCATION, getWebSocketLocation(request));
		header.put(RequestHeader.WS_PATH, request.getUri());

		header.put(RequestHeader.WS_PROTOCOL, subProt);

		header.put(RequestHeader.WS_SEARCHSTRING, searchString);
		header.put(RequestHeader.WS_HOST, request.getHeader(HttpHeaders.Names.HOST));
		return header;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeComplete(ChannelHandlerContext context, WriteCompletionEvent event) throws Exception {
		super.writeComplete(context, event);
	}

	/**
	 * Returns the web socket location URL
	 * 
	 * @param request
	 *            the http request object
	 * @return the location url string
	 */
	private String getWebSocketLocation(HttpRequest request) {
		// TODO: fix this URL for wss: (secure)
		String location = "ws://" + request.getHeader(HttpHeaders.Names.HOST) + request.getUri();
		return location;
	}

	/**
	 * Returns the channel context
	 * 
	 * @return the channel context
	 */
	public ChannelHandlerContext getChannelHandlerContext() {
		return context;
	}

	/**
	 * Listener class for SSL TLS handshake completion.
	 */
	private static final class SecureWebSocketConnectionListener implements ChannelFutureListener {

		private final SslHandler mSSLHandler;

		SecureWebSocketConnectionListener(SslHandler aSSLHandler) {
			this.mSSLHandler = aSSLHandler;
		}

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				// that means SSL handshaking is done.
				logger.info("SSL handshaking success");
			} else {
				future.getChannel().close();
			}
		}
	}
}
