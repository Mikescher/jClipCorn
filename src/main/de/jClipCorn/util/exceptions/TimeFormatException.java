package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class TimeFormatException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975016618L;

	public TimeFormatException(String rep) {
		super("Cannot parse Time '" + rep + "'");
	}
}
