package com.uniovi.es.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends Exception{

	private static final long serialVersionUID = 4001710687990554589L;
	
	public ForbiddenException() {}
	
	public ForbiddenException(String message) {
		super(message);
	}

	public ForbiddenException(Throwable cause) {
		super(cause);
	}

	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
