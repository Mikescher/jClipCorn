package de.jClipCorn.util.datatypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum StringMatchType implements ContinoousEnum<StringMatchType> {
	SM_STARTSWITH(0),
	SM_INCLUDES(1),
	SM_ENDSWITH(2),
	SM_EQUALS(3);

	private final static String[] NAMES = {
		LocaleBundle.getString("StringMatchType.STARTSWITH"), //$NON-NLS-1$
		LocaleBundle.getString("StringMatchType.INCLUDES"),   //$NON-NLS-1$
		LocaleBundle.getString("StringMatchType.ENDSWITH"),    //$NON-NLS-1$
		LocaleBundle.getString("StringMatchType.EQUALS"),    //$NON-NLS-1$
	};
	
	private final int id;
	
	private static final EnumWrapper<StringMatchType> wrapper = new EnumWrapper<>(SM_STARTSWITH);

	private StringMatchType(int val) {
		id = val;
	}
	
	public static EnumWrapper<StringMatchType> getWrapper() {
		return wrapper;
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
	public StringMatchType[] evalues() {
		return StringMatchType.values();
	}
}
