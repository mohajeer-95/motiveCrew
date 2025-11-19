package com.eska.motive.crew.ws.exception;

import com.eska.motive.crew.contract.StatusCode;

import lombok.Getter;

/**
 * @author A.Juhaini
 */

@Getter
public class ValidationException extends Exception{
	

	private static final long serialVersionUID = 7799855377510457678L;
	
	private StatusCode statusCode;

	public ValidationException (String message  , Throwable throwable) {
		super(message , throwable);
	}
	
	public ValidationException(String message) {
		super(message);
	}
	
	public ValidationException(StatusCode statusCode) {
		super(statusCode.getDescription());
		this.statusCode = statusCode;
	}
	
	

}
