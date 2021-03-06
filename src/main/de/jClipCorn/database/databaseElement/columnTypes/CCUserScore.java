package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

import javax.swing.*;

public enum CCUserScore implements ContinoousEnum<CCUserScore> {
	RATING_0  (0, 1),  // Fucking bullshit crap
	RATING_I  (1, 2),  // bad movie
	RATING_II (2, 3),  // not recommended
	RATING_III(3, 5),  // good enough to watch
	RATING_IV (4, 6),  // recommended - good movie
	RATING_V  (5, 7),  // I f*** love this piece of movie-artwork
	RATING_NO (6, 0),  // Unrated
	RATING_MID(7, 4);  // Average - meets expectations

	private final static String[] NAMES = {
		LocaleBundle.getString("CCMovieScore.R0"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R1"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R2"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R3"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R4"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.R5"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.RNO"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieScore.RMID"),  //$NON-NLS-1$
	};

	private final int id;

	private final int order;

	private final static EnumWrapper<CCUserScore> wrapper = new EnumWrapper<>(RATING_NO, p -> p.order);

	private CCUserScore(int val, int ord) {
		id    = val;
		order = ord;
	}
	
	public static EnumWrapper<CCUserScore> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public int getOrder() { return order; }

	public static int compare(CCUserScore s1, CCUserScore s2) {
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
			return Resources.ICN_TABLE_SCORE_0.get16x16();
		case RATING_I:
			return Resources.ICN_TABLE_SCORE_1.get16x16();
		case RATING_II:
			return Resources.ICN_TABLE_SCORE_2.get16x16();
		case RATING_MID:
			return Resources.ICN_TABLE_SCORE_6.get16x16();
		case RATING_III:
			return Resources.ICN_TABLE_SCORE_3.get16x16();
		case RATING_IV:
			return Resources.ICN_TABLE_SCORE_4.get16x16();
		case RATING_V:
			return Resources.ICN_TABLE_SCORE_5.get16x16();
		case RATING_NO:
		default:
			return null;
		}
	}
	
	public MultiSizeIconRef getIconRef() {
		switch (this) {
		case RATING_0:
			return Resources.ICN_TABLE_SCORE_0;
		case RATING_I:
			return Resources.ICN_TABLE_SCORE_1;
		case RATING_II:
			return Resources.ICN_TABLE_SCORE_2;
		case RATING_MID:
			return Resources.ICN_TABLE_SCORE_6;
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
	public CCUserScore[] evalues() {
		return CCUserScore.values();
	}
}
