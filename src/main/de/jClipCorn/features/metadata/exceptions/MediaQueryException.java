package de.jClipCorn.features.metadata.exceptions;

public class MediaQueryException extends MetadataQueryException {
	private static final long serialVersionUID = -400882894506601560L;

	public MediaQueryException(String text, String stderr) {
		super(text, stderr);
	}

	public MediaQueryException(String text, String stderr, Throwable cause) {
		super(text, stderr, cause);
	}

	public MediaQueryException(String text) {
		super(text);
	}

	public MediaQueryException(String text, Throwable cause) {
		super(text, cause);
	}
}
