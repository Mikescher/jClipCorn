package de.jClipCorn.util.datatypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum CharListMatchType implements ContinoousEnum<CharListMatchType> {
	STRING_START(0),
	WORD_START(1),
	ANYWHERE(2);

	private final static String[] NAMES = {
		LocaleBundle.getString("CharListMatchType.STRING_START"), //$NON-NLS-1$
		LocaleBundle.getString("CharListMatchType.WORD_START"),   //$NON-NLS-1$
		LocaleBundle.getString("CharListMatchType.ANYWHERE")      //$NON-NLS-1$
	};

	private final int id;

	private static final EnumWrapper<CharListMatchType> wrapper = new EnumWrapper<>(STRING_START);

	CharListMatchType(int val) {
		id = val;
	}
	
	public static EnumWrapper<CharListMatchType> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}
	
	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public String[] getList() {
		return NAMES;
	}

	@Override
	public CharListMatchType[] evalues() {
		return CharListMatchType.values();
	}
}
