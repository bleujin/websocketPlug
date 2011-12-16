//	---------------------------------------------------------------------------
//	jWebSocket - FlashBridge Plug-In
//	Copyright (c) 2010 Innotrade GmbH (http://jWebSocket.org)
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License 
//  for more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package net.ion.websocket.common.plugin.flashbridge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import net.ion.websocket.common.api.WebSocketEngine;
import net.ion.websocket.common.config.PluginConfiguration;
import net.ion.websocket.common.logging.Logging;
import net.ion.websocket.common.plugin.TokenPlugIn;
import net.ion.websocket.common.util.Tools;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * This plug-in processes the policy-file-request from the browser side flash plug-in. This makes jWebSocket cross-browser-compatible.
 * 
 * @author aschulze
 */
public class FlashBridgePlugIn extends TokenPlugIn {

	private static Logger logger = Logging.getLogger(FlashBridgePlugIn.class);
	private ServerSocket serverSocket = null;
	private int listenerPort = 843;
	private boolean isRunning = false;
	private int engineInstanceCount = 0;
	private BridgeProcess bridgeProcess = null;
	private Thread bridgeThread = null;
	private final static String PATH_TO_CROSSDOMAIN_XML = "crossdomain_xml";
	private static String crossDomainXML = "<cross-domain-policy>" + "<allow-access-from domain=\"*\" to-ports=\"*\" />" + "</cross-domain-policy>";

	public FlashBridgePlugIn(PluginConfiguration pconfig) {
		super(pconfig);
		logger.debug("Instantiating FlashBridge plug-in...");

		mGetSettings();

		try {
			serverSocket = new ServerSocket(listenerPort);

			bridgeProcess = new BridgeProcess(this);
			bridgeThread = new Thread(bridgeProcess);
			bridgeThread.start();
			if (logger.isInfoEnabled()) {
				logger.info("FlashBridge plug-in successfully loaded.");
			}
		} catch (IOException ex) {
			logger.error("FlashBridge could not be started: " + ex.getMessage());
		}
	}

	private void mGetSettings() {
		// load global settings, default to "true"
		String pathToCrossDomainXML = getString(PATH_TO_CROSSDOMAIN_XML);
		if (pathToCrossDomainXML != null) {
			try {
				logger.debug("Trying to load " + pathToCrossDomainXML + "...");
				pathToCrossDomainXML = Tools.expandEnvVars(pathToCrossDomainXML);
				if (logger.isDebugEnabled()) {
					logger.debug("Trying to load expanded " + pathToCrossDomainXML + "...");
				}
				File file = new File(pathToCrossDomainXML);
				crossDomainXML = FileUtils.readFileToString(file, "UTF-8");
				logger.info("crossdomain config successfully loaded from " + pathToCrossDomainXML + ".");
			} catch (Exception lEx) {
				logger.error(lEx.getClass().getSimpleName() + " reading crossdomain.xml: " + lEx.getMessage());
			}
		}
	}

	private class BridgeProcess implements Runnable {

		private final FlashBridgePlugIn plugIn;

		/**
		 * creates the server socket bridgeProcess for new incoming socket connections.
		 * 
		 * @param aPlugIn
		 */
		public BridgeProcess(FlashBridgePlugIn aPlugIn) {
			this.plugIn = aPlugIn;
		}

		@Override
		public void run() {
			logger.debug("Starting FlashBridge process...");
			isRunning = true;
			Thread.currentThread().setName("jWebSocket FlashBridge");
			while (isRunning) {
				try {
					// accept is blocking so here is no need
					// to put any sleeps into the loop
					logger.debug("Waiting on flash policy-file-request on port " + serverSocket.getLocalPort() + "...");
					Socket clientSocket = serverSocket.accept();
					logger.debug("Client connected...");
					try {
						// clientSocket.setSoTimeout(TIMEOUT);
						InputStream input = clientSocket.getInputStream();
						OutputStream output = clientSocket.getOutputStream();
						byte[] ba = new byte[4096];
						String lineStr = "";
						boolean foundPolicyFileRequest = false;
						int lLen = 0;
						while (lLen >= 0 && !foundPolicyFileRequest) {
							lLen = input.read(ba);
							if (lLen > 0) {
								lineStr += new String(ba, 0, lLen, "US-ASCII");
							}
								logger.debug("Received " + lineStr + "...");
							foundPolicyFileRequest = lineStr.indexOf("policy-file-request") >= 0; // "<policy-file-request/>"
						}
						if (foundPolicyFileRequest) {
							logger.debug("Answering on flash policy-file-request (" + lineStr + ")...");
							// logger.debug("Answer: " + mCrossDomainXML);
							output.write(crossDomainXML.getBytes("UTF-8"));
							output.flush();
						} else {
							logger.warn("Received invalid policy-file-request (" + lineStr + ")...");
						}
					} catch (Exception ex) {
						logger.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
					}

					clientSocket.close();
					if (logger.isDebugEnabled()) {
						logger.debug("Client disconnected...");
					}
				} catch (Exception ex) {
					isRunning = false;
					logger.error("Socket state: " + ex.getMessage());
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("FlashBridge process stopped.");
			}
		}
	}

	@Override
	public void engineStarted(WebSocketEngine engine) {
		if (logger.isDebugEnabled()) {
			logger.debug("Engine '" + engine.getId() + "' started.");
		}
		// every time an engine starts increment counter
		engineInstanceCount++;
	}

	@Override
	public void engineStopped(WebSocketEngine engine) {
		if (logger.isDebugEnabled()) {
			logger.debug("Engine '" + engine.getId() + "' stopped.");
		}
		// every time an engine starts decrement counter
		engineInstanceCount--;
		// when last engine stopped also stop the FlashBridge
		if (engineInstanceCount <= 0) {
			super.engineStopped(engine);

			isRunning = false;
			long lStarted = new Date().getTime();

			try {
				// when done, close server socket
				// closing the server socket should lead to an exception
				// at accept in the bridgeProcess thread which terminates the
				// bridgeProcess
				logger.debug("Closing FlashBridge server socket...");
				serverSocket.close();
				logger.debug("Closed FlashBridge server socket.");
			} catch (Exception ex) {
				logger.error("(accept) " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}

			try {
				bridgeThread.join(10000);
			} catch (Exception ex) {
				logger.error(ex.getClass().getSimpleName() + ": " + ex.getMessage());
			}
			long lDuration = new Date().getTime() - lStarted;
			if (bridgeThread.isAlive()) {
				logger.warn("FlashBridge did not stopped after " + lDuration + "ms.");
			} else {
				logger.debug("FlashBridge stopped after " + lDuration + "ms.");
			}
		}
	}
}
