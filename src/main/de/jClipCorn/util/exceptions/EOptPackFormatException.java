package de.jClipCorn.util.exceptions;

import de.jClipCorn.util.enumextension.ContinoousEnum;

@SuppressWarnings("nls")
public class EOptPackFormatException extends CCFormatException {
	@SuppressWarnings("rawtypes")
	public EOptPackFormatException(String val, Class cls) {
		super("Value '" + val + "' is not a valid format for the Opt<X> EProp " + cls.getSimpleName());
	}
}
