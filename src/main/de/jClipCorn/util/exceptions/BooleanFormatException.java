package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class BooleanFormatException extends CCFormatException {
	private static final long serialVersionUID = 1657838580504616413L;

	public BooleanFormatException(String rep) {
		super("Cannot parse Boolean '" + rep + "'");
	}
}
