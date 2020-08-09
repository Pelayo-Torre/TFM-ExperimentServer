package com.uniovi.es.exceptions;

public class PetitionException extends Exception{
	
	private static final long serialVersionUID = 4001710687990554589L;
	
	public PetitionException() {}
	
	public PetitionException(String message) {
		super(message);
	}

	public PetitionException(Throwable cause) {
		super(cause);
	}

	public PetitionException(String message, Throwable cause) {
		super(message, cause);
	}


}
