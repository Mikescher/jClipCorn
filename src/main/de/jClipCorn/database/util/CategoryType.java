package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CategoryType implements ContinoousEnum<CategoryType> 
{ 
	UNKOWN(0), 
	LOW_QUALITY(1), 
	OKAY(2), 
	GOOD(3), 
	VERY_GOOD(4), 
	HIGH_DEFINITION(5);

	private final static String[] NAMES =
	{
		LocaleBundle.getString("CategoryType.UNKOWN"), //$NON-NLS-1$
		LocaleBundle.getString("CategoryType.LOW_QUALITY"), //$NON-NLS-1$
		LocaleBundle.getString("CategoryType.OKAY"), //$NON-NLS-1$
		LocaleBundle.getString("CategoryType.GOOD"), //$NON-NLS-1$
		LocaleBundle.getString("CategoryType.VERY_GOOD"),  //$NON-NLS-1$
		LocaleBundle.getString("CategoryType.HIGH_DEFINITION"),  //$NON-NLS-1$
	};
	
	private int id;
	
	private static EnumWrapper<CategoryType> wrapper = new EnumWrapper<>(UNKOWN);

	private CategoryType(int val) {
		id = val;
	}

	public static EnumWrapper<CategoryType> getWrapper() {
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

	public static int compare(CCFSK o1, CCFSK o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}

	@Override
	public CategoryType[] evalues() {
		return CategoryType.values();
	}

}