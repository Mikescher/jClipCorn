package de.jClipCorn.util.exceptions;

public class CancelledException extends RuntimeException {
	public CancelledException() {
		super("Operation cancelled"); //$NON-NLS-1$
	}
}
