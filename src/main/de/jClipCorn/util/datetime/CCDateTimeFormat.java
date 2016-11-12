package de.jClipCorn.util.datetime;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCDateTimeFormat implements ContinoousEnum<CCDateTimeFormat> {
	ISO_8601(0),
	GERMAN(1),
	AMERICA(2);

	private final static String NAMES[] = { 
		LocaleBundle.getString("CCDateTimeFormat.ISO_8601"), //$NON-NLS-1$
		LocaleBundle.getString("CCDateTimeFormat.GERMAN"),   //$NON-NLS-1$
		LocaleBundle.getString("CCDateTimeFormat.AMERICA"),  //$NON-NLS-1$
	};

	private int id;
	
	private static EnumWrapper<CCDateTimeFormat> wrapper = new EnumWrapper<>(ISO_8601);

	private CCDateTimeFormat(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCDateTimeFormat> getWrapper() {
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
	public CCDateTimeFormat[] evalues() {
		return CCDateTimeFormat.values();
	}
}
