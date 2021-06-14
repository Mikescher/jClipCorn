package de.jClipCorn.util.enumextension;

import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.EnumValueNotFoundException;

@SuppressWarnings("rawtypes")
public interface IEnumWrapper {
	ContinoousEnum findOrNull(int val);
	ContinoousEnum findOrException(int val) throws EnumFormatException;
	ContinoousEnum findByTextOrException(String strval) throws EnumValueNotFoundException;
}
