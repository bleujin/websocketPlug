package net.ion.chat.server;

import java.util.Map;

import net.ion.chat.api.ChatConstants;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;

public class UserBean {

	private Map<String, String> map = MapUtil.newMap() ;
	private UserBean(String userId) {
		map.put(ChatConstants.VAR_USERID, userId );
	}

	public final static UserBean create(String userId) {
		return new UserBean(userId);
	}
	
	public String getString(String fieldId) {
		return ObjectUtil.toString(map.get(fieldId));
	}

	public Object get(String fieldId) {
		return map.get(fieldId);
	}

	public String getUserId() {
		return map.get(ChatConstants.VAR_USERID);
	}
	
	public String getSenderName(){
		return getUserId() ;
	}
	public String toString(){
		return map.toString() ;
	}

}
