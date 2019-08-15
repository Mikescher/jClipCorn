package de.jClipCorn.util.datatypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum AnyMatchType implements ContinoousEnum<AnyMatchType> {
	LESSER(0),
	GREATER(1),
	EXACT(2),
	SM_STARTSWITH(3),
	SM_INCLUDES(4),
	SM_ENDSWITH(5);

	private final static String[] NAMES = {
		LocaleBundle.getString("DecimalSearchType.LESSER"),    //$NON-NLS-1$
		LocaleBundle.getString("DecimalSearchType.GREATER"),   //$NON-NLS-1$
		LocaleBundle.getString("DecimalSearchType.EXACT"),     //$NON-NLS-1$
		LocaleBundle.getString("StringMatchType.STARTSWITH"), //$NON-NLS-1$
		LocaleBundle.getString("StringMatchType.INCLUDES"),   //$NON-NLS-1$
		LocaleBundle.getString("StringMatchType.ENDSWITH"),    //$NON-NLS-1$
	};

	private final int id;

	private static EnumWrapper<AnyMatchType> wrapper = new EnumWrapper<>(LESSER);

	private AnyMatchType(int val) {
		id = val;
	}
	
	public static EnumWrapper<AnyMatchType> getWrapper() {
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
	public AnyMatchType[] evalues() {
		return AnyMatchType.values();
	}
}
