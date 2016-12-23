package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCOnlineScore implements ContinoousEnum<CCOnlineScore> {
	STARS_0_0(0),
	STARS_0_5(1),
	STARS_1_0(2),
	STARS_1_5(3),
	STARS_2_0(4),
	STARS_2_5(5),
	STARS_3_0(6),
	STARS_3_5(7),
	STARS_4_0(8),
	STARS_4_5(9),
	STARS_5_0(10);

	private final static String NAMES[] = {
		LocaleBundle.getString("CCMovieOnlineScore.00"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.05"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.10"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.15"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.20"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.25"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.30"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.35"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.40"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.45"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieOnlineScore.50")  //$NON-NLS-1$
	};
	
	private int id;
	
	private static EnumWrapper<CCOnlineScore> wrapper = new EnumWrapper<>(STARS_0_0);

	private CCOnlineScore(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCOnlineScore> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(CCOnlineScore o1, CCOnlineScore o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case STARS_0_0:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_0);
		case STARS_0_5:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_1);
		case STARS_1_0:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_2);
		case STARS_1_5:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_3);
		case STARS_2_0:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_4);
		case STARS_2_5:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_5);
		case STARS_3_0:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_6);
		case STARS_3_5:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_7);
		case STARS_4_0:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_8);
		case STARS_4_5:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_9);
		case STARS_5_0:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_ONLINESCORE_10);
		default:
			return null;
		}
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
	public CCOnlineScore[] evalues() {
		return CCOnlineScore.values();
	}
}