package com.sipgate.siptest.exception;

public class UnexpectedSiptestException extends RuntimeException {

	public UnexpectedSiptestException() {
	}

	public UnexpectedSiptestException(String message) {
		super(message);
	}

	public UnexpectedSiptestException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedSiptestException(Throwable cause) {
		super(cause);
	}

	public UnexpectedSiptestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
