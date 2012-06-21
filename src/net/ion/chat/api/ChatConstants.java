package net.ion.chat.api;

public interface ChatConstants {
	public final static String XUSER_INFO = "XSOCKET-USER-INFO" ;
	public final static String VAR_REQUEST_URI = "$request_uri" ;
	public final static String VAR_AUTHID = "$authId";
	public final static String VAR_USERNAME = "$userName";
	public final static String VAR_USERID = "$userId";
	public final static String VAR_SERVICE_NAME = "$serviceName" ;
	public static final String VAR_SESSIONID = "$sessionId";
	
	public static final String TOON_HOME_DIR = "toon.home.dir" ;
	public static final String DEFAULT_CONFIG_FILENAME = "./resource/config/server-config.xml" ;
	
	public final static String PARAM_TOPICID = "_topicId";
	public final static String PARAM_TIMEOUT = "_timeout";
	
	
	public final static String MESSAGE_WORKSPACE_ID = "websocket";

	public final static String SERVER_GROUP = "servers";
	public final static String MESSAGE_GROUP = "messages";


	public final static String PUSH_WORKSPACE_ID = "push";
	public final static String PUSH_GROUP = "pushs";
	public final static String BADGE_GROUP = "badge";
}
