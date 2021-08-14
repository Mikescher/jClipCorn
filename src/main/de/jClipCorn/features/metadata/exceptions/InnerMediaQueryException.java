package de.jClipCorn.features.metadata.exceptions;

public class InnerMediaQueryException extends MetadataQueryException {
	private static final long serialVersionUID = -400882894506601561L;

	public InnerMediaQueryException(String text) {
		super(text);
	}

	public InnerMediaQueryException(String text, Throwable cause) {
		super(text, cause);
	}
}
