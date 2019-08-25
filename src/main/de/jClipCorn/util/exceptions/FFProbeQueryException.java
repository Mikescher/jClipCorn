package de.jClipCorn.util.exceptions;

import de.jClipCorn.util.Str;

@SuppressWarnings("nls")
public class FFProbeQueryException extends Exception {
	private static final long serialVersionUID = -400882894506601560L;

	public final String MessageLong;

	public FFProbeQueryException(String text, String stderr) {
		super(text);
		MessageLong = stderr;
	}

	public FFProbeQueryException(String text) {
		super(text);
		MessageLong = Str.Empty;
	}
}
