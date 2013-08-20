package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DecimalSearchType;

public class CustomScoreFilter extends AbstractCustomFilter {
	private CCMovieScore low = CCMovieScore.RATING_0;
	private CCMovieScore high = CCMovieScore.RATING_0;
	private DecimalSearchType searchType = DecimalSearchType.EXACT;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		CCMovieScore sco = (CCMovieScore) e.getValue(ClipTableModel.COLUMN_SCORE);
		
		switch (searchType) {
		case LESSER:
			return sco.asInt() < high.asInt();
		case GREATER:
			return low.asInt() < sco.asInt();
		case IN_RANGE:
			return low.asInt() < sco.asInt() && sco.asInt() < high.asInt();
		case EXACT:
			return low == sco;
		default:
			return false;
		}
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Score", asString()); //$NON-NLS-1$
	}
	
	public String asString() {
		switch (searchType) {
		case LESSER:
			return "< " + low.asInt(); //$NON-NLS-1$
		case GREATER:
			return low.asInt() + " <"; //$NON-NLS-1$
		case IN_RANGE:
			return low.asInt() + " < X < " + high.asInt(); //$NON-NLS-1$
		case EXACT:
			return "== " + low.asInt(); //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}

	public DecimalSearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(DecimalSearchType searchType) {
		this.searchType = searchType;
	}

	public CCMovieScore getHigh() {
		return high;
	}

	public void setHigh(CCMovieScore high) {
		this.high = high;
	}

	public CCMovieScore getLow() {
		return low;
	}

	public void setLow(CCMovieScore low) {
		this.low = low;
	}
}
