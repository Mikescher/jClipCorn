package de.jClipCorn.util.exceptions;

public class XMLFormatException extends Exception {
	private static final long serialVersionUID = -400882824506601560L;

	public XMLFormatException(String message) {
		super(message);
	}

	public XMLFormatException(String message, Exception cause) {
		super(message, cause);
	}
}
