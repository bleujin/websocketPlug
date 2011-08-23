package net.ion.websocket.filter;

import net.ion.framework.util.Debug;
import net.ion.websocket.TestBaseWebSocket;
import net.ion.websocket.client.SyncMockClient;
import net.ion.websocket.common.api.WebSocketConnector;
import net.ion.websocket.common.filter.BaseFilter;
import net.ion.websocket.common.kit.CloseReason;
import net.ion.websocket.common.kit.FilterResponse;
import net.ion.websocket.common.plugin.CounterPlugIn;
import net.ion.websocket.plugin.MessagePacket;

public class TestFilter extends TestBaseWebSocket {

	public void testrFilterSize() throws Exception {
		CountFilter filter = new CountFilter();
		server.getFilterChain().addFilter(filter);

		assertEquals(1, server.getFilterChain().getFilters().size());
	}

	public void testCountFilter() throws Exception {
		CountFilter filter = new CountFilter();
		server.getFilterChain().addFilter(filter);
		
		SyncMockClient mock = SyncMockClient.newTest();
		mock.connect(uri);

		mock.sendMessage(MessagePacket.PING);
		mock.await();
		assertEquals(2, filter.getCount());
	}
	
	public void testRevoke() throws Exception {
		RevokeFilter filter = new RevokeFilter();
		server.getFilterChain().addFilter(filter);
		
		SyncMockClient mock = SyncMockClient.newTest();
		assertEquals(1, server.getFilterChain().getFilters().size()) ; 
		
		mock.connect(uri);
		mock.sendMessage(MessagePacket.PING);
		assertEquals(0, server.getAllConnectors().length );
	}

	
	public void testRejectPlugin() throws Exception {
		RevokeFilter filter = new RevokeFilter();
		CounterPlugIn plugin = new CounterPlugIn();
		
		server.getFilterChain().addFilter(filter);
		server.getPlugInChain().addPlugIn(plugin) ;
		
		SyncMockClient mock = SyncMockClient.newTest();
		mock.connect(uri);
		
		assertEquals(0, plugin.getProcessCounter()) ;
	}
}

class RevokeFilter extends BaseFilter {

	public RevokeFilter() {
		super(RevokeFilter.class.getCanonicalName());
	}
	@Override
	public void processPacketIn(FilterResponse response, WebSocketConnector connector) {
		connector.stopConnector(CloseReason.SERVER) ;
		response.rejectMessage("revoked.") ;
	}

	@Override
	public void processPacketOut(FilterResponse response, WebSocketConnector source) {
		Debug.debug("out..", response, source);
	}
}

class CountFilter extends BaseFilter {

	int count = 0;

	public CountFilter() {
		super(CountFilter.class.getCanonicalName());
	}

	@Override
	public void processPacketIn(FilterResponse response, WebSocketConnector connector) {
		Debug.debug("in..", response, connector);
		count++;
	}

	@Override
	public void processPacketOut(FilterResponse response, WebSocketConnector source) {
		Debug.debug("out..", response, source);
		count++;
	}

	public int getCount() {
		return count;
	}
}