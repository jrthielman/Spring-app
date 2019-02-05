package com.example.app.ws.exceptions;

public class UserAlreadyExistsException extends RuntimeException{

	private static final long serialVersionUID = 104840128501501230L;
	
	public UserAlreadyExistsException(String message) {
		super(message);
	}

}
