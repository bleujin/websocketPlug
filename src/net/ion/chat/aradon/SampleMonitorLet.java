package net.ion.chat.aradon;

import java.util.List;
import java.util.Map;

import net.ion.chat.api.ChatConstants;
import net.ion.chat.handler.ChatEngine;
import net.ion.chat.server.UserBean;
import net.ion.chat.server.UserConnector;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.WebSocketConnection;
import net.ion.radon.core.let.DefaultLet;

import org.restlet.representation.Representation;

public class SampleMonitorLet extends DefaultLet {

	@Override
	protected Representation myGet() throws Exception {
		ChatEngine engine = getMySectionService().getAradon().getServiceContext().getAttributeObject(ChatEngine.class.getCanonicalName(), ChatEngine.class) ;
		
		UserConnector[] conns =  engine.getAllConnectors() ;
		
		List<Map<String, ?>> data = ListUtil.newList() ;
		for (UserConnector conn : conns) {
			Map<String, Object> cinfo = MapUtil.newMap() ;
			cinfo.put(WebSocketConnection.VAR_USERID, conn.data(ChatConstants.VAR_USERID)) ;
			cinfo.put("$sessionId", conn.data(ChatConstants.VAR_SESSIONID)) ;
			cinfo.put("$user", conn.data(UserBean.class.getCanonicalName())) ;
			data.add(cinfo) ;
		}
		
		return toRepresentation(data); 
	}

}
