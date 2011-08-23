package net.ion.websocket.common.api;

public interface Selector {

	boolean isTrueCondition(WebSocketConnector conn);

}
