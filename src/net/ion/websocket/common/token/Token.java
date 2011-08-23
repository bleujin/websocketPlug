//	---------------------------------------------------------------------------
//	jWebSocket - Token Implementation
//	Copyright (c) 2010 Alexander Schulze, Innotrade GmbH
//	---------------------------------------------------------------------------
//	This program is free software; you can redistribute it and/or modify it
//	under the terms of the GNU Lesser General Public License as published by the
//	Free Software Foundation; either version 3 of the License, or (at your
//	option) any later version.
//	This program is distributed in the hope that it will be useful, but WITHOUT
//	ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//	FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
//	more details.
//	You should have received a copy of the GNU Lesser General Public License along
//	with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
//	---------------------------------------------------------------------------
package net.ion.websocket.common.token;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.websocket.common.api.WebSocketToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A token is ...
 * 
 * @author aschulze
 */
public class Token extends BaseToken implements WebSocketToken {

	private JSONObject mData = new JSONObject();

	/**
	 * Creates a new empty instance of a token. The token does not contain any
	 * items.
	 */
	public Token() {
	}

	/**
	 * 
	 * @param aType
	 */
	public Token(String aType) {
		setType(aType);
	}

	/**
	 * 
	 * @param aJSON
	 */
	public Token(JSONObject aJSON) {
		mData = aJSON;
	}

	/**
	 * 
	 * @param namespace
	 * @param type
	 */
	public Token(String namespace, String type) {
		setNS(namespace);
		setType(type);
	}

	/**
	 * 
	 * @param json
	 */
	public void setJSONObject(JSONObject json) {
		mData = json;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public JSONObject getJSONObject() {
		return mData;
	}

	private Object getValue(Object value) {
		if (value instanceof Token) {
			value = ((Token) value).getJSONObject();
		} else if (value instanceof Collection) {
			JSONArray lJA = new JSONArray();
			for (Object lItem : (Collection) value) {
				lJA.put(getValue(lItem));
			}
			value = lJA;
		} else if (value instanceof Map) {
			JSONObject lJO = new JSONObject();
			for (Entry<Object, Object> lItem : ((Map<Object, Object>) value).entrySet()) {
				try {
					lJO.put(lItem.getKey().toString(), getValue(lItem.getValue()));
				} catch (JSONException ex) {
				}
			}
			value = lJO;
		} else if (value instanceof Object[]) {
			JSONArray lJA = new JSONArray();
			Object[] lOA = (Object[]) value;
			for (int i = 0; i < lOA.length; i++) {
				lJA.put(getValue(lOA[i]));
			}
			value = lJA;
		}
		return value;
	}

	/**
	 * puts a new key/value pair into the token, in other words it adds a new
	 * item to the token.
	 * 
	 * @param key
	 *            key of the the token item.
	 * @param value
	 *            value of the token item.
	 */
	public void put(String key, Object value) {
		try {
			mData.put(key, getValue(value));
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		try {
			return mData.get(key);
		} catch (JSONException ex) {
			return null;
		}
	}

	/**
	 * 
	 * @param aKey
	 * @return
	 */
	public void remove(String aKey) {
		mData.remove(aKey);
	}

	/**
	 * 
	 * @return
	 */
	public Iterator<String> getKeys() {
		return mData.keys();
	}

	/**
	 * 
	 * @param key
	 * @param dftValue
	 * @return
	 */
	public String getString(String key, String dftValue) {
		String lResult;
		try {
			lResult = mData.getString(key);
		} catch (JSONException ex) {
			lResult = dftValue;
		}
		return lResult;
	}

	/**
	 * 
	 * @param key
	 */
	public void setString(String key, String dftValue) {
		try {
			mData.put(key, dftValue);
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return getString(key, null);
	}

	/**
	 * 
	 * @param key
	 * @param dftValue
	 * @return
	 */
	public Integer getInteger(String key, Integer dftValue) {
		Integer lResult;
		try {
			lResult = mData.getInt(key);
		} catch (JSONException ex) {
			lResult = dftValue;
		}
		return lResult;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Integer getInteger(String key) {
		return getInteger(key, null);
	}

	public void setInteger(String key, Integer dftValue) {
		try {
			mData.put(key, dftValue);
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 * @param key
	 * @param dftValue
	 * @return
	 */
	public Boolean getBoolean(String key, Boolean dftValue) {
		Boolean lResult;
		try {
			lResult = mData.getBoolean(key);
		} catch (JSONException ex) {
			lResult = dftValue;
		}
		return lResult;
	}

	/**
	 * 
	 * @param arg
	 * @return
	 */
	public Boolean getBoolean(String arg) {
		return getBoolean(arg, null);
	}

	/**
	 * 
	 * @param key
	 */
	public void setBoolean(String key, Boolean dftValue) {
		try {
			mData.put(key, dftValue);
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 * 
	 * @param key
	 * @param dftValue
	 * @return
	 */
	public List getList(String key, Boolean dftValue) {
		// TODO: Implement this
		return null;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public List getList(String key) {
		// TODO: Implement this
		return null;
	}

	/**
	 * 
	 * @param key
	 * @param dftValue
	 * @return
	 */
	public Map getMap(String key, Boolean dftValue) {
		// TODO: Implement this
		return null;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Map getMap(String key) {
		// TODO: Implement this
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public final String getType() {
		return getString("type");
	}

	/**
	 * 
	 * @param type
	 */
	public final void setType(String type) {
		put("type", type);
	}

	/**
	 * Returns the name space of the token. If you have the same token type
	 * interpreted by multiple different plug-ins the name space allows to
	 * uniquely address a certain plug-in. Each plug-in has its own name space.
	 * 
	 * @return the name space.
	 */
	public final String getNS() {
		return getString("ns");
	}

	/**
	 * Sets the name space of the token. If you have the same token type
	 * interpreted by multiple different plug-ins the namespace allows to
	 * uniquely address a certain plug-in. Each plug-in has its own namespace.
	 * 
	 * @param namespace
	 *            the namespace to be set for the token.
	 */
	public final void setNS(String namespace) {
		put("ns", namespace);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		return mData.toString();
	}
}
