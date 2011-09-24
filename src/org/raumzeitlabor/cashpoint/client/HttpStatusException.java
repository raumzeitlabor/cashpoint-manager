package org.raumzeitlabor.cashpoint.client;

public class HttpStatusException extends Exception {
	private final int status;
	
	public HttpStatusException(int i) {
		super();
		this.status = i;
	}

	public int getStatus() {
		return status;
	}

}
