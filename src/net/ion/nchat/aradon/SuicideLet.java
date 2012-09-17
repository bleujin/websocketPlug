package net.ion.nchat.aradon;

import java.util.Timer;
import java.util.TimerTask;

import net.ion.nradon.Radon;
import net.ion.radon.core.let.AbstractServerResource;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;

public class SuicideLet extends AbstractServerResource {

	@Get
	public String getMyName() {
		final Radon server = getContext().getAttributeObject(Radon.class.getCanonicalName(), Radon.class);
		return "Hello.. ServerID is" + server.getConfig().aradon().getGlobalConfig().server().id();
	}

	@Delete
	public String suicide() {

		long timeoutMili = Math.max(getInnerRequest().getParameterAsInteger("timeout"), 100);

		final Radon server = getContext().getAttributeObject(Radon.class.getCanonicalName(), Radon.class);
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
