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
import java.util.Date;

import net.ion.websocket.common.kit.WebSocketFrameType;

/**
 * Specifies the API for low level data packets which are interchanged between
 * client and server. Data packets do not have a special format at this
 * communication level.
 * 
 * @author aschulze
 */
public interface WebSocketPacket {
	
	public final static WebSocketPacket BLANK = new WebSocketPacket(){

		@Override
		public String getASCII() {
			return "";
		}

		@Override
		public byte[] getByteArray() {
			return new byte[0];
		}

		@Override
		public Date getCreationDate() {
			return new Date();
		}

		@Override
		public WebSocketFrameType getFrameType() {
			return WebSocketFrameType.INVALID;
		}

		@Override
		public String getString() {
			return "";
		}

		@Override
		public String getString(String enc) throws UnsupportedEncodingException {
			return "";
		}

		@Override
		public String getUTF8() {
			return "";
		}

		@Override
		public void initFragmented(int total) {
			
		}

		@Override
		public boolean isComplete() {
			return true;
		}

		@Override
		public boolean isFragmented() {
			return false;
		}

		@Override
		public boolean isTimedOut() {
			return false;
		}

		@Override
		public void packFragments() {
		}

		@Override
		public void setASCII(String str) {
		}

		@Override
		public void setByteArray(byte[] barray) {
		}

		@Override
		public void setCreationDate(Date date) {
		}

		@Override
		public void setFragment(String str, int idx) {
		}

		@Override
		public void setFrameType(WebSocketFrameType frameType) {
		}

		@Override
		public void setString(String str) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setString(String str, String enc) throws UnsupportedEncodingException {
		}

		@Override
		public void setTimeout(long milliseconds) {
		}

		@Override
		public void setUTF8(String str) {
		}
	} ;

	/**
	 * Sets the value of the data packet to the given array of bytes.
	 * @param aByteArray
	 */
	void setByteArray(byte[] aByteArray);

	/**
	 * 
	 * @return
	 */
	boolean isFragmented();

	/**
	 * 
	 * @return
	 */
	boolean isComplete();

	/**
	 *
	 * @param aString
	 * @param aStart
	 */
	void setFragment(String aString, int aIdx);

	/**
	 *
	 */
	void packFragments();

	/**
	 * 
	 * @param date
	 */
	void setCreationDate(Date date);

	/**
	 * 
	 * @return
	 */
	Date getCreationDate();

	/**
	 * 
	 * @param milliseconds
	 */
	void setTimeout(long milliseconds);

	/**
	 *
	 * @return
	 */
	boolean isTimedOut();

	/**
	 *
	 * @param total
	 */
	void initFragmented(int total);

	/**
	 * Sets the value of the data packet to the given string by using
	 * default encoding.
	 * @param string
	 */
	void setString(String string);

	/**
	 * Sets the value of the data packet to the given string by using
	 * the passed encoding.
	 * @param string
	 * @param encoding
	 * @throws UnsupportedEncodingException
	 */
	void setString(String string, String encoding) throws UnsupportedEncodingException;

	/**
	 * Sets the value of the data packet to the given string by using
	 * UTF-8 encoding.
	 * @param string
	 */
	void setUTF8(String string);

	/**
	 * Sets the value of the data packet to the given string by using
	 * 7 bit US-ASCII encoding.
	 * @param string
	 */
	void setASCII(String string);

	/**
	 * Returns the content of the data packet as an array of bytes.
	 * @return Data packet as array of bytes.
	 */
	byte[] getByteArray();

	/**
	 * Returns the content of the data packet as a string using default
	 * encoding.
	 * @return Raw Data packet as string with default encoding.
	 */
	String getString();

	/**
	 * Returns the content of the data packet as a string using the passed
	 * encoding.
	 * @param encoding
	 * @return String using the passed encoding.
	 * @throws UnsupportedEncodingException
	 */
	String getString(String encoding) throws UnsupportedEncodingException;

	/**
	 * Interprets the data packet as a UTF8 string and returns the string
	 * in UTF-8 encoding. If an exception occurs "null" is returned.
	 * @return Data packet as UTF-8 string or {@code null} if not convertible.
	 */
	String getUTF8();

	/**
	 * Interprets the data packet as a US-ASCII string and returns the string
	 * in US-ASCII encoding. If an exception occurs "null" is returned.
	 * @return Data packet as US-ASCII string or {@code null} if not convertible.
	 */
	String getASCII();

	/**
	 *
	 * @return
	 */
	WebSocketFrameType getFrameType();

	/**
	 *
	 * @param frameType
	 */
	void setFrameType(WebSocketFrameType frameType);
}
