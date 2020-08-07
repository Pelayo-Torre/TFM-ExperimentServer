package com.uniovi.es.exceptions;

public class ExperimentException extends Exception{
	
	private static final long serialVersionUID = 4001710687990554589L;
	
	public ExperimentException() {}
	
	public ExperimentException(String code) {
		super(code);
	}

	public ExperimentException(Throwable cause) {
		super(cause);
	}

	public ExperimentException(String code, Throwable cause) {
		super(code, cause);
	}

	
	

}
