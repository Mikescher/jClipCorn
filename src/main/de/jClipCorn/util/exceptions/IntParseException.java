package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class IntParseException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975016618L;

	public IntParseException(String rep) {
		super("Cannot parse integer '" + rep + "'");
	}
}
