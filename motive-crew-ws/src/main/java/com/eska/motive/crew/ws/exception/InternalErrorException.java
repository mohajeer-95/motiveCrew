package com.eska.motive.crew.ws.exception;

import com.eska.motive.crew.contract.StatusCode;

import lombok.Getter;

/**
 * @author A.Juhaini
 */

@Getter
public class InternalErrorException extends Exception{

	private static final long serialVersionUID = 7799855377510457678L;
	
	private StatusCode statusCode;

	public InternalErrorException (String message  , Throwable throwable) {
		super(message , throwable);
	}
	
	public InternalErrorException(String message) {
		super(message);
	}
	
	public InternalErrorException(StatusCode statusCode) {
		super(statusCode.getDescription());
		this.statusCode = statusCode;
	}
}
