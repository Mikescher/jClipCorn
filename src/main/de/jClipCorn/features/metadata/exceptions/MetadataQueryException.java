package de.jClipCorn.features.metadata.exceptions;

import de.jClipCorn.util.Str;

public class MetadataQueryException extends Exception {
	private static final long serialVersionUID = 8034916440118958657L;
	
	public final String MessageLong;

	public MetadataQueryException(String text, String stderr) {
		super(text);
		MessageLong = stderr;
	}

	public MetadataQueryException(String text) {
		super(text);
		MessageLong = Str.Empty;
	}

	public MetadataQueryException(String text, Throwable cause) {
		super(text, cause);
		MessageLong = Str.Empty;
	}
}
