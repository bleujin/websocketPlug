package net.ion.websocket.common.kit;

public class BroadcastOptions {

	private boolean senderIncluded = false;
	private boolean responseRequested = false;
    private boolean mAsync = false;

	private static BroadcastOptions EXCLUDE_SELF = new BroadcastOptions(false, true) ;
	private static BroadcastOptions DEFAULT = new BroadcastOptions(true, true) ;
	
	private BroadcastOptions(boolean senderIncluded, boolean responseRequested) {
		this.senderIncluded = senderIncluded;
		this.responseRequested = responseRequested;
	}

	public static BroadcastOptions excludeSelfOption() {
		return EXCLUDE_SELF;
	}

	public static BroadcastOptions defaultOption() {
		return DEFAULT;
	}


	public boolean isSenderIncluded() {
		return senderIncluded;
	}

	public void setSenderIncluded(boolean senderIncluded) {
		this.senderIncluded = senderIncluded;
	}

	public boolean isResponseRequested() {
		return responseRequested;
	}

	public void setResponseRequested(boolean responseRequested) {
		this.responseRequested = responseRequested;
	}
    /**
     * @return the async
     */
    public boolean isAsync() {
        return mAsync;
    }

    /**
     * @param aAsync the async to set
     */
    public void setAsync(boolean aAsync) {
        mAsync = aAsync;
    }

}
