//	---------------------------------------------------------------------------
//	jWebSocket - TCP Engine
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
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
package net.ion.websocket.server.engine.tcp;

import com.sun.net.ssl.internal.ssl.SSLSocketImpl;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Date;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.log4j.Logger;

import net.ion.framework.util.Debug;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.config.CommonConstants;
import net.ion.websocket.common.config.EngineConfiguration;
import net.ion.websocket.common.config.ServerConstants;
import net.ion.websocket.common.engine.BaseEngine;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.RequestHeader;
import net.ion.websocket.common.kit.WebSocketException;
import net.ion.websocket.common.kit.WebSocketHandshake;
import net.ion.websocket.server.engine.EngineConfig;

/**
 * Implementation of the jWebSocket TCP engine. The TCP engine provide a Java Socket implementation of the WebSocket protocol. It contains the handshake
 * 
 * @author aschulze
 * @author jang
 */
public class TCPEngine extends BaseEngine {

	private static Logger logger = Logging.getLogger(TCPEngine.class);
	private ServerSocket tcpServerSocket = null;
	private SSLServerSocket sslServerSocket = null;
	private int tcpListenerPort = CommonConstants.DEFAULT_PORT;
	private int sslListenerPort = CommonConstants.DEFAULT_SSLPORT;
	private int sessionTimeout = CommonConstants.DEFAULT_TIMEOUT;
	private String keyStore = ServerConstants.WEBSOCKET_KEYSTORE;
	private String keyStorePassword = ServerConstants.WEBSOCKET_KS_DEF_PWD;
	private boolean isRunning = false;
	private boolean eventsFired = false;
	private Thread tcpEngineThread = null;
	private Thread sslEngineThread = null;

	public TCPEngine(EngineConfiguration econfig) {
		super(econfig);
		tcpListenerPort = econfig.getPort();
		sslListenerPort = econfig.getSSLPort();
		sessionTimeout = econfig.getTimeout();
		keyStore = econfig.getKeyStore();
		keyStorePassword = econfig.getKeyStorePassword();
	}
	
	public final static TCPEngine test(int port){
		return new TCPEngine(EngineConfig.test(port)) ;
	}
	

