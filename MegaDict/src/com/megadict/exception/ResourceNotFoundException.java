package com.megadict.exception;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(final Throwable t) {
		super(t);
	}
}
