package de.jClipCorn.features.metadata.exceptions;

public class MP4BoxQueryException extends MetadataQueryException {
	private static final long serialVersionUID = 8242423575518007273L;

	public MP4BoxQueryException(String text, String stderr) {
		super(text, stderr);
	}

	public MP4BoxQueryException(String text) {
		super(text);
	}
}
