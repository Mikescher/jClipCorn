package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class TagNotFoundException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975016618L;

	public TagNotFoundException(int v) {
		super("Tag not found: "+v);
	}
}
