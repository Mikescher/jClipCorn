package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class StringSpecFormatException extends CCFormatException {
	private static final long serialVersionUID = 1657838580504616413L;

	public StringSpecFormatException(String data, String fmt) {
		super("Cannot parse Value '" + data + "' agains format '" + fmt + "'");
	}
}
