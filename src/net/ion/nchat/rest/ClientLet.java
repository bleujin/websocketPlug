package net.ion.nchat.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.radon.core.config.AradonConstant;
import net.ion.radon.core.let.AbstractServerResource;

import org.antlr.stringtemplate.StringTemplate;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public class ClientLet extends AbstractServerResource {

	@Get
	public Representation viewPage() throws FileNotFoundException, IOException, URISyntaxException {

		String userId = getInnerRequest().getAttribute("userId");
		String userIp = getInnerRequest().getClientInfo().getAddress();
		String topicId = getInnerRequest().getAttribute("topicId");

		JsonObject winfo = getTargetWebsocketServer(topicId, userId, userIp);

		// net.ion.toon.aradon.ClientLet
		String fileName = getContext().getAttributeObject(ClientLet.class.getCanonicalName(), "./resource/toonweb/chat.tpl", String.class);
		File tplFile = new File(fileName);
		if (!tplFile.exists())
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "not found template file : " + fileName);

		StringTemplate st = new StringTemplate(IOUtil.toString(new FileInputStream(tplFile)));
		Map<String, String> configMap = MapUtil.newMap();
		configMap.put("address", winfo.asString("path"));
		configMap.put("sender", userId);
		configMap.put("topicId", topicId);
		st.setAttribute("config", configMap);

		return new StringRepresentation(st.toString(), MediaType.TEXT_HTML, Language.ALL);
	}

	protected JsonObject getTargetWebsocketServer(String topicId, String userId, String userIp) throws URISyntaxException, UnknownHostException {
		String authKey = "0";
		Object port = getContext().getAttributeObject(AradonConstant.CONFIG_PORT) ;
		String portString = ObjectUtil.toString(port, "9000") ;

		URI path = new URI("ws://" + InetAddress.getLocalHost().getHostAddress() + ":" + portString + "/async/chat/" + topicId + "/" + userId);

		JsonObject jsonObject = new JsonObject();
		jsonObject.put("path", path.toString());
		jsonObject.put("userid", userId);
		jsonObject.put("authkey", authKey);

		return jsonObject;
	}
}
