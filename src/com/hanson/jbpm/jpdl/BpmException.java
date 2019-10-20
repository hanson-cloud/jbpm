package com.hanson.jbpm.jpdl;

public class BpmException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BpmException() {
		super();
	}

	public BpmException(String message, Throwable cause) {
		super(message, cause);
	}

	public BpmException(String message) {
		super(message);
	}

	public BpmException(Throwable cause) {
		super(cause);
	}
}
