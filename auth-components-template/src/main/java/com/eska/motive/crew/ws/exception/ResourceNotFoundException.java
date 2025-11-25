package com.eska.motive.crew.ws.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found
 * 
 * @author Your Name
 */
@Getter
public class ResourceNotFoundException extends Exception {

    private static final long serialVersionUID = -6806458320186959974L;
    
    private String errorCode;

    public ResourceNotFoundException(String message) {
        super(message);
        this.errorCode = "RESOURCE_NOT_FOUND";
    }
    
    public ResourceNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}


