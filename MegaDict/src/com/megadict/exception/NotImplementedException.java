package com.megadict.exception;

public class NotImplementedException extends RuntimeException {
	public NotImplementedException(final Throwable t) {
		super(t);
	}

	public NotImplementedException() {
		super("Not implented yet.");
	}
}
