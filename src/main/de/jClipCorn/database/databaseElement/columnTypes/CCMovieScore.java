package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.MultiIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCMovieScore implements ContinoousEnum<CCMovieScore> {
	RATING_0(0),	// Fucking bullshit crap
	RATING_I(1),	// bad movie
	RATING_II(2),	// not recommended
	RATING_III(3),	// good enough to watch
	RATING_IV(4),	// recommended - good movie
	RATING_V(5),	// I f*** love this piece of movie-artwork
	RATING_NO(6);	// Unrated
	
	private final static String NAMES[] = {
		LocaleBundle.getString("CCMovieScore.R0"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R1"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R2"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R3"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R4"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R5"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.RNO")   //$NON-NLS-1$
	};
	
	private int id;

	private static EnumWrapper<CCMovieScore> wrapper = new EnumWrapper<>(RATING_NO);

	private CCMovieScore(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCMovieScore> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(CCMovieScore s1, CCMovieScore s2) {
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
	
	public ImageIcon getIcon() {
		switch (this) {
		case RATING_0:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_SCORE_0.icon16x16);
		case RATING_I:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_SCORE_1.icon16x16);
		case RATING_II:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_SCORE_2.icon16x16);
		case RATING_III:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_SCORE_3.icon16x16);
		case RATING_IV:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_SCORE_4.icon16x16);
		case RATING_V:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_SCORE_5.icon16x16);
		case RATING_NO:
		default:
			return null;
		}
	}
	
	public MultiIconRef getIconRef() {
		switch (this) {
		case RATING_0:
			return Resources.ICN_TABLE_SCORE_0;
		case RATING_I:
			return Resources.ICN_TABLE_SCORE_1;
		case RATING_II:
			return Resources.ICN_TABLE_SCORE_2;
		case RATING_III:
			return Resources.ICN_TABLE_SCORE_3;
		case RATING_IV:
			return Resources.ICN_TABLE_SCORE_4;
		case RATING_V:
			return Resources.ICN_TABLE_SCORE_5;
		case RATING_NO:
		default:
			return null;
		}
	}

	@Override
	public CCMovieScore[] evalues() {
		return CCMovieScore.values();
	}
}
