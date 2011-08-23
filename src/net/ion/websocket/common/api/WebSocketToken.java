//	---------------------------------------------------------------------------
//	jWebSocket - Token Interface
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
package net.ion.websocket.common.api;

import java.util.List;
import java.util.Map;

/**
 *
 * @author aschulze
 */
public interface WebSocketToken {


	/**
	 *
	 * @param key
	 * @param dftValue
	 * @return
	 */
	String getString(String key, String dftValue);

	/**
	 *
	 * @param key
	 * @return
	 */
	String getString(String key);

	/**
	 *
	 * @param key
	 * @param value 
	 */
	void setString(String key, String value);

	/**
	 *
	 * @param key
	 * @param dftValue
	 * @return
	 */
	Integer getInteger(String key, Integer dftValue);

	/**
	 *
	 * @param key
	 * @return
	 */
	Integer getInteger(String key);

	/**
	 *
	 * @param key
	 * @param value
	 */
	void setInteger(String key, Integer value);

	/**
	 *
	 * @param key
	 * @param dftValue
	 * @return
	 */
	Boolean getBoolean(String key, Boolean dftValue);

	/**
	 *
	 * @param key
	 * @return
	 */
	Boolean getBoolean(String key);

	/**
	 *
	 * @param key
	 * @param dftValue
	 */
	void setBoolean(String key, Boolean dftValue);

	/**
	 *
	 * @param key
	 * @param dftValue
	 * @return
	 */
	List getList(String key, Boolean dftValue);

	/**
	 *
	 * @param key
	 * @return
	 */
	List getList(String key);

	// TODO: Add list access methods

	/**
	 *
	 * @param key
	 * @param dftValue
	 * @return
	 */
	Map getMap(String key, Boolean dftValue);

	/**
	 *
	 * @param key
	 * @return
	 */
	Map getMap(String key);

	// TODO: Add map access methods

	// TODO: Add date/time fields


	/**
	 *
	 * @param key
	 * @return
	 */
	void remove(String key);

}
