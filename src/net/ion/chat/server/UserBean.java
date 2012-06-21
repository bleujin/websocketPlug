package net.ion.chat.server;

import java.util.Map;

import net.ion.framework.db.Row;
import net.ion.framework.db.Rows;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;

public class UserBean {

	private Map<String, ?> map;
	public final static UserBean TESTUSER = new UserBean(MapUtil.<String, Object>chainMap().put("USER_ID", "N/A").put("NICKNAME", "bleujin").toMap()) ;
	public final static String Test_Failer_UserId = "" ;
	public final static UserBean ARADON = new UserBean(MapUtil.create("USER_ID", "__aradon")) ;
	
	private UserBean(Map<String, ?> firstRow) {
		this.map = firstRow;
	}

	public final static UserBean create(Rows rows, String userId) {
		if (rows.getRowCount() < 1) return UserBean.notRegister(userId) ;
		return new UserBean(rows.firstRow().toMap());
	}
	
	private static UserBean notRegister(String userId) {
		return new UserBean(MapUtil.chainKeyMap().put("USER_ID", userId).put("LOGIN_ID", userId).put("USER_NAME", userId).put("DEVICE_TOKEN", "not_defined").put("DEVICE_ID", "not_defined").put("NICKNAME", userId).toMap());
	}
	

	public String getString(String fieldId) {
		return ObjectUtil.toString(map.get(fieldId));
	}

	public Object get(String fieldId) {
		return map.get(fieldId);
	}

	public String getUserId() {
		return getString("USER_ID");
	}
	
	public String getSenderName(){
		return StringUtil.defaultIfEmpty(StringUtil.defaultIfEmpty(getNickName(), getUserName()), getUserId()) ;
	}

	public String getDeviceId() {
		return getString("DEVICE_ID");
	}

	public String getDeviceToken() {
		return getString("DEVICE_TOKEN");
	}

	public String getUserName() {
		return getString("USER_NAME");
	}

	public String getLoginId() {
		return getString("LOGIN_ID");
	}

	public String getCell() {
		return getString("CELL");
	}

	public String getEmail() {
		return getString("EMAIL");
	}

	public String getNickName() {
		return getString("NICKNAME");
	}
	
	public String toString(){
		return map.toString() ;
	}

}
