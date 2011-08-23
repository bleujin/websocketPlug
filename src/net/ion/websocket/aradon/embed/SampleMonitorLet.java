package net.ion.websocket.aradon.embed;

import java.util.List;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.let.DefaultLet;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.api.WebSocketServer;
import net.ion.websocket.common.connector.BaseConnector;

import org.restlet.representation.Representation;

public class SampleMonitorLet extends DefaultLet {

	@Override
	protected Representation myGet() throws Exception {
		WebSocketServer server = getMySectionService().getAradon().getServiceContext().getAttributeObject(WebSocketServer.class.getCanonicalName(), WebSocketServer.class) ;
		
		WebSocketConnector[] conns =  server.getAllConnectors() ;
		
		List<Map<String, ?>> data = ListUtil.newList() ;
		for (WebSocketConnector conn : conns) {
			Map<String, Object> cinfo = MapUtil.newMap() ;
			cinfo.put("$sessionId", conn.getSession().getSessionId()) ;
			cinfo.put(BaseConnector.VAR_REQUEST_URI, conn.getString(BaseConnector.VAR_REQUEST_URI)) ;
			cinfo.put("$user", conn.getString("userId")) ;
			data.add(cinfo) ;
		}
		
		return toRepresentation(data); 
	}

}
