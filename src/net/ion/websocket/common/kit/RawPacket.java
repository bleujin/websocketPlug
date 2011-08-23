//	---------------------------------------------------------------------------
//	jWebSocket - Raw Data Packet Implementation
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
package net.ion.websocket.common.kit;

import java.io.UnsupportedEncodingException;

import net.ion.websocket.common.api.WebSocketPacket;

/**
 * Implements the low level data packets which are interchanged between client and server. Data packets do not have a special format at this communication level.
 * 
 * @author aschulze
 */
public class RawPacket implements WebSocketPacket {

	public static final int FRAMETYPE_UTF8 = 0;
	public static final int FRAMETYPE_BINARY = 1;

	private byte[] data = null;
	private int frameType = FRAMETYPE_UTF8;

	/**
	 * Instantiates a new data packet and initializes its value to the passed array of bytes.
	 * 
	 * @param barray
	 *            byte array to be used as value for the data packet.
	 */
	public RawPacket(byte[] barray) {
		setByteArray(barray);
	}

	/**
	 * Instantiates a new data packet and initializes its value to the passed string using the default encoding.
	 * 
	 * @param message
	 *            string to be used as value for the data packet.
	 * @throws UnsupportedEncodingException
	 */
	public RawPacket(String message) throws UnsupportedEncodingException {
		setString(message, "UTF-8");
	}

	/**
	 * Instantiates a new data packet and initializes its value to the passed string using the passed encoding (should always be "UTF-8").
	 * 
	 * @param message
	 *            string to be used as value for the data packet.
	 * @param encoding
	 *            should always be "UTF-8"
	 * @throws UnsupportedEncodingException
	 */
	public RawPacket(String message, String encoding) throws UnsupportedEncodingException {
		setString(message, encoding);
	}

	public void setByteArray(byte[] barray) {
		data = barray;
	}

	public void setString(String message) {
		data = message.getBytes();
	}

	public void setString(String message, String encoding) throws UnsupportedEncodingException {
		data = message.getBytes(encoding);
	}

	public void setUTF8(String message) {
		try {
			data = message.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			// ignore exception here
		}
	}

	public void setASCII(String message) {
		try {
			data = message.getBytes("US-ASCII");
		} catch (UnsupportedEncodingException ex) {
			// ignore exception here
		}
	}

	public byte[] getByteArray() {
		return data;
	}

	public String getString() {
		return new String(data);
	}

	public String getString(String encoding) throws UnsupportedEncodingException {
		return new String(data, encoding);
	}

	public String getUTF8() {
		try {
			return new String(data, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	public String getASCII() {
		try {
			return new String(data, "US-ASCII");
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	/**
	 * @return the frameType
	 */
	public int getFrameType() {
		return frameType;
	}

	/**
	 * @param frameType
	 *            the frameType to set
	 */
	public void setFrameType(int frameType) {
		this.frameType = frameType;
	}
}
