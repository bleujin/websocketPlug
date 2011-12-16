// ---------------------------------------------------------------------------
// jWebSocket - JSON Token Processor
// Copyright (c) 2010 jWebSocket.org, Alexander Schulze, Innotrade GmbH
// ---------------------------------------------------------------------------
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by the
// Free Software Foundation; either version 3 of the License, or (at your
// option) any later version.
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
// more details.
// You should have received a copy of the GNU Lesser General Public License along
// with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
// ---------------------------------------------------------------------------
package net.ion.websocket.common.packet.processor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.kit.RawPacket;
import net.ion.websocket.common.token.MapToken;
import net.ion.websocket.common.token.Token;

/**
 * converts JSON formatted data packets into tokens and vice versa.
 * 
 * @author Alexander Schulze, Roderick Baier (improvements regarding JSON
 *         array), Quentin Ambard (add support for Map and List for
 *         PacketToToken and tokeToPacket).
 */
@SuppressWarnings("rawtypes")
public class JSONProcessor {

	/**
	 * @param aObject
	 * @return an object which is the JSON representation of aObject
	 */
	@SuppressWarnings("unchecked")
	private static Object convertJsonToJavaObject(Object aObject) {
		if (aObject instanceof JSONArray) {
			return jsonArrayToList((JSONArray) aObject);
		} else if (aObject instanceof JSONObject) {
			return jsonObjectToMap((JSONObject) aObject);
		} else {
			return aObject;
		}
	}

	/**
	 * Quentin: Recursivly convert a JSONArray to a List of List, Map or Object.
	 * (also convert all the JSONObject to Map) Example: jsonArrayToList(
	 * JSONArray [[1,2,3],{"value": "a"}]) will retour a List{{1,2,3},{"value" =>
	 * "b"}}
	 * 
	 * @param jsonArray
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public static List jsonArrayToList(JSONArray jsonArray) {
		List lList = new FastList();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				Object lSubObject = jsonArray.get(i);
				lList.add(convertJsonToJavaObject(lSubObject));
			} catch (JSONException e) {
				// Sould never happen: aJsonArray.get(i)
				// will always exists
			}
		}
		return lList;
	}

	/**
	 * Quentin: Recursivly convert a JSONObject to a Map of List, Map or Object.
	 * (also convert all the JSONArray to List)
	 * 
	 * @param aJsonArray
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public static Map jsonObjectToMap(JSONObject jsonObject) {
		FastMap lFastMap = new FastMap();
		Iterator iterator = jsonObject.keys();
		while (iterator.hasNext()) {
			try {
				Object aKey = iterator.next();
				Object aValue = convertJsonToJavaObject(jsonObject.get(String.valueOf(aKey)));
				lFastMap.put(aKey, aValue);
			} catch (JSONException e) {
				// Sould never happen:
				// aJsonObject.get(String.valueOf(aKey))
				// will always exists
			}
		}
		return lFastMap;
	}

	/**
	 * Convert a json string to a token. If the json string isn't a valid one, 
	 * return an empty token.
	 * Note that if you need a more generic conversion (other sub protocol than 
	 * json), you may also use the following:
	 * Token lToken = TokenServer.packetToToken(aConnector, new RawPacket(aJsonString))
	 * Depending of the SubProtocol of aConnector, the token will be automatically created 
	 * (if the SubProtocol is WS_SUBPROT_JSON, the conversion will be done internally using this method)
	 * @param jsonString a json string
	 * @return the token corresponding to the json string, or an empty token
	 */
	public static Token jsonStringToToken(String jsonString) {
		Token lToken = new MapToken();
		try {
			String lStr = jsonString;
			JSONTokener lJT = new JSONTokener(lStr);
			JSONObject lJO = new JSONObject(lJT);
			for (Iterator lIterator = lJO.keys(); lIterator.hasNext();) {
				String lKey = (String) lIterator.next();
				Object lValue = lJO.get(lKey);
				lToken.setValidated(lKey, convertJsonToJavaObject(lValue));
			}
		} catch (Exception ex) {
			// TODO: process exception
			// log.error(ex.getClass().getSimpleName() + ": " +
			// ex.getMessage());
		}
		return lToken;
	}

