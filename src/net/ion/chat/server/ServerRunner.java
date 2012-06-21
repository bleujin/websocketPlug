package net.ion.chat.server;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;

import net.ion.chat.handler.ServerConfigParser;
import net.ion.chat.handler.ChatEngine;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.Options;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.config.XMLConfig;

import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class ServerRunner {

	private ServerRunner(String configPath, int port) {

	}

	public static void main(String[] args) throws Exception {
		Options option = new Options(args);
		String configPath = option.getString("config", "./resource/config/server-config.xml");
		String action = option.getString("action", "restart");

		File configFile = new File(configPath);
		if (!configFile.exists()) {
			configFile = new File(configPath + ".tpl");
			if (!configFile.exists()) {
				throw new IllegalStateException("config file not found : " + configPath);
			}
		}
		ServerConfigParser scf = ServerConfigParser.parse(XMLConfig.create(configFile));

		ChatEngine engine = scf.getTalkEngine();

		int port = option.getInt("port", engine.getConfiguration().getPort());
		engine.getConfiguration().setPort(port);
		String sid = option.getString("sid", engine.getConfiguration().getId());
		engine.getConfiguration().setId(sid);

		Debug.line("configPath", configPath, "port", port, "action", action);

		if ("stop".equals(action)) {
			stopServer(port);
			System.exit(0);
		}

		if ("restart".equals(action)) {
			stopServer(port);
		}

		ChatServer toon = ChatServer.create(engine);
		toon.start();
		new InfinityThread().startNJoin();
	}

	private static void stopServer(int port) {
		try {
			AradonClient ac = AradonClientFactory.create("http://127.0.0.1:" + port);
			Representation rep = ac.createRequest("/aradon/shutdown?timeout=100").delete();
			Debug.line(rep.getText());
			Thread.sleep(1000);
		} catch (ConnectException ignore) {
			Debug.line(ignore);
		} catch (IOException ignore) {
			Debug.line(ignore);
		} catch (ResourceException ignore) {
			Debug.line(ignore);
		} catch (InterruptedException ignore) {
			Debug.line(ignore);
		}
	}
}
