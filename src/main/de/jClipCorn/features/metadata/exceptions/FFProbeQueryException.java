package de.jClipCorn.features.metadata.exceptions;

public class FFProbeQueryException extends MetadataQueryException {
	private static final long serialVersionUID = -400882894506601560L;

	public FFProbeQueryException(String text, String stderr) {
		super(text, stderr);
	}

	public FFProbeQueryException(String text) {
		super(text);
	}
}
