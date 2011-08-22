package com.megadict.exception;

public class OperationFailedException extends RuntimeException {
    
    private static final long serialVersionUID = 4475812074237232764L;
    private static final String TEMPLATE_MESSAGE = "Problem occured while performing %s.";
    private static final String TEMPLATE_MESSAGE_FOR_ROOT_CAUSE = "\nThe root cause is %s : %s";
 
    public OperationFailedException(String operationName) {
        super(makeMessage(operationName));
    }

    public OperationFailedException(String operationName, Exception rootCause) {
        super(makeMessageWithRootCause(operationName, rootCause), rootCause);
    }
    
    private static String makeMessage(String operationName) {
        return String.format(TEMPLATE_MESSAGE, operationName);
    }

    private static String makeMessageWithRootCause(String operationName, Exception rootCause) {
        StringBuilder fullMessage = new StringBuilder();
        fullMessage.append(makeMessage(operationName));
        fullMessage.append(String.format(TEMPLATE_MESSAGE_FOR_ROOT_CAUSE, rootCause.getClass().getName(),
                rootCause.getMessage()));
        return fullMessage.toString();
    }
}
