package com.megadict.exception;

public class OperationFailedException extends RuntimeException {

    public OperationFailedException(String operationName, Exception rootCause) {
        super(makeMessage(operationName), rootCause);
    }
    
    private static String makeMessage(String operationName) {
        return "Problem occured while performing " + operationName + ".";
    }
   
    private static final long serialVersionUID = 4475812074237232764L;
}
