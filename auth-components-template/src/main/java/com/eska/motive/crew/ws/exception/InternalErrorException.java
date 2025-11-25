package com.eska.motive.crew.ws.exception;

import lombok.Getter;

/**
 * Exception thrown when an internal server error occurs
 * 
 * @author Your Name
 */
@Getter
public class InternalErrorException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private String errorCode;

    public InternalErrorException(String message) {
        super(message);
        this.errorCode = "INTERNAL_ERROR";
    }
    
    public InternalErrorException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}


