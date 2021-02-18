package com.uniovi.es.exceptions;

public class AdministrationException extends Exception{
	
	private static final long serialVersionUID = 4001710687990554589L;
	
	public AdministrationException() {}
	
	public AdministrationException(String message) {
		super(message);
	}

	public AdministrationException(Throwable cause) {
		super(cause);
	}

	public AdministrationException(String message, Throwable cause) {
		super(message, cause);
	}

}
