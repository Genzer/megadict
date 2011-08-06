package com.megadict.exception;

public class ParseIndexException extends RuntimeException {

    public ParseIndexException(String message) {
        super(message);
    }

    public ParseIndexException(String message, String invalidString) {
        super(message);
        this.invalidString = invalidString;
    }

    public ParseIndexException(String message, Exception rootCause) {
        super(message, rootCause);
    }

    public String getInvalidString() {
        return invalidString;
    }

    private String invalidString;
    private static final long serialVersionUID = 2602100314259911092L;
}
