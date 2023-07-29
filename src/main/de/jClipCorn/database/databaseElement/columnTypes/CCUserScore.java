package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

import javax.swing.*;

public enum CCUserScore implements ContinoousEnum<CCUserScore> {
	RATING_0  (0, 1),  // [-3] Fucking bullshit crap
	RATING_I  (1, 2),  // [-2] bad movie
	RATING_II (2, 3),  // [-1] not recommended
	RATING_III(3, 5),  // [+1] good enough to watch
	RATING_IV (4, 6),  // [+2] recommended - good movie
	RATING_V  (5, 7),  // [+3] I f*** love this piece of movie-artwork
	RATING_NO (6, 0),  // [..] Unrated
	RATING_MID(7, 4);  // [  ] Average - meets expectations

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

	CCUserScore(int val, int ord) {
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
	
	public ImageIcon getIcon(boolean hasComment)
	{
		var icnref = this.getIconRef(hasComment);
		if (icnref == null) return null;

		return icnref.get16x16();
	}

	@SuppressWarnings("DuplicateBranchesInSwitch")
	public MultiSizeIconRef getIconRef(boolean hasComment)
	{
		if (hasComment)
		{
			switch (this)
			{
				case RATING_0:   return Resources.ICN_TABLE_SCORE_0_COMMENT;
				case RATING_I:   return Resources.ICN_TABLE_SCORE_1_COMMENT;
				case RATING_II:  return Resources.ICN_TABLE_SCORE_2_COMMENT;
				case RATING_MID: return Resources.ICN_TABLE_SCORE_6_COMMENT;
				case RATING_III: return Resources.ICN_TABLE_SCORE_3_COMMENT;
				case RATING_IV:  return Resources.ICN_TABLE_SCORE_4_COMMENT;
				case RATING_V:   return Resources.ICN_TABLE_SCORE_5_COMMENT;
				case RATING_NO:  return null;
				default:         return null;
			}
		}
		else
		{
			switch (this)
			{
				case RATING_0:   return Resources.ICN_TABLE_SCORE_0_NOCOMMENT;
				case RATING_I:   return Resources.ICN_TABLE_SCORE_1_NOCOMMENT;
				case RATING_II:  return Resources.ICN_TABLE_SCORE_2_NOCOMMENT;
				case RATING_MID: return Resources.ICN_TABLE_SCORE_6_NOCOMMENT;
				case RATING_III: return Resources.ICN_TABLE_SCORE_3_NOCOMMENT;
				case RATING_IV:  return Resources.ICN_TABLE_SCORE_4_NOCOMMENT;
				case RATING_V:   return Resources.ICN_TABLE_SCORE_5_NOCOMMENT;
				case RATING_NO:  return null;
				default:         return null;
			}
		}
	}

	@Override
	public CCUserScore[] evalues() {
		return CCUserScore.values();
	}
}
