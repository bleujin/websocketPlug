/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ion.websocket.common.kit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import javolution.util.FastMap;

/**
 *
 * @author alexanderschulze
 */
public class Headers {

	public static final String HOST = "Host";
	public static final String UPGRADE = "Upgrade";
	public static final String CONNECTION = "Connection";
	
	public static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";
	public static final String SEC_WEBSOCKET_ORIGIN = "Sec-WebSocket-Origin";
	public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";
	public static final String SEC_WEBSOCKET_VERSION = "Sec-WebSocket-Version";
	
	public static final String SEC_WEBSOCKET_ACCEPT = "Sec-WebSocket-Accept";
	
	private Map<String, String> mFields = new FastMap<String, String>();
	private String mFirstLine = null;
	private byte[] mTrailingBytes = null;

	/**
	 * 
	 * @param version
	 * @param input
	 * @throws WebSocketException
	 */
	public void readFromStream(int version, InputStream input) throws WebSocketException {
		// the header is complete when the first empty line is detected
		boolean completedHeader = false;

		// signal if we are still within the header
		boolean lInHeader = true;
		int lLineNo = 0;
		ByteArrayOutputStream bufferOut = new ByteArrayOutputStream(512);
		ByteArrayOutputStream trailOut = new ByteArrayOutputStream(16);

		byte[] serverResponse = new byte[16];

		int lA, lB = -1;
		while (!completedHeader) {
			lA = lB;
			try {
				lB = input.read();
			} catch (IOException ex) {
				throw new WebSocketException("Error on reading stream: " + ex.getMessage());
			}
			bufferOut.write(lB);
			if (!lInHeader) {
				trailOut.write(lB);
				if (trailOut.size() == 16) {
					completedHeader = true;
				}
			} else if (0x0D == lA && 0x0A == lB) {
				String lineStr;
				try {
					lineStr = bufferOut.toString("UTF-8");
				} catch (UnsupportedEncodingException ex) {
					throw new WebSocketException("Error on on converting string: " + ex.getMessage());
				}
				// if the line is empty, the header is complete
				if (lineStr.trim().equals("")) {
					lInHeader = false;
					completedHeader = !WebSocketProtocolAbstraction.isHixieVersion(version);
				} else {
					if (0 == lLineNo) {
						mFirstLine = lineStr;
					} else {
						String[] lKeyVal = lineStr.split(":", 2);
						if (2 == lKeyVal.length) {
							mFields.put(lKeyVal[0].trim(), lKeyVal[1].trim());
						}
					}
					lLineNo++;

				}
				bufferOut.reset();
			}
		}
	}

	/**
	 * @return the mFields
	 */
	public Map<String, String> getFields() {
		return mFields;
	}

	/**
	 * 
	 * @param field
	 * @return
	 */
	public String getField(String field) {
		if (null != mFields) {
			return mFields.get(field);
		}
		return null;
	}

	/**
	 * @return the mFirstLine
	 */
	public String getFirstLine() {
		return mFirstLine;
	}

	/**
	 * @return the mTrailingBytes
	 */
	public byte[] getTrailingBytes() {
		return mTrailingBytes;
	}
}
