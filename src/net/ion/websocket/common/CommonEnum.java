package net.ion.websocket.common;

public class CommonEnum {

	
	public enum SubProtocol {
		MESSENGER, CHAT, UNKNOWN ;

		public static SubProtocol from(String name) {
			if (name == null || "json".equals(name)){
				return MESSENGER ;
			}
			return SubProtocol.valueOf(name == null ? "MESSENGER" : name.toUpperCase() ); 
		}

	}
	
}
