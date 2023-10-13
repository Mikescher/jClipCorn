package de.jClipCorn.util.datatypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum ElemFieldMatchType implements ContinoousEnum<ElemFieldMatchType> {
	TITLE(0),
	ZYKLUS(1),
	TITLE_AND_ZYKLUS(2);

	private final static String[] NAMES = {
		LocaleBundle.getString("ElemFieldMatchType.TITLE"),            //$NON-NLS-1$
		LocaleBundle.getString("ElemFieldMatchType.ZYKLUS"),           //$NON-NLS-1$
		LocaleBundle.getString("ElemFieldMatchType.TITLE_AND_ZYKLUS")  //$NON-NLS-1$
	};

	private final int id;

	private static final EnumWrapper<ElemFieldMatchType> wrapper = new EnumWrapper<>(TITLE_AND_ZYKLUS);

	ElemFieldMatchType(int val) {
		id = val;
	}
	
	public static EnumWrapper<ElemFieldMatchType> getWrapper() {
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
	public ElemFieldMatchType[] evalues() {
		return ElemFieldMatchType.values();
	}
}
