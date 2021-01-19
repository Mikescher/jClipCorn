package de.jClipCorn.properties.enumerations;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum AppThemePackage implements ContinoousEnum<AppThemePackage> {
	DEFAULT(0),
	RADIANCE(1),
	FLATLAF(2);

	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		"Default",
		"Radiance",
		"FlatLaf",
	};

	private final int id;

	private static final EnumWrapper<AppThemePackage> wrapper = new EnumWrapper<>(DEFAULT);

	AppThemePackage(int val) {
		id = val;
	}
	
	public static EnumWrapper<AppThemePackage> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String[] getList() {
		return NAMES;
	}
	
	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public AppThemePackage[] evalues() {
		return AppThemePackage.values();
	}
}