	/**
	 * converts a JSON formatted data packet into a token.
	 * 
	 * @param dataPacket
	 * @return
	 */
	public static Token packetToToken(WebSocketPacket dataPacket) {
		Token lToken = new MapToken();
		try {
			String lStr = dataPacket.getString("UTF-8");
			return jsonStringToToken(lStr);
		} catch (Exception ex) {
			// // TODO: process exception
			// log.error(ex.getClass().getSimpleName() + ": " +
			// ex.getMessage());
		}
		return lToken;
	}

	public static WebSocketPacket tokenToPacket(Token token) {
		WebSocketPacket packet = null;
		try {
			JSONObject lJO = tokenToJSON(token);
			String data = lJO.toString();
			packet = new RawPacket(data, "UTF-8");
		} catch (Exception ex) {
			// TODO: process exception
			// log.error(ex.getClass().getSimpleName() + ": " +
			// ex.getMessage());
		}

		return packet;
	}

	/** 
	 * transform a list to a JSONArray
	 * @param list
	 * @return a JSONArray which represents aList
	 * @throws JSONException
	 */
	public static JSONArray listToJsonArray(List list) throws JSONException {
		JSONArray lArray = new JSONArray();
		for (Object item : list) {
			lArray.put(convertObjectToJson(item));
		}
		return lArray;
	}

	/**
	 * transform a list of objects to a JSONArray
	 * @param objectList
	 * @return a JSONArray which represents aObjectList
	 * @throws JSONException
	 */
	public static JSONArray objectListToJsonArray(Object[] objectList)
			throws JSONException {
		JSONArray lArray = new JSONArray();
		for (int lIdx = 0; lIdx < objectList.length; lIdx++) {
			Object lObj = objectList[lIdx];
			lArray.put(convertObjectToJson(lObj));
		}
		return lArray;
	}

	/**
	 * transform a map to a JSONObject
	 * @param map
	 * @return a JSONObject which represents aMap. All the keys values are passed
	 *         as String using the toString method of the key.
	 * @throws JSONException
	 */
	public static JSONObject mapToJsonObject(Map<?, ?> map)
			throws JSONException {
		JSONObject lObject = new JSONObject();
		for (Entry<?, ?> lEntry : map.entrySet()) {
			String lKey = lEntry.getKey().toString();
			Object lValue = convertObjectToJson(lEntry.getValue());
			lObject.put(lKey, lValue);
		}
		return lObject;
	}

	/**
	 * transform an object to another JSON object (match all possibilities)
	 * @param obj
	 * @return an Object which represents aObject (looks for List, Token and Maps)
	 * @throws JSONException
	 */
	public static Object convertObjectToJson(Object obj)
			throws JSONException {
		if (obj instanceof List) {
			return listToJsonArray((List) obj);
		} else if (obj instanceof Token) {
			return tokenToJSON((Token) obj);
		} else if (obj instanceof Object[]) {
			return objectListToJsonArray((Object[]) obj);
		} else if (obj instanceof Map) {
			return mapToJsonObject((Map<?, ?>) obj);
		} else {
			return obj;
		}
	}

	/**
	 * transform a token to a json object
	 * @param token
	 * @return a JSONObject which represents aToken (looks for List, Token and
	 *         Maps)
	 * @throws JSONException
	 */
	public static JSONObject tokenToJSON(Token token) throws JSONException {
		JSONObject lJSO = new JSONObject();
		Iterator<String> iterator = token.getKeyIterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = token.getObject(key);
			lJSO.put(key, convertObjectToJson(value));
		}
		return lJSO;
	}
}
