package com.example.app.ws.exceptions;

public class UserServiceException extends RuntimeException{

	private static final long serialVersionUID = 138274197498175191L;
	
	public UserServiceException(String message) {
		super(message);
	}
}
