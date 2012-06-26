package net.ion.chat.aradon;

import java.util.Timer;
import java.util.TimerTask;

import net.ion.chat.server.ChatServer;
import net.ion.radon.core.let.AbstractServerResource;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;

public class SuicideLet extends AbstractServerResource {

	@Get
	public String getMyName() {
		final ChatServer server = getContext().getAttributeObject(ChatServer.class.getCanonicalName(), ChatServer.class);

		return "Hello.. ServerID is" + server.getEngine().getId();
	}

	@Delete
	public String suicide() {

		long timeoutMili = Math.max(getInnerRequest().getParameterAsInteger("timeout"), 100);

		final ChatServer server = getContext().getAttributeObject(ChatServer.class.getCanonicalName(), ChatServer.class);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					server.stop();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.exit(0);
				}
			}

		}, timeoutMili);

		return timeoutMili + "(ms) shutdown..";
	}
}
