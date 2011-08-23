package net.ion.websocket.server.engine.netty.http;


import java.io.Serializable;
import java.util.Comparator;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @version $Rev: 2080 $, $Date: 2011/07/23 04:35:53 $
 */
final class CaseIgnoringComparator implements Comparator<String>, Serializable {

	private static final long serialVersionUID = 4582133183775373862L;

	static final CaseIgnoringComparator INSTANCE = new CaseIgnoringComparator();

	private CaseIgnoringComparator() {
		super();
	}

	public int compare(String o1, String o2) {
		return o1.compareToIgnoreCase(o2);
	}

	private Object readResolve() {
		return INSTANCE;
	}
}