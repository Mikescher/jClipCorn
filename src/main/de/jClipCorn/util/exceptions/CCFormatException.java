package de.jClipCorn.util.exceptions;

public abstract class CCFormatException extends Exception {
	private static final long serialVersionUID = 6077380414975475759L;
	
	public CCFormatException(String msg) {
		super(msg);
	}

	public CCFormatException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
