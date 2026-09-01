package de.jClipCorn.util.exceptions;

public class UUIDFormatException extends CCFormatException {
	private static final long serialVersionUID = 3182749813350276411L;

	public UUIDFormatException(String msg) {
		super(msg);
	}

	public UUIDFormatException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
