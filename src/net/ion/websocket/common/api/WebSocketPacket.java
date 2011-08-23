//	---------------------------------------------------------------------------
//	jWebSocket - Data Packet API
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

import java.io.UnsupportedEncodingException;

/**
 * Specifies the API for low level data packets which are interchanged between
 * client and server. Data packets do not have a special format at this
 * communication level.
 * 
 * @author aschulze
 */
public interface WebSocketPacket {

	public final static WebSocketPacket BLANK = new WebSocketPacket(){


		public void setByteArray(byte[] barrays) {
		}
		public void setString(String message) {
		}
		public void setString(String message, String encoding) throws UnsupportedEncodingException {
		}
		public void setUTF8(String message) {
		}
		public void setASCII(String message) {
		}
		public byte[] getByteArray() {
			return new byte[0];
		}
		public String getString() {
			return "";
		}
		public String getString(String encoding) throws UnsupportedEncodingException {
			return "";
		}
		public String getUTF8() {
			return "";
		}
		public String getASCII() {
			return "";
		}
		public int getFrameType() {
			return 0;
		}
		public void setFrameType(int frametype) {
		}
	} ;
	/**
	 * Sets the value of the data packet to the given array of bytes.
	 * 
	 * @param barrays
	 */
	void setByteArray(byte[] barrays);

	/**
	 * Sets the value of the data packet to the given string by using default
	 * encoding.
	 * 
	 * @param message
	 */
	void setString(String message);

	/**
	 * Sets the value of the data packet to the given string by using the passed
	 * encoding.
	 * 
	 * @param message
	 * @param encoding
	 * @throws UnsupportedEncodingException
	 */
	void setString(String message, String encoding) throws UnsupportedEncodingException;

	/**
	 * Sets the value of the data packet to the given string by using UTF-8
	 * encoding.
	 * 
	 * @param message
	 */
	void setUTF8(String message);

	/**
	 * Sets the value of the data packet to the given string by using 7 bit
	 * US-ASCII encoding.
	 * 
	 * @param message
	 */
	void setASCII(String message);

	/**
	 * Returns the content of the data packet as an array of bytes.
	 * 
	 * @return Data packet as array of bytes.
	 */
	byte[] getByteArray();

	/**
	 * Returns the content of the data packet as a string using default
	 * encoding.
	 * 
	 * @return Raw Data packet as string with default encoding.
	 */
	String getString();

	/**
	 * Returns the content of the data packet as a string using the passed
	 * encoding.
	 * 
	 * @param encoding
	 * @return String using the passed encoding.
	 * @throws UnsupportedEncodingException
	 */
	String getString(String encoding) throws UnsupportedEncodingException;

	/**
	 * Interprets the data packet as a UTF8 string and returns the string in
	 * UTF-8 encoding. If an exception occurs "null" is returned.
	 * 
	 * @return Data packet as UTF-8 string or {@code null} if not convertible.
	 */
	String getUTF8();

	/**
	 * Interprets the data packet as a US-ASCII string and returns the string in
	 * US-ASCII encoding. If an exception occurs "null" is returned.
	 * 
	 * @return Data packet as US-ASCII string or {@code null} if not
	 *         convertible.
	 */
	String getASCII();

	/**
	 * 
	 * @return
	 */
	int getFrameType();

	/**
	 * 
	 * @param frametype
	 */
	void setFrameType(int frametype);
}
