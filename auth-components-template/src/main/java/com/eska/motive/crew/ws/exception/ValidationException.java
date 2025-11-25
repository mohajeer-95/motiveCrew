package com.eska.motive.crew.ws.exception;

import lombok.Getter;

/**
 * Exception thrown when validation fails
 * 
 * @author Your Name
 */
@Getter
public class ValidationException extends Exception {

    private static final long serialVersionUID = 7799855377510457678L;
    
    private String errorCode;

    public ValidationException(String message) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
    }
    
    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ValidationException(String message, Throwable throwable) {
        super(message, throwable);
        this.errorCode = "VALIDATION_ERROR";
    }
}


