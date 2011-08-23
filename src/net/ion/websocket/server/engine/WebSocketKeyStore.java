package net.ion.websocket.server.engine;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class WebSocketKeyStore {
    private static final short[] DATA = new short[] {};
    
    public static InputStream asInputStream() {
        byte[] data = new byte[DATA.length];
        for (int i = 0; i < data.length; i ++) {
            data[i] = (byte) DATA[i];
        }
        return new ByteArrayInputStream(data);
    }

    public static char[] getCertificatePassword() {
        return "jwebsocket".toCharArray();
    }

    public static char[] getKeyStorePassword() {
        return "jwebsocket".toCharArray();
    }

    private WebSocketKeyStore() {
        throw new AssertionError();
    }
}
