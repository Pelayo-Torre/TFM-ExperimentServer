package com.uniovi.es.exceptions;

public class AttempsException extends Exception {

	private static final long serialVersionUID = 4001710687990554589L;

	public AttempsException() {}

	public AttempsException(String message) {
		super(message);
	}

	public AttempsException(Throwable cause) {
		super(cause);
	}

	public AttempsException(String message, Throwable cause) {
		super(message, cause);
	}
}
