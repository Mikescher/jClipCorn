package de.jClipCorn.util.stream;

public class CCDeadStreamError extends Error {
	private static final long serialVersionUID = -253575814792947570L;

	public CCDeadStreamError() {
		super("Cannot iterate finished CCStream"); //$NON-NLS-1$
	}
}
