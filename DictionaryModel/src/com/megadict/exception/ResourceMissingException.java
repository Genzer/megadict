package com.megadict.exception;

public class ResourceMissingException extends RuntimeException {

    public ResourceMissingException(String resourceName) {
        super(makeMessage(resourceName));
    }
    
    private static String makeMessage(String resourceName) {
        return resourceName + " is missing";
    }
    
    public ResourceMissingException(String resourceName, Exception rootCause) {
        super(makeMessage(resourceName), rootCause);
    }
    
    private static final long serialVersionUID = -6568658901793220837L;
    
}
