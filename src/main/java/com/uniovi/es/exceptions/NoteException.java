package com.uniovi.es.exceptions;

public class NoteException extends Exception{
	
	private static final long serialVersionUID = 4001710687990554589L;
	
	public NoteException() {}
	
	public NoteException(String message) {
		super(message);
	}

	public NoteException(Throwable cause) {
		super(cause);
	}

	public NoteException(String message, Throwable cause) {
		super(message, cause);
	}

}
