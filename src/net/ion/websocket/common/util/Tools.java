//	---------------------------------------------------------------------------
//	jWebSocket - WebSocket Tools
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
package net.ion.websocket.common.util;

import java.security.MessageDigest;
import java.util.Formatter;

import net.ion.framework.util.Debug;

/**
 * Provides some convenience methods to support the web socket development.
 * 
 * @author aschulze
 */
public class Tools {

	/**
	 * Returns the MD5 sum of the given string. The output always has 32 digits.
	 * 
	 * @param message
	 *            String the string to calculate the MD5 sum for.
	 * @return MD5 sum of the given string.
	 */
	public static String getMD5(String message) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] sourceBuffer = message.getBytes("UTF-8");
			byte[] targetBuffer = md.digest(sourceBuffer);
			Formatter formatter = new Formatter();
			for (byte b : targetBuffer) {
				formatter.format("%02x", b);
			}
			return (formatter.toString());
		} catch (Exception ex) {
			// log.error("getMD5: " + ex.getMessage());
			Debug.error("getMD5: " + ex.getMessage());
		}
		return null;
	}

	/**
	 * Returns the hex value of the given int as a string. If {@code aLen} is
	 * greater than zero the output is cut or filled to the given length
	 * otherwise the exact number of digits is returned.
	 * 
	 * @param value
	 *            Integer to be converted into a hex-string.
	 * @param length
	 *            Number of hex digits (optionally filled or cut if needed)
	 * @return Hex-string of the given integer.
	 */
	public static String intToHex(int value, int length) {
		String result = Integer.toHexString(value);
		if (length > 0 && result.length() > length) {
			result = result.substring(0, length);
		} else {
			while (result.length() < length) {
				result = "0" + result.substring(0, length);
			}
		}
		return result;
	}
}
