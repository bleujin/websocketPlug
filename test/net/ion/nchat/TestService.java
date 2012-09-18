package net.ion.nchat;

import java.awt.Desktop;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.restlet.Response;
import org.restlet.data.Method;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.StringUtil;
import net.ion.nradon.Radon;
import net.ion.nradon.RadonServer;
import net.ion.radon.Options;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.config.AradonConstant;
import junit.framework.TestCase;

public class TestService extends TestCase {

	public void testStart() throws Exception {
		RadonServer server = new RadonServer(new Options(new String[]{"-action:start", "-config:resource/config/aradon-config.xml"})) ;
		
		Radon radon = server.start() ;
		Debug.line(radon.getConfig().aradon().getServiceContext().getAttributes()) ;
		Debug.line(radon.getConfig().aradon().getServiceContext().getAttributeObject(AradonConstant.CONFIG_PORT)) ;
		
		
		AradonClient ac = AradonClientFactory.create("http://localhost:9000") ;
		IAradonRequest request = ac.createRequest("/rest/client/123/bleujin") ;
		
		Response response = request.handle(Method.GET);
//		Debug.line(response.getEntityAsText()) ;
		

//		Desktop.getDesktop().browse(new URI("http://localhost:8787/rest/client/topic/hero")) ;
//		Desktop.getDesktop().browse(new URI("http://localhost:8787/rest/client/topic/jin")) ;
		
		
		new InfinityThread().startNJoin() ;
	}
	
	
	public void testFirst() throws Exception {
		RadonServer server = new RadonServer(new Options(new String[]{"-action:start", "-config:resource/config/aradon-config.xml", "-port:9000"})) ;
		Radon radon = server.start() ;
		new InfinityThread().startNJoin() ;
	}

	public void testSecond() throws Exception {
		RadonServer server = new RadonServer(new Options(new String[]{"-action:start", "-config:resource/config/aradon-config.xml", "-port:9100"})) ;
		Radon radon = server.start() ;
		new InfinityThread().startNJoin() ;
	}

	public void testFindMyrIp() throws Exception {
		InputStream in = new URL("http://ip-echo.appspot.com/").openStream() ;
		Debug.line(StringUtil.trim(IOUtil.toString(in))) ;
		in.close() ;
		
	}
	
}
