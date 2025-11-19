package com.eska.motive.crew.ws.exception;

import com.eska.motive.crew.contract.StatusCode;

import lombok.Getter;

/**
 * @author A.Juhaini
 */

@Getter
public class ResourceNotFoundException extends Exception {

	private static final long serialVersionUID = -6806458320186959974L;	
	private StatusCode statusCode;

	public ResourceNotFoundException (String message , StatusCode statusCode) {
		super(message);
		this.statusCode = statusCode;
	}
	
	public ResourceNotFoundException(StatusCode statusCode) {
		super(statusCode.getDescription());
		this.statusCode = statusCode;
	}
		

}
