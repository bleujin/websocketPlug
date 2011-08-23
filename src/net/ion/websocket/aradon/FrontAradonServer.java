package net.ion.websocket.aradon;

import net.ion.radon.core.Aradon;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.config.AradonConfiguration;

public class FrontAradonServer {

	private Aradon aradon;

	private String configFilePath;
	private int portNo;

	public FrontAradonServer(String configFilePath, int portNo) {
		this.configFilePath = configFilePath;
		this.portNo = portNo;
	}

	public FrontAradonServer(AradonConfiguration config) {
		this(config.getConfigPath(), config.getPortNo());
	}

	void init() throws Exception {
		aradon = new Aradon();
		aradon.init(configFilePath);

		// aradon.start() ;
	}

	public Aradon getAradon() {
		return aradon;
	}

	void startAradon(final int port) throws Exception {
		init();
		new Thread() {
			public void run() {
				try {
					aradon.startServer(port);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void start(WebSocketServer server) throws Exception {
		int port = (portNo == 0) ? server.getEngine().getConfiguration().getPort() + 10000 : portNo;
		startAradon(port);
		aradon.getServiceContext().putAttribute(WebSocketServer.class.getCanonicalName(), server);
	}

	public void stop() {
		aradon.stop();
	}
}
