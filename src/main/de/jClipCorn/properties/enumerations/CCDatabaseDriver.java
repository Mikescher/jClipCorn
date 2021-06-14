package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum CCDatabaseDriver implements ContinoousEnum<CCDatabaseDriver> {
	DERBY(0),
	SQLITE(1), 
	STUB(2), 
	INMEMORY(3);
	
	private final static String[] NAMES = {
		LocaleBundle.getString("CCDatabaseDriver.DERBY"),   //$NON-NLS-1$
		LocaleBundle.getString("CCDatabaseDriver.SQLITE"),  //$NON-NLS-1$
		LocaleBundle.getString("CCDatabaseDriver.STUB"),    //$NON-NLS-1$
		LocaleBundle.getString("CCDatabaseDriver.INMEMORY"),//$NON-NLS-1$
	};
	
	private int id;

	private static final EnumWrapper<CCDatabaseDriver> wrapper = new EnumWrapper<>(DERBY);

	CCDatabaseDriver(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCDatabaseDriver> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}

	public static CCDatabaseDriver[] getSelectableValues() {
		return new CCDatabaseDriver[]{ DERBY, SQLITE };
	}

	public boolean isDeprecated() {
		if (this == DERBY) return true;
		return false;
	}

	@Override
	public int asInt() {
		return id;
	}

	public static int compare(CCDatabaseDriver s1, CCDatabaseDriver s2) {
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
	public CCDatabaseDriver[] evalues() {
		return CCDatabaseDriver.values();
	}
}
