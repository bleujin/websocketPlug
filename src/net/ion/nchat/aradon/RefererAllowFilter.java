package net.ion.nchat.aradon;

import net.ion.framework.util.StringUtil;
import net.ion.radon.core.IService;
import net.ion.radon.core.filter.IFilterResult;
import net.ion.radon.core.filter.IRadonFilter;

import org.apache.commons.lang.ArrayUtils;
import org.restlet.Request;
import org.restlet.Response;

public class RefererAllowFilter extends IRadonFilter{

	private String[] address ;
	
	public RefererAllowFilter() {
		this("localhost, 127.0.0.1");
	}

	public RefererAllowFilter(String addresss) {
		this.address = StringUtil.split(addresss, ", ") ;
	}

	public static IRadonFilter test() {
		return new RefererAllowFilter("localhost, 127.0.0.1");
	}

	@Override
	public IFilterResult afterHandle(IService iservice, Request request, Response response) {
		return IFilterResult.CONTINUE_RESULT;
	}

	@Override
	public IFilterResult preHandle(IService iservice, Request request, Response response) {
		String clientAddress = request.getClientInfo().getAddress() ;
		return (clientAddress == null || ArrayUtils.contains(address, clientAddress)) ? IFilterResult.CONTINUE_RESULT : IFilterResult.STOP_RESULT ;
	}

}
