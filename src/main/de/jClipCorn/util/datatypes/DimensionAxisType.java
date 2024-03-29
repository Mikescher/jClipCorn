package de.jClipCorn.util.datatypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum DimensionAxisType implements ContinoousEnum<DimensionAxisType> {
	WIDTH(0),
	HEIGHT(1);

	private final static String[] NAMES = {
		LocaleBundle.getString("DimensionAxisType.WIDTH"), //$NON-NLS-1$
		LocaleBundle.getString("DimensionAxisType.HEIGHT"),   //$NON-NLS-1$
	};
	
	private final int id;
	
	private static final EnumWrapper<DimensionAxisType> wrapper = new EnumWrapper<>(WIDTH);

	DimensionAxisType(int val) {
		id = val;
	}
	
	public static EnumWrapper<DimensionAxisType> getWrapper() {
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
	public DimensionAxisType[] evalues() {
		return DimensionAxisType.values();
	}
}
