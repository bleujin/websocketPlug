package net.ion.websocket.plugin;

import java.util.Date;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.websocket.common.kit.WebSocketException;

import org.json.JSONObject;


public class TestMessagePacket extends TestCase{

	public void testInit() throws Exception {
		MessagePacket mp = MessagePacket.create().inner("head").put("requestTime", new Date().getTime()).put("requestId", "bleujin") ;
		Debug.debug(mp.toRoot().getFullString()) ;
	}
	
	public void testParent() throws Exception {
		MessagePacket mp = makeExample() ;
		
		Debug.debug(mp.getFullString()) ;
		
		// System.out.println(mp.inner("head").get("requestTime")) ;
		Debug.debug(mp.get("head/requestTime")) ;
		Debug.debug(mp.get("body/room/roomid")) ;
	}

	private MessagePacket makeExample() throws WebSocketException {
		return MessagePacket.create()
			.inner("head").put("requestTime", new Date().getTime()).put("requestId", "bleujin").toParent()
			.inner("body").put("greeting", "hello") 
				.inner("room").put("roomid", 42222).toRoot();
	}
	
	public void testLoad() throws Exception {
		MessagePacket source = makeExample();
		
		String msg = source.getFullString() ;
		
		MessagePacket mp = MessagePacket.load(msg) ;
		assertEquals(source.get("head/requestTime"), mp.get("Head/RequestTime")) ;
	}

	public void testEmpty() throws Exception {
		MessagePacket source = makeExample();
		
		assertEquals("", source.getString("not/exist")) ;
		assertEquals("not", source.getString("not/exist", "not")) ;
	}
	
	public void testHas() throws Exception {
		MessagePacket msg = makeExample() ;
		assertEquals(true, msg.has("head.requesttime")) ;
		assertEquals(true, msg.has("head.requestTime")) ;
		assertEquals(false, msg.has("content.requesttime")) ;
	}
	
	public void testGetDefaultString() throws Exception {
		MessagePacket msg = makeExample() ;
		assertEquals("bleujin", msg.getString("head.requestid", "abcd")) ;
		assertEquals("bleujin", msg.getString("head.requestID", "abcd")) ;
		assertEquals("abcd", msg.getString("head.sender", "abcd")) ;
	}
	
	public void testInner() throws Exception {
		MessagePacket mp = MessagePacket.create().inner("name").put("first", "bleu").put("last", "jin").toRoot() ;
		
		assertEquals("bleu", mp.getString("name.first")) ;
		assertEquals("jin", mp.getString("name.last")) ;
	}

	public void testAraray() throws Exception {
		MessagePacket msg = MessagePacket.create().inner("peoples") ;
		for (int i = 0; i < 3; i++) {
			JSONObject jso = new JSONObject("{result:'success', color:[1,2,3]}") ;
			jso.put("index", i) ;
			msg.toRoot().inner("peoples").append("people", jso) ;
		}
		// msg.child("peoples.people") ;
		
		Debug.debug(msg.toRoot().child("peoples.people")) ;
	}
}
