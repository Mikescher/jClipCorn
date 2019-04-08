package de.jClipCorn.util.exceptions;

import de.jClipCorn.util.Str;

@SuppressWarnings("nls")
public class MediaQueryException extends Exception {
	private static final long serialVersionUID = -400882894506601560L;

	public final String MessageLong;

	public MediaQueryException(String text, String stderr) {
		super(text);
		MessageLong = stderr;
	}

	public MediaQueryException(String text) {
		super(text);
		MessageLong = Str.Empty;
	}
}
