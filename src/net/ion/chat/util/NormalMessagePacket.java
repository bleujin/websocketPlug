package net.ion.chat.util;

import java.util.Map;

import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;



public class NormalMessagePacket implements IMessagePacket {

	public static final NormalMessagePacket EMPTY = NormalMessagePacket.load("{}");
	public static final NormalMessagePacket PING = NormalMessagePacket.load("{head:{command:'ping'},body:{}}");

	private final JsonObject root;
	private final String currentPath;
	private final JsonObject current;

	private Map<String, NormalMessagePacket> childMap ;
	
	private NormalMessagePacket(JsonObject root) {
		this.root = root;
		this.currentPath = "";
		this.current = root;
		this.childMap = MapUtil.newCaseInsensitiveMap() ;
		childMap.put("", this) ;
	}

	private NormalMessagePacket(JsonObject root, String currentPath, JsonObject current, Map<String, NormalMessagePacket> childMap) {
		this.root = root;
		this.currentPath = currentPath;
		this.current = current;
		this.childMap = childMap ;
	}

	public final static NormalMessagePacket load(String message) {
		return new NormalMessagePacket(JsonParser.fromString(message).getAsJsonObject());
	}

	public static NormalMessagePacket create() {
		return new NormalMessagePacket(new JsonObject());
	}

	public static NormalMessagePacket load(JsonObject jsonObject) {
		return new NormalMessagePacket(jsonObject);
	}

	public NormalMessagePacket inner(String _inname) {
		String inname = _inname.toLowerCase() ;
		if (!current.has(inname)) {
			current.add(inname, new JsonObject());
		}
		
		String newPath = (isRoot() ? "" : currentPath + ".") + inname;
		if (! childMap.containsKey(newPath)){
			JsonObject newCurrent = current.asJsonObject(inname);
			childMap.put(newPath, new NormalMessagePacket(root, newPath, newCurrent, this.childMap)) ;
		}  

		return childMap.get(newPath) ;
	}


	public Object get(String _path) {
		return get(this.current, _path) ;
	}
	public Object get(JsonObject stdObj, String path) {
		return JsonUtil.findSimpleObject(stdObj, path) ;
	}

	public String getString(String path) {
		return ObjectUtil.toString(get(this.current, path));
	}

	public int getInt(String path, int dftvalue){
		Object object = get(path);
		return Integer.parseInt(ObjectUtil.toString(object, dftvalue + "")) ;
	}

	
	public String getString(String path, String dftValue) {
		String result = getString(path);
		if (StringUtil.isBlank(result))
			return dftValue;
		return result;
	}

	public NormalMessagePacket toParent() {
		String parentPath = "";
		if (StringUtil.contains(currentPath, ".")) {
			parentPath = StringUtil.substringBeforeLast(currentPath, ".");
		}
		
		return childMap.get(parentPath) ;
	}

	public NormalMessagePacket toRoot() {
		return childMap.get("");
	}

	public String getFullString() {
		return root.toString();
	}

	public boolean has(String path) {
		return ! StringUtil.isBlank(getString(path));
	}

	public NormalMessagePacket put(String key, Object value) {
		current.put(key, value);

		return this;
	}

	public NormalMessagePacket array(String key, Object[] values) {
		return put(key, values);
	}

	public Map<String, ? extends Object> toMap() {
		return current.toMap();
	}

	private boolean isRoot() {
		return StringUtil.isBlank(this.currentPath);
	}

	public String toString() {
		return getFullString();
	}

	public boolean isPing() {
		return "ping".equals(getString("head.command")) || "keepalive".equals(getString("head.command")) ;
	}

}