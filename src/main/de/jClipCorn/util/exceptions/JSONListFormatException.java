package de.jClipCorn.util.exceptions;

/** A JSON list column that cannot be parsed - never fall back to an empty list, it would be written back on the next save. */
@SuppressWarnings("nls")
public class JSONListFormatException extends CCFormatException {
	private static final long serialVersionUID = 3146579215617722870L;

	public JSONListFormatException(String json, Throwable cause) {
		super("Cannot parse JSON list '" + json + "'", cause);
	}
}
