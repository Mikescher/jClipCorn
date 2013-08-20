package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DecimalSearchType;

public class CustomOnlinescoreFilter extends AbstractCustomFilter {
	private CCMovieOnlineScore low = CCMovieOnlineScore.STARS_0_0;
	private CCMovieOnlineScore high = CCMovieOnlineScore.STARS_0_0;
	private DecimalSearchType searchType = DecimalSearchType.EXACT;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		CCMovieOnlineScore osco = (CCMovieOnlineScore) e.getValue(ClipTableModel.COLUMN_ONLINESCORE);
		
		switch (searchType) {
		case LESSER:
			return osco.asInt() < high.asInt();
		case GREATER:
			return low.asInt() < osco.asInt();
		case IN_RANGE:
			return low.asInt() < osco.asInt() && osco.asInt() < high.asInt();
		case EXACT:
			return low == osco;
		default:
			return false;
		}
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Onlinescore", asString()); //$NON-NLS-1$
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

	public CCMovieOnlineScore getHigh() {
		return high;
	}

	public void setHigh(CCMovieOnlineScore high) {
		this.high = high;
	}

	public CCMovieOnlineScore getLow() {
		return low;
	}

	public void setLow(CCMovieOnlineScore low) {
		this.low = low;
	}
}
