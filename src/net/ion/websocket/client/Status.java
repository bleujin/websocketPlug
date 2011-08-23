package net.ion.websocket.client;

public enum Status {
	INIT(-1), CONNECTING(0), OPEN(1), CLOSING(2), CLOSED(3);
	private int status;

	Status(int theStatus) {
		this.status = theStatus;
	}

	public int getStatus() {
		return status;
	}

	public boolean isConnected() {
		return getStatus() == 1;
	}
}