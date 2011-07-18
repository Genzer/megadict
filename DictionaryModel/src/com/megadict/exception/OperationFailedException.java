package com.megadict.exception;

public class OperationFailedException extends RuntimeException {

    public OperationFailedException(String operationName, Exception rootCause) {
        super(makeMessage(operationName), rootCause);
    }
    
    private static String makeMessage(String operationName) {
        return  String.format(TEMPLATE_MESSAGE, operationName);
    }
   
    private static final String TEMPLATE_MESSAGE = "Problem occured while performing %s .";
    private static final long serialVersionUID = 4475812074237232764L;
}
