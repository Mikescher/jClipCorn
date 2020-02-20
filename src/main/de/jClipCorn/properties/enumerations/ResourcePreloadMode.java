package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum ResourcePreloadMode implements ContinoousEnum<ResourcePreloadMode> {
	NO_PRELOAD(0),
	SYNC_PRELOAD(1),
	ASYNC_PRELOAD(2);

	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("ResourcePreloadMode.Opt0"),
		LocaleBundle.getString("ResourcePreloadMode.Opt1"),
		LocaleBundle.getString("ResourcePreloadMode.Opt2"),
	};

	private int id;

	private static final EnumWrapper<ResourcePreloadMode> wrapper = new EnumWrapper<>(NO_PRELOAD);

	private ResourcePreloadMode(int val) {
		id = val;
	}
	
	public static EnumWrapper<ResourcePreloadMode> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(ResourcePreloadMode s1, ResourcePreloadMode s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
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
	public ResourcePreloadMode[] evalues() {
		return ResourcePreloadMode.values();
	}
}
