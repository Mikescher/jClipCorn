package de.jClipCorn.util.exceptions;

import de.jClipCorn.util.enumextension.ContinoousEnum;

@SuppressWarnings("nls")
public class EnumValueNotFoundException extends CCFormatException {
	private static final long serialVersionUID = -1691358075975016618L;

	@SuppressWarnings("rawtypes")
	public EnumValueNotFoundException(String val, Class<? extends ContinoousEnum> cls) {
		super("Value '" + val + "' is not a valid element of enum " + cls.getSimpleName());
	}
}
