package com.megadict.exception;

public class InternetConnectionFailedException extends RuntimeException {
    private static final long serialVersionUID = -8787125253432087963L;
    
    public InternetConnectionFailedException() {
        super();
    }
    
    public InternetConnectionFailedException(String message) {
        super(message);
    }
    
    public InternetConnectionFailedException(String message, Exception rootCause) {
        super(message, rootCause);
    }
    
}