	@Override
	public void startEngine() throws WebSocketException {

		// create unencrypted server socket for ws:// protocol
		logger.debug("Starting TCP engine '" + getId() + "' at port " + tcpListenerPort + " with default timeout " + (sessionTimeout > 0 ? sessionTimeout + "ms" : "infinite") + "...");
		try {
			tcpServerSocket = new ServerSocket(tcpListenerPort);

			EngineListener listener = new EngineListener(this, tcpServerSocket);
			tcpEngineThread = new Thread(listener);
			tcpEngineThread.start();

		} catch (IOException ex) {
			throw new WebSocketException(ex.getMessage());
		}

		// TODO: results in firing started event twice! make more clean!
		// super.startEngine();
		logger.info("TCP engine '" + getId() + "' started' at port " + tcpListenerPort + " with default timeout " + (sessionTimeout > 0 ? sessionTimeout + "ms" : "infinite") + ".");

		// create encrypted (SSL) server socket for wss:// protocol
		if (sslListenerPort > 0) {
			if (keyStore != null && !keyStore.isEmpty() && keyStorePassword != null && !keyStorePassword.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("Starting SSL engine '" + getId() + "' at port " + sslListenerPort + " with default timeout " + (sessionTimeout > 0 ? sessionTimeout + "ms" : "infinite") + "...");
				}
				try {
					SSLContext sslContext = SSLContext.getInstance("SSL");
					KeyManagerFactory keyManagerFac = KeyManagerFactory.getInstance("SunX509");
					KeyStore myKeyStore = KeyStore.getInstance("JKS");

					// String lKeyStorePath = JWebSocketConfig.getConfigFolder(mKeyStore);
					String keyStorePath = keyStore;
					if (keyStorePath != null) {
						char[] password = keyStorePassword.toCharArray();
						myKeyStore.load(new FileInputStream(keyStorePath), password);
						keyManagerFac.init(myKeyStore, password);

						sslContext.init(keyManagerFac.getKeyManagers(), null, null);
						SSLServerSocketFactory sslFactory = sslContext.getServerSocketFactory();
						sslServerSocket = (SSLServerSocket) sslFactory.createServerSocket(sslListenerPort);
						EngineListener sslListener = new EngineListener(this, sslServerSocket);
						sslEngineThread = new Thread(sslListener);
						sslEngineThread.start();

						logger.info("SSL engine '" + getId() + "' started' at port " + sslListenerPort + " with default timeout " + (sessionTimeout > 0 ? sessionTimeout + "ms" : "infinite") + ".");
					} else {
						logger.error("SSL engine could not be instantiated: " + "KeyStore '" + keyStore + "' not found.");
					}
				} catch (Exception ex) {
					logger.error("SSL engine could not be instantiated: " + ex.getMessage());
				}
			} else {
				logger.error("SSL engine could not be instantiated due to missing configuration," + " please set sslport, keystore and password options.");
			}
		} else {
			logger.info("No SSL engine configured," + " set sslport, keystore and password options if desired.");
		}
	}

	@Override
	public void stopEngine(CloseReason reason) throws WebSocketException {
		logger.debug("Stopping TCP engine '" + getId() + "' at port " + tcpListenerPort + "...");

		// resetting "isRunning" causes engine listener to terminate
		isRunning = false;
		long startedTime = new Date().getTime();

		// close unencrypted TCP server socket
		try {
			// when done, close server socket
			// closing the server socket should lead to an IOExeption
			// at accept in the listener thread which terminates the listener
			if (tcpServerSocket != null && !tcpServerSocket.isClosed()) {
				tcpServerSocket.close();
				if (logger.isInfoEnabled()) {
					logger.info("TCP engine '" + getId() + "' stopped at port " + tcpListenerPort + " (closed=" + tcpServerSocket.isClosed() + ").");
				}
				tcpServerSocket = null;
			} else {
				logger.warn("Stopping TCP engine '" + getId() + "': no server socket or server socket closed.");
			}
		} catch (Exception ex) {
			logger.error(ex.getClass().getSimpleName() + " on stopping TCP engine '" + getId() + "': " + ex.getMessage());
		}

		// close encrypted SSL server socket
		try {
			// when done, close server socket
			// closing the server socket should lead to an IOExeption
			// at accept in the listener thread which terminates the listener
			if (sslServerSocket != null && !sslServerSocket.isClosed()) {
				sslServerSocket.close();
				if (logger.isInfoEnabled()) {
					logger.info("SSL engine '" + getId() + "' stopped at port " + sslListenerPort + " (closed=" + sslServerSocket.isClosed() + ").");
				}
				sslServerSocket = null;
			} else {
				logger.warn("Stopping SSL engine '" + getId() + "': no server socket or server socket closed.");
			}
		} catch (Exception ex) {
			logger.error(ex.getClass().getSimpleName() + " on stopping SSL engine '" + getId() + "': " + ex.getMessage());
		}

		// stop TCP listener thread
		if (tcpEngineThread != null) {
			try {
				// TODO: Make this timeout configurable one day
				tcpEngineThread.join(10000);
			} catch (Exception ex) {
				logger.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
			if (logger.isDebugEnabled()) {
				long duration = new Date().getTime() - startedTime;
				if (tcpEngineThread.isAlive()) {
					logger.warn("TCP engine '" + getId() + "' did not stop after " + duration + "ms.");
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("TCP engine '" + getId() + "' stopped after " + duration + "ms.");
					}
				}
			}
		}

		// stop SSL listener thread
		if (sslEngineThread != null) {
			try {
				// TODO: Make this timeout configurable one day
				sslEngineThread.join(10000);
			} catch (Exception ex) {
				logger.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
			if (logger.isDebugEnabled()) {
				long lDuration = new Date().getTime() - startedTime;
				if (sslEngineThread.isAlive()) {
					logger.warn("SSL engine '" + getId() + "' did not stop after " + lDuration + "ms.");
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("SSL engine '" + getId() + "' stopped after " + lDuration + "ms.");
					}
				}
			}
		}

		// inherited method stops all connectors
		startedTime = new Date().getTime();
		int numConns = getConnectors().size();
		super.stopEngine(reason);

		// now wait until all connectors have been closed properly
		// or timeout exceeds...
		try {
			while (getConnectors().size() > 0 && new Date().getTime() - startedTime < 10000) {
				Thread.sleep(250);
			}
		} catch (Exception ex) {
			logger.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
		}
		if (logger.isDebugEnabled()) {
			long lDuration = new Date().getTime() - startedTime;
			int lRemConns = getConnectors().size();
			if (lRemConns > 0) {
				logger.warn(lRemConns + " of " + numConns + " TCP connectors '" + getId() + "' did not stop after " + lDuration + "ms.");
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(numConns + " TCP connectors '" + getId() + "' stopped after " + lDuration + "ms.");
				}
			}
		}
	}

	@Override
	public void connectorStarted(WebSocketConnector connector) {
		if (logger.isDebugEnabled()) {
			logger.debug("Detected new connector at port " + connector.getRemotePort() + ".");
		}
		super.connectorStarted(connector);
	}

	@Override
	public void connectorStopped(WebSocketConnector connector, CloseReason closeReason) {
		if (logger.isDebugEnabled()) {
			logger.debug("Detected stopped connector at port " + connector.getRemotePort() + ".");
		}
		super.connectorStopped(connector, closeReason);
	}

	private RequestHeader processHandshake(Socket aClientSocket) throws UnsupportedEncodingException, IOException {

		InputStream input = aClientSocket.getInputStream();
		OutputStream output = aClientSocket.getOutputStream();

		// TODO: Replace this structure by more dynamic ByteArrayOutputStream?
		byte[] byteBuffer = new byte[8192];
		int readInt = input.read(byteBuffer);
		if (readInt <= 0) {
			logger.warn("Connection did not detect initial handshake.");
			return null;
		}
		byte[] byteReq = new byte[readInt];
		System.arraycopy(byteBuffer, 0, byteReq, 0, readInt);

		/* please keep comment for debugging purposes! */
		if (logger.isDebugEnabled()) {
			logger.debug("Parsing handshake request: " + new String(byteReq).replace("\r\n", "\\n"));
			// mLog.debug("Parsing initial WebSocket handshake...");
		}
		Map resMap = WebSocketHandshake.parseC2SRequest(byteReq, aClientSocket instanceof SSLSocketImpl);

		RequestHeader header = EngineUtils.validateC2SRequest(resMap, logger);
		if (header == null) {
			return null;
		}

		// generate the websocket handshake
		// if policy-file-request is found answer it
		byte[] ba = WebSocketHandshake.generateS2CResponse(resMap);
		if (ba == null) {
			if (logger.isDebugEnabled()) {
				logger.warn("TCPEngine detected illegal handshake.");
			}
			return null;
		}

		/* please keep comment for debugging purposes! */
		logger.debug("Flushing handshake response: " + new String(ba).replace("\r\n", "\\n"));
		// mLog.debug("Flushing initial WebSocket handshake...");

		output.write(ba);
		output.flush();

		// maybe the request is a flash policy-file-request
		String flashBridgeReq = (String) resMap.get("policy-file-request");
		if (flashBridgeReq != null) {
			logger.warn("TCPEngine returned policy file request ('" + flashBridgeReq + "'), check for FlashBridge plug-in.");
		}

		// if we detected a flash policy-file-request return "null"
		// (no websocket header detected)
		if (flashBridgeReq != null) {
			logger.warn("TCP Engine returned policy file response ('" + new String(ba, "US-ASCII") + "'), check for FlashBridge plug-in.");
			return null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Handshake flushed.");
		}

		return header;
	}

	@Override
	/*
	 * Returns {@code true} if the TCP engine is running or {@code false} otherwise. The alive status represents the state of the TCP engine listener thread.
	 */
	public boolean isAlive() {
		return (tcpEngineThread != null && tcpEngineThread.isAlive());
	}

	private class EngineListener implements Runnable {

		private WebSocketEngine engine = null;
		private ServerSocket server = null;

		/**
		 * Creates the server socket listener for new incoming socket connections.
		 * 
		 * @param engine
		 */
		public EngineListener(WebSocketEngine engine, ServerSocket serverSocket) {
			this.engine = engine;
			this.server = serverSocket;
		}

		@Override
		public void run() {
			Thread.currentThread().setName("WebSocket TCP-Engine (" + server.getLocalPort() + ", " + (server instanceof SSLServerSocket ? "SSL secured)" : "non secured)"));

			// notify server that engine has started
			if (!eventsFired) {
				eventsFired = true;
				engineStarted();
			}

			isRunning = true;
			while (isRunning) {
				try {
					// accept is blocking so here is no need to put any sleeps into this loop
					// if (log.isDebugEnabled()) {
					// log.debug("Waiting for client...");
					// }
					Socket clientSocket = server.accept();
					boolean tcpNoDelay = clientSocket.getTcpNoDelay();
					clientSocket.setTcpNoDelay(true);
					try {
						// process handshake to parse header data
						RequestHeader header = processHandshake(clientSocket);
						if (header != null) {
							// set socket timeout to given amount of milliseconds
							// use tcp engine's timeout as default and
							// check system's min and max timeout ranges
							int mySessionTimeout = header.getTimeout(getConfiguration().getTimeout());
							/*
							 * min and max range removed since 0.9.0.0602, see config documentation if (lSessionTimeout > JWebSocketServerConstants.MAX_TIMEOUT) { lSessionTimeout = JWebSocketServerConstants.MAX_TIMEOUT; } else if (lSessionTimeout < JWebSocketServerConstants.MIN_TIMEOUT) { lSessionTimeout = JWebSocketServerConstants.MIN_TIMEOUT; }
							 */
							if (mySessionTimeout > 0) {
								clientSocket.setSoTimeout(mySessionTimeout);
							}
							// create connector and pass header
							// log.debug("Instantiating connector...");
							WebSocketConnector conn = new TCPConnector(engine, clientSocket);
							conn.setVersion(header.getVersion());

							String logInfo = conn.isSSL() ? "SSL" : "TCP";
							if (logger.isDebugEnabled()) {
								logger.debug(logInfo + " client accepted on port " + clientSocket.getPort() + " with timeout " + (mySessionTimeout > 0 ? mySessionTimeout + "ms" : "infinite") + " (TCPNoDelay was: " + tcpNoDelay + ")...");
							}

							// log.debug("Setting header to engine...");
							conn.setHeader(header);
							// log.debug("Adding connector to engine...");
							getServer().addConnector(conn) ;
							// getConnectors().put(conn.getId(), conn);
							if (logger.isDebugEnabled()) {
								logger.debug("Starting " + logInfo + " connector...");
							}
							conn.startConnector();
						} else {
							// if header could not be parsed properly
							// immediately disconnect the client.
							clientSocket.close();
						}
					} catch (UnsupportedEncodingException ex) {
						logger.error("(encoding) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					} catch (IOException ex) {
						logger.error("(io) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					} catch (Exception ex) {
						logger.error("(other) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
					}
				} catch (Exception ex) {
					isRunning = false;
					logger.error("(accept) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
				}
			}

			// notify server that engine has stopped
			// this closes all connections
			if (eventsFired) {
				eventsFired = false;
				engineStopped();
			}
		}
	}
}
