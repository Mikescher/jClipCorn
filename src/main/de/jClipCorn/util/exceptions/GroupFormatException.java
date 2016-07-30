package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class GroupFormatException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975026618L;

	public GroupFormatException(String rep) {
		super("Cannot parse group '" + rep + "'");
	}

	public GroupFormatException(String rep, String lst) {
		super("Cannot parse group '" + rep + "' in list '" + lst + "'");
	}
}
