package com.uniovi.es.exceptions;

public class InvestigatorException extends Exception{
	
	private static final long serialVersionUID = 4001710687990554589L;
	
	public InvestigatorException() {}
	
	public InvestigatorException(String message) {
		super(message);
	}

	public InvestigatorException(Throwable cause) {
		super(cause);
	}

	public InvestigatorException(String message, Throwable cause) {
		super(message, cause);
	}

}
