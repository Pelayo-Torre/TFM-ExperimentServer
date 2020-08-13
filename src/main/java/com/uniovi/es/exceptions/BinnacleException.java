package com.uniovi.es.exceptions;

public class BinnacleException extends Exception{
	
	private static final long serialVersionUID = 4001710687990554589L;
	
	public BinnacleException() {}
	
	public BinnacleException(String message) {
		super(message);
	}

	public BinnacleException(Throwable cause) {
		super(cause);
	}

	public BinnacleException(String message, Throwable cause) {
		super(message, cause);
	}

}
