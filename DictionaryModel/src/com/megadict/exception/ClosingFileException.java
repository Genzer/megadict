package com.megadict.exception;

public class ClosingFileException extends OperationFailedException {

    public ClosingFileException(String file, Exception rootCause) {
        super("closing file " + file, rootCause);
    }
    
    private static final long serialVersionUID = -1047535443812769383L;
}
