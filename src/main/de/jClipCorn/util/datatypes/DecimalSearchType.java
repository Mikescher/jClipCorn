package de.jClipCorn.util.datatypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum DecimalSearchType implements ContinoousEnum<DecimalSearchType> {
	LESSER(0),
	GREATER(1),
	IN_RANGE(2),
	EXACT(3);

	private final static String[] NAMES = {
		LocaleBundle.getString("DecimalSearchType.LESSER"),    //$NON-NLS-1$
		LocaleBundle.getString("DecimalSearchType.GREATER"),   //$NON-NLS-1$
		LocaleBundle.getString("DecimalSearchType.IN_RANGE"),  //$NON-NLS-1$
		LocaleBundle.getString("DecimalSearchType.EXACT")      //$NON-NLS-1$
	};
	
	private final int id;
	
	private static final EnumWrapper<DecimalSearchType> wrapper = new EnumWrapper<>(LESSER);

	DecimalSearchType(int val) {
		id = val;
	}
	
	public static EnumWrapper<DecimalSearchType> getWrapper() {
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
	public DecimalSearchType[] evalues() {
		return DecimalSearchType.values();
	}
}
