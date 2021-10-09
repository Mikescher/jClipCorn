package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class OnlineScoreFormatException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975016618L;

	public OnlineScoreFormatException(String rep) {
		super("Cannot parse OnlineScore '" + rep + "'");
	}

	public OnlineScoreFormatException(String rep, Throwable cause) {
		super("Cannot parse OnlineScore '" + rep + "'", cause);
	}
}
