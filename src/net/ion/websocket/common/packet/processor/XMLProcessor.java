//	---------------------------------------------------------------------------
//	jWebSocket - XML Token Processor
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
package net.ion.websocket.common.packet.processor;

import java.util.Collection;

import net.ion.websocket.common.api.WebSocketPacket;
import net.ion.websocket.common.token.Token;
import net.ion.websocket.common.token.TokenFactory;

/**
 * converts XML formatted data packets into tokens and vice versa.
 * @author aschulze
 */
public class XMLProcessor {

    // TODO: Logging cannot be used in common module because not supported on all clients
    // private static Logger log = Logging.getLogger(XMLProcessor.class);
    /**
     * converts a XML formatted data packet into a token.
     * @param aDataPacket
     * @return
     */
    public static Token packetToToken(WebSocketPacket aDataPacket) {
        // todo: implement!
        Token lArgs = TokenFactory.createToken();
        return lArgs;
    }

    private static String stringToXML(String string) {
        // todo: implement!
        String lRes = null;
        return lRes;
    }

    private static String listToXML(Collection<Object> collection) {
        // todo: implement!
        String lRes = null;
        return lRes;
    }

    private static String objectToXML(Object obj) {
        // todo: implement!
        String lRes = null;
        return lRes;
    }

    /**
     * converts a token into a XML formatted data packet.
     * @param token
     * @return
     */
    public static WebSocketPacket tokenToPacket(Token token) {
        // todo: implement!
        return null;
    }
}
