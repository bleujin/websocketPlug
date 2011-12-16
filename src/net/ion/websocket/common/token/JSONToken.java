//	---------------------------------------------------------------------------
//	jWebSocket - JSON Token Implementation
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javolution.util.FastMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A token is ...
 * @author aschulze
 */
public class JSONToken extends BaseToken implements Token {

	private JSONObject data = null;

	/**
	 * Creates a new empty instance of a token.
	 * The token does not contain any items.
	 */
	public JSONToken() {
		data = new JSONObject();
	}

	/**
	 *
	 * @param type
	 */
	public JSONToken(String type) {
		data = new JSONObject();
		setType(type);
	}

	/**
	 *
	 * @param json
	 */
	public JSONToken(JSONObject json) {
		data = json;
	}

	/**
	 *
	 * @param ns
	 * @param type
	 */
	public JSONToken(String ns, String type) {
		data = new JSONObject();
		setNS(ns);
		setType(type);
	}

	@Override
	public void clear() {
		// TODO: implement clear for JSON token
	}

	@Override
	public void set(ITokenizable tokenizable) {
	}

	/**
	 *
	 *
	 * @return
	 */
	@Override
	public Map getMap() {
		// TODO: implement getMap for JSON token
		return null;
	}

	/**
	 *
	 *
	 * @return
	 */
	@Override
	public void setMap(Map map) {
		// TODO: implement setMap for JSON token
	}

	/**
	 *
	 * @param json
	 */
	public void setJSONObject(JSONObject json) {
		data = json;
	}

	/**
	 *
	 *
	 * @return
	 */
	public JSONObject getJSONObject() {
		return data;
	}

	private Object getValue(Object value) {
		if (value instanceof JSONToken) {
			value = ((JSONToken) value).getJSONObject();
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
				} catch (JSONException lEx) {
				}
			}
			value = lJO;
		} else if (value instanceof Object[]) {
			JSONArray lJA = new JSONArray();
			Object[] lOA = (Object[]) value;
			for (int lIdx = 0; lIdx < lOA.length; lIdx++) {
				lJA.put(getValue(lOA[lIdx]));
			}
			value = lJA;
		}
		return value;
	}

	/**
	 * puts a new key/value pair into the token, in other words it adds a
	 * new item to the token.
	 * @param aKey key of the the token item.
	 * @param aValue value of the token item.
	 */
	public void put(String aKey, Object aValue) {
		try {
			data.put(aKey, getValue(aValue));
		} catch (JSONException lEx) {
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
			return data.get(key);
		} catch (JSONException lEx) {
			return null;
		}
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	@Override
	public void remove(String key) {
		data.remove(key);
	}

	/**
	 *
	 * @param key
	 * @param defaulVal
	 * @return
	 */
	@Override
	public String getString(String key, String defaulVal) {
		String lResult;
		try {
			lResult = data.getString(key);
		} catch (JSONException lEx) {
			lResult = defaulVal;
		}
		return lResult;
	}

	/**
	 *
	 * @param key
	 */
	@Override
	public void setString(String key, String value) {
		try {
			data.put(key, value);
		} catch (JSONException lEx) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	@Override
	public String getString(String key) {
		return getString(key, null);
	}

	/**
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public Integer getInteger(String key, Integer defaultVal) {
		Integer lResult;
		try {
			lResult = data.getInt(key);
		} catch (JSONException lEx) {
			lResult = defaultVal;
		}
		return lResult;
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	@Override
	public Integer getInteger(String key) {
		return getInteger(key, null);
	}

	@Override
	public void setInteger(String key, Integer val) {
		try {
			data.put(key, val);
		} catch (JSONException lEx) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public Double getDouble(String key, Double defaultVal) {
		Double lResult;
		try {
			lResult = data.getDouble(key);
		} catch (JSONException lEx) {
			lResult = defaultVal;
		}
		return lResult;
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	@Override
	public Double getDouble(String key) {
		return getDouble(key, null);
	}

	@Override
	public void setDouble(String key, Double val) {
		try {
			data.put(key, val);
		} catch (JSONException lEx) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public Boolean getBoolean(String key, Boolean defaultVal) {
		Boolean lResult;
		try {
			lResult = data.getBoolean(key);
		} catch (JSONException lEx) {
			lResult = defaultVal;
		}
		return lResult;
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	@Override
	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	/**
	 *
	 * @param key
	 */
	@Override
	public void setBoolean(String key, Boolean value) {
		try {
			data.put(key, value);
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public List getList(String key, List defaultVal) {
		return Collections.EMPTY_LIST;
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	@Override
	public List getList(String key) {
		// TODO: Implement this
		return Collections.EMPTY_LIST;
	}

	/**
	 *
	 * @param aKey
	 * @param aList
	 */
	@Override
	public void setList(String aKey, List aList) {
		try {
			data.put(aKey, aList);
		} catch (JSONException lEx) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @param aKey
	 * @param aTokenizable
	 */
	@Override
	public void setToken(String aKey, ITokenizable aTokenizable) {
	}

	/**
	 *
	 * @param aKey
	 * @param aToken
	 */
	@Override
	public void setToken(String aKey, Token aToken) {
	}

	/**
	 *
	 * @param aKey
	 * @return
	 */
	@Override
	public Token getToken(String aKey) {
		return null;
	}

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	@Override
	public Token getToken(String aKey, Token aDefault) {
		return null;
	}

	/**
	 *
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	@Override
	public Map getMap(String aKey, Map aDefault) {
		// TODO: Implement this
		return null;
	}

	/**
	 *
	 * @param aKey
	 * @return
	 */
	@Override
	public Map getMap(String aKey) {
		Map lMap = new FastMap<String, Object>();
		Iterator<String> lIterator = data.keys();
		while (lIterator.hasNext()) {
			String lKey = (String) lIterator.next();
			try {
				Object lVal = data.get(lKey);
				lMap.put(lKey, lVal);
			} catch (Exception lEx) {
				// TODO: process exception
			}
		}
		return lMap;
	}

	/**
	 *
	 * @param aKey
	 * @param aList
	 */
	@Override
	public void setMap(String aKey, Map aMap) {
		try {
			data.put(aKey, aMap);
		} catch (JSONException ex) {
			// TODO: handle exception
		}
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return data.toString();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Iterator<String> getKeyIterator() {
		return data.keys();
	}

	/**
	 *
	 * @param aKey
	 * @return
	 */
	@Override
	public Object getObject(String aKey) {
		Object lObj = null;
		try {
			lObj = data.get(aKey);
		} catch (Exception lEx) {
			// 
		}
		return lObj;
	}
}
