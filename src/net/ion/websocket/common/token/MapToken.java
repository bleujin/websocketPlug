//	---------------------------------------------------------------------------
//	jWebSocket - Map Token Implementation
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

import javolution.util.FastList;
import javolution.util.FastMap;
import net.ion.framework.util.ObjectUtil;

/**
 * A token is ...
 * 
 * @author aschulze
 */
public class MapToken extends BaseToken implements Token {

	private Map data = null;

	/**
	 * Creates a new empty instance of a token. The token does not contain any items.
	 */
	public MapToken() {
		data = new FastMap();
	}

	/**
	 * 
	 * @param aType
	 */
	public MapToken(String aType) {
		data = new FastMap();
		setType(aType);
	}

	/**
	 * 
	 * @param values
	 */
	public MapToken(FastMap values) {
		data = values;
	}

	/**
	 * 
	 * @param ns
	 * @param type
	 */
	public MapToken(String ns, String type) {
		data = new FastMap();
		setNS(ns);
		setType(type);
	}

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public void set(ITokenizable tokenizable) {
		tokenizable.writeToToken(this);
	}

	/**
	 * 
	 * @param aMap
	 */
	@Override
	public void setMap(Map aMap) {
		data = aMap;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	@Override
	public Map getMap() {
		return data;
	}

	private Object getValue(Object value) {
		if (value instanceof MapToken) {
			value = ((MapToken) value).getMap();
		} else if (value instanceof Collection) {
			List lList = new FastList();
			for (Object lItem : (Collection) value) {
				lList.add(getValue(lItem));
			}
			value = lList;
		} else if (value instanceof Map) {
			Map map = new FastMap();
			for (Entry<Object, Object> lItem : ((Map<Object, Object>) value).entrySet()) {
				map.put(lItem.getKey().toString(), getValue(lItem.getValue()));
			}
			value = map;
		} else if (value instanceof Object[]) {
			List list = new FastList();
			Object[] lOA = (Object[]) value;
			for (int lIdx = 0; lIdx < lOA.length; lIdx++) {
				list.add(getValue(lOA[lIdx]));
			}
			value = list;
		}
		return value;
	}

	/**
	 * puts a new key/value pair into the token, in other words it adds a new item to the token.
	 * 
	 * @param key
	 *            key of the the token item.
	 * @param value
	 *            value of the token item.
	 */
	private void put(String key, Object value) {
		data.put(key, getValue(value));
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private Object get(String key) {
		return data.get(key);
	}

	/**
	 * 
	 * @param key
	 */
	@Override
	public void remove(String key) {
		data.remove(key);
	}

	/**
	 * 
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public String getString(String key, String defaultVal) {
		return ObjectUtil.coalesce((String)data.get(key), defaultVal) ;
	}

	/**
	 * 
	 * @param key
	 */
	@Override
	public void setString(String key, String value) {
		try {
			data.put(key, value);
		} catch (Exception lEx) {
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
		return ObjectUtil.coalesce((Integer) data.get(key), defaultVal) ;
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
	public void setInteger(String key, Integer value) {
		data.put(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public Double getDouble(String key, Double defaultVal) {
		Double result;
		try {
			Object value = data.get(key);
			if (value instanceof String) {
				result = Double.parseDouble((String) value);
			} else if (value instanceof Integer) {
				result = (Integer) value / 1.0;
			} else {
				result = (Double) value;
			}
			if (result == null) {
				result = defaultVal;
			}
		} catch (Exception ex) {
			result = defaultVal;
		}
		return result;
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
	public void setDouble(String key, Double value) {
		data.put(key, value);
	}

	/**
	 * 
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public Boolean getBoolean(String key, Boolean defaultVal) {
		return ObjectUtil.coalesce((Boolean) data.get(key), defaultVal) ;
	}

	/**
	 * 
	 * @param arg
	 * @return
	 */
	@Override
	public Boolean getBoolean(String arg) {
		return getBoolean(arg, null);
	}

	/**
	 * 
	 * @param key
	 */
	@Override
	public void setBoolean(String key, Boolean value) {
		data.put(key, value);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public List getList(String key) {
		return getList(key, null);
	}

	/**
	 * 
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public List getList(String key, List defaultVal) {
		return ObjectUtil.coalesce((List) data.get(key), defaultVal);
	}

	/**
	 * 
	 * @param key
	 * @param list
	 */
	@Override
	public void setList(String key, List list) {
		data.put(key, list);
	}

	/**
	 * 
	 * @param key
	 * @param tokenizable
	 */
	@Override
	public void setToken(String key, ITokenizable tokenizable) {
		Token token = TokenFactory.createToken();
		tokenizable.writeToToken(token);
		setToken(key, token);
	}

	/**
	 * 
	 * @param key
	 * @param token
	 */
	@Override
	public void setToken(String key, Token token) {
		data.put(key, token);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Token getToken(String key) {
		return getToken(key, null);
	}

	/**
	 * 
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public Token getToken(String key, Token defaultVal) {
		return ObjectUtil.coalesce((Token) data.get(key), defaultVal);
	}

	/**
	 * 
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	@Override
	public Map getMap(String key, Map defaultVal) {
		return ObjectUtil.coalesce((Map) data.get(key), defaultVal);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Map getMap(String key) {
		return getMap(key, null);
	}

	/**
	 * 
	 * @param key
	 */
	@Override
	public void setMap(String key, Map values) {
		data.put(key, values);
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
		return data.keySet().iterator();
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Object getObject(String key) {
		return data.get(key);
	}
}
