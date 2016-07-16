package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class DateTimeFormatException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975016618L;

	public DateTimeFormatException(String rep) {
		super("Cannot parse DateTime '" + rep + "'");
	}
}
