package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

import javax.swing.*;

public enum CCFSK implements ContinoousEnum<CCFSK> {
	RATING_0(0), // keine Alterschbeschräkung
	RATING_I(1), // ab 6 Jahren
	RATING_II(2), // ab 12 Jahren
	RATING_III(3), // ab 16 Jahren
	RATING_IV(4); // keine Jugendfreigabe

	private final static String[] NAMES =
	{
		LocaleBundle.getString("CCMovieFSK.FSK0"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieFSK.FSK1"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieFSK.FSK2"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieFSK.FSK3"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieFSK.FSK4")  //$NON-NLS-1$
	};
	private final static int[] AGES = {0, 6, 12, 16, 18};
	private int id;
	
	private static final EnumWrapper<CCFSK> wrapper = new EnumWrapper<>(RATING_0);

	private CCFSK(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCFSK> getWrapper() {
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

	public static CCFSK getNearest(int age) {
		int val = -1;
		int max = Integer.MAX_VALUE;

		for (int i = 0; i < AGES.length; i++) {
			if (Math.abs(age - AGES[i]) <= max) { // <= is used to prioritize the higher Ratings (Age=9 will result in FSK-12, rathrer than FSK-6)
				val = i;
				max = Math.abs(age - AGES[i]);
			}
		}

		if (val == -1) return null;
		
		return getWrapper().findOrFatalError(val);
	}

	public static int compare(CCFSK o1, CCFSK o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}

	public ImageIcon getIcon() {
		switch (this) {
		case RATING_0:
			return Resources.ICN_TABLE_FSK_0.get();
		case RATING_I:
			return Resources.ICN_TABLE_FSK_1.get();
		case RATING_II:
			return Resources.ICN_TABLE_FSK_2.get();
		case RATING_III:
			return Resources.ICN_TABLE_FSK_3.get();
		case RATING_IV:
			return Resources.ICN_TABLE_FSK_4.get();
		default:
			return null;
		}
	}

	@Override
	public CCFSK[] evalues() {
		return CCFSK.values();
	}

	public CCOptionalFSK asOptionalFSK() {
		return CCOptionalFSK.getWrapper().findOrFatalError(id);
	}
}
