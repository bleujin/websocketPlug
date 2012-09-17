package net.ion.nchat.aradon;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.ion.framework.util.StringUtil;
import net.ion.radon.core.filter.IRadonFilter;
import net.ion.radon.core.security.ChallengeAuthenticator;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.MapVerifier;
import org.restlet.security.Verifier;

public class AdminPageFilter extends ChallengeAuthenticator {

	private AdminPageFilter(String startwithIp) {
		super("admin zone", new MyVerifier(startwithIp, "kalce", "bleujin"));
	}

	public AdminPageFilter(String startwithIp, String id, String pwd) {
		super("admin zone", new MyVerifier(startwithIp, id, pwd));
	}
	
	public static IRadonFilter create() {
		return new AdminPageFilter("61.250.201.");
	}

	public static IRadonFilter test(String startwithIp) {
		return new AdminPageFilter(startwithIp);
	}

}

class MyVerifier implements Verifier {

	private String startWithIp;
	private Verifier admin;

	public MyVerifier(String startwithIp, String adminId, String adminPwd) {
		this.startWithIp = startwithIp;
		ConcurrentMap<String, char[]> users = new ConcurrentHashMap<String, char[]>();
		users.put(adminId, adminPwd.toCharArray());
		this.admin = new MapVerifier(users);
	}

	public int verify(Request request, Response response) {

		String clientIp = request.getClientInfo().getAddress();
		if (StringUtil.isBlank(clientIp) || clientIp.startsWith(startWithIp)) {
			return 4;
		} else {
			return admin.verify(request, response);
		}
	}

}
