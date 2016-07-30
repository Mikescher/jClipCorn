package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class OnlineRefFormatException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975016618L;

	public OnlineRefFormatException(String rep) {
		super("Cannot parse OnlineReference '" + rep + "'");
	}
}
