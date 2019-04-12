package de.jClipCorn.util.exceptions;

public class SerializationException extends Exception {
	private static final long serialVersionUID = -8706835645321245202L;

	public SerializationException(String msg) {
		super(msg);
	}

	public SerializationException(String msg, Exception inner) {
		super(msg, inner);
	}

}
