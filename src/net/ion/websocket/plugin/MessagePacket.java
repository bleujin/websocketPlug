package net.ion.websocket.plugin;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.kit.RawPacket;
import net.ion.websocket.common.kit.WebSocketRuntimeException;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessagePacket implements IMessagePacket {

	public final static MessagePacket EMPTY = new MessagePacket(ValueObject.create(new JSONObject()));
	public static final MessagePacket PING = new MessagePacket(ValueObject.createPing());

	private MessagePacket parent;
	private ValueObject vo;

	private MessagePacket(ValueObject vo) {
		this.vo = vo;
	}

	private MessagePacket(MessagePacket parent, ValueObject valueObj) {
		this.parent = parent;
		this.vo = valueObj;
	}

	public final static MessagePacket load(String message) {
		try {
			return new MessagePacket(ValueObject.load(message));
		} catch (JSONException e) {
			throw new WebSocketRuntimeException(e.getMessage());
		}
	}

	public static MessagePacket create() {
		return new MessagePacket(new ValueObject(new JSONObject()));
	}

	public static MessagePacket load(JSONObject jsonObject) {
		return new MessagePacket(new ValueObject(jsonObject));
	}

	public MessagePacket inner(String inname) throws WebSocketRuntimeException {
		try {
			if (vo.has(inname)) {
				return new MessagePacket(this, ValueObject.load(vo.getJSONObject(inname)));
			} else {
				JSONObject newJSON = new JSONObject();
				vo.put(inname, newJSON);
				return new MessagePacket(this, ValueObject.create(newJSON));
			}
		} catch (JSONException e) {
			throw new WebSocketRuntimeException(e.getMessage());
		}
	}

	public MessagePacket[] child(String path) {
		try {
			JSONArray array = (JSONArray) (get(path));

			List<MessagePacket> result = ListUtil.newList();
			for (int i = 0; i < array.length(); i++) {
				result.add(new MessagePacket(this, ValueObject.create(array.getJSONObject(i))));
			}
			return result.toArray(new MessagePacket[0]);
		} catch (JSONException e) {
			throw new WebSocketRuntimeException(e.getMessage());
		}
	}

	public Object get(String path) {
		try {
			return vo.get(path);
		} catch (JSONException e) {
			throw new WebSocketRuntimeException(e.getMessage());
		}
	}

	public String getString(String path) {
		try {
			return vo.getString(path, "");
		} catch (JSONException e) {
			return "";
		}
	}

	public String getString(String path, String dftValue) {
		try {
			return vo.getString(path, dftValue);
		} catch (JSONException e) {
			return dftValue;
		}
	}

	public MessagePacket toParent() {
		return parent;
	}

	public MessagePacket toRoot() {
		MessagePacket current = this;
		while (current.parent != null) {
			current = current.parent;
		}
		return current;
	}

	public String getFullString() {
		return toRoot().vo.getString();
	}

	public boolean has(String path) {
		return vo.has(path);
	}

	public MessagePacket put(String key, Object value) {
		try {
			vo.put(key, value);
			return this;
		} catch (JSONException e) {
			throw new WebSocketRuntimeException(e.getMessage());
		}
	}

	public MessagePacket append(String key, Object value) {
		try {
			vo.append(key, value);
			return this;
		} catch (JSONException e) {
			throw new WebSocketRuntimeException(e.getMessage());
		}
	}

	public JSONObject getJSONObject(String key) {
		try {
			return vo.getJSONObject(key);
		} catch (JSONException e) {
			throw new WebSocketRuntimeException(e.getMessage());
		}
	}

	public WebSocketPacket forSend() throws WebSocketRuntimeException {
		try {
			return new RawPacket(toRoot().getFullString());
		} catch (UnsupportedEncodingException e) {
			return new RawPacket(toRoot().getFullString().getBytes(Charset.forName("UTF-8")));
		}
	}

	public String toString() {
		return getFullString();
	}

	private static class ValueObject {

		private JSONObject json;
		private static ValueObject EMPTY = new ValueObject(new JSONObject());

		private ValueObject(JSONObject json) {
			this.json = json;
		}

		private static ValueObject createPing() {
			try {
				return load("{head:{command:'ping'},body:{}}");
			} catch (JSONException e) {
				return EMPTY;
			}
		}

		public String getString() {
			return json.toString();
		}

		public String getString(String path) throws JSONException {
			return ObjectUtil.toString(get(path));
		}

		public String getString(String path, String dftValue) throws JSONException {
			if (has(path)) {
				return getString(path);
			} else {
				return dftValue;
			}
		}

		public static ValueObject load(String msg) throws JSONException {
			return load(new JSONObject(msg));
		}

		public static ValueObject load(JSONObject json) {
			return new ValueObject(json);
		}

		public final static ValueObject create(JSONObject json) {
			return new ValueObject(json);
		}

		public JSONObject getJSONObject(String name) throws JSONException {
			return json.getJSONObject(toKeyId(name));
		}

		public Object get(String path) throws JSONException {
			if (StringUtil.isBlank(path))
				return null;
			String[] names = StringUtil.split(path, "./");
			if (names.length == 1) {
				return getObject(names[0]);
			} else {
				return ValueObject.create(this.getJSONObject(names[0])).get(StringUtil.join(ArrayUtils.subarray(names, 1, names.length), "."));
			}
		}

		public Object getObject(String name) throws JSONException {
			return json.get(toKeyId(name));
		}

		public void put(String key, Object value) throws JSONException {
			json.put(toKeyId(key), value);
		}

		public void append(String key, Object value) throws JSONException {
			json.accumulate(key, value);
		}

		boolean has(String path) {
			if (StringUtil.isBlank(path))
				return false;

			String[] names = StringUtil.split(path.toLowerCase(), "./");
			if (names.length == 1) {
				return json.has(names[0]);
			} else {
				try {
					if (!json.has(names[0]))
						return false;
					if (!(json.get(names[0]) instanceof JSONObject))
						return false;
					return ValueObject.create(this.getJSONObject(names[0])).has(StringUtil.join(ArrayUtils.subarray(names, 1, names.length), "."));
				} catch (JSONException ignore) {
					return false;
				}
			}
		}

		private String toKeyId(String key) {
			return key.toLowerCase();
		}

	}

}
