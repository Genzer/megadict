package com.megadict.exception;

public class OperationFailedException extends RuntimeException {

    public OperationFailedException(String operation, Exception rootCause) {
        super(makeMessage(operation), rootCause);
    }
    
    private static String makeMessage(String operation) {
        return "Problem occured while performing " + operation + ".";
    }
   
    private static final long serialVersionUID = 4475812074237232764L;
}
