package de.jClipCorn.util.filesystem;

public interface FSPathFilter {
	/**
	 * Tests if a specified file should be included in a file list.
	 */
	boolean accept(FSPath path);
}
