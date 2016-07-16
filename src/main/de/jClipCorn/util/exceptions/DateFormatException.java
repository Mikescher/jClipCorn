package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class DateFormatException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975016618L;

	public DateFormatException(String rep) {
		super("Cannot parse Date '" + rep + "'");
	}
}
