package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class FileLockedException extends Exception {
	private static final long serialVersionUID = -400882894506601560L;

	public FileLockedException(String path) {
		super("The file '" + path + "' is locked");
	}
}
