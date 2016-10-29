package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCMovieFSK implements ContinoousEnum<CCMovieFSK> {
	RATING_0(0), // keine Alterschbeschräkung
	RATING_I(1), // ab 6 Jahren
	RATING_II(2), // ab 12 Jahren
	RATING_III(3), // ab 16 Jahren
	RATING_IV(4); // keine Jugendfreigabe

	private final static String NAMES[] = { LocaleBundle.getString("CCMovieFSK.FSK0"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieFSK.FSK1"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieFSK.FSK2"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieFSK.FSK3"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieFSK.FSK4") }; //$NON-NLS-1$
	private final static int AGES[] = { 0, 6, 12, 16, 18 };
	private int id;
	
	private static EnumWrapper<CCMovieFSK> wrapper = new EnumWrapper<>(RATING_0);

	private CCMovieFSK(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCMovieFSK> getWrapper() {
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

	public static CCMovieFSK getNearest(int age) {
		int val = -1;
		int max = Integer.MAX_VALUE;

		for (int i = 0; i < AGES.length; i++) {
			if (Math.abs(age - AGES[i]) <= max) { // <= is used to prioritize the higher Ratings (Age=9 will result in FSK-12, rathrer than FSK-6)
				val = i;
				max = Math.abs(age - AGES[i]);
			}
		}

		return getWrapper().find(val);
	}

	public static int compare(CCMovieFSK o1, CCMovieFSK o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}

	public ImageIcon getIcon() {
		switch (this) {
		case RATING_0:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_FSK_0);
		case RATING_I:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_FSK_1);
		case RATING_II:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_FSK_2);
		case RATING_III:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_FSK_3);
		case RATING_IV:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_FSK_4);
		default:
			return null;
		}
	}

	@Override
	public CCMovieFSK[] evalues() {
		return CCMovieFSK.values();
	}
}
