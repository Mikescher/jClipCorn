package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCQualityCategoryType implements ContinoousEnum<CCQualityCategoryType>
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

	private final static IconRef[] ICONS =
	{
		Resources.ICN_TABLE_QUALITY_0,

		Resources.ICN_TABLE_QUALITY_1,
		Resources.ICN_TABLE_QUALITY_2,
		Resources.ICN_TABLE_QUALITY_3,
		Resources.ICN_TABLE_QUALITY_4,
		Resources.ICN_TABLE_QUALITY_5,
	};

	private int id;
	
	private static final EnumWrapper<CCQualityCategoryType> wrapper = new EnumWrapper<>(UNKOWN);

	CCQualityCategoryType(int val) {
		id = val;
	}

	public static EnumWrapper<CCQualityCategoryType> getWrapper() {
		return wrapper;
	}

	public static CCQualityCategoryType min(CCQualityCategoryType a, CCQualityCategoryType b) {
		if (a == UNKOWN) return b;
		if (b == UNKOWN) return a;

		if (a.asInt() < b.asInt()) return a; else return b;
	}

	public static CCQualityCategoryType min(CCQualityCategoryType a, CCQualityCategoryType b, CCQualityCategoryType c) {
		if (a == UNKOWN) return min(b,c);
		if (b == UNKOWN) return min(a,c);
		if (c == UNKOWN) return min(b,b);

		if (a.asInt() < b.asInt()) {
			if (a.asInt() < c.asInt()) return a; else return c;
		} else {
			if (c.asInt() < b.asInt()) return c; else return b;
		}
	}

	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	public IconRef getIcon() {
		return ICONS[asInt()];
	}

	@Override
	public String[] getList() {
		return NAMES;
	}

	public static int compare(CCFSK o1, CCFSK o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}

	@Override
	public CCQualityCategoryType[] evalues() {
		return CCQualityCategoryType.values();
	}

}