package de.jClipCorn.util.exceptions;

@SuppressWarnings("nls")
public class EOptPackFormatException extends CCFormatException {
	@SuppressWarnings("rawtypes")
	public EOptPackFormatException(String val, Class cls) {
		super("Value '" + val + "' is not a valid format for the Opt<X> EProp " + cls.getSimpleName());
	}
}
