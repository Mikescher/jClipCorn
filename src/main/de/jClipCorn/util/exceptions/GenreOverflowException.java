package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class GenreOverflowException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975016618L;

	public GenreOverflowException() {
		super("Too much genres in list");
	}
}
