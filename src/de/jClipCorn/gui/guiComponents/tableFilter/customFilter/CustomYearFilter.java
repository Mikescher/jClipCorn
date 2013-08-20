package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.YearRange;

public class CustomYearFilter extends AbstractCustomFilter {
	private int low = 1900;
	private int high = 1900;
	private DecimalSearchType searchType = DecimalSearchType.EXACT;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		YearRange range = (YearRange)e.getValue(ClipTableModel.COLUMN_YEAR);
		
		switch (searchType) {
		case LESSER:
			return range.isCompletelySmallerThan(new YearRange(high));
		case GREATER:
			return range.isCompletelyGreaterThan(new YearRange(low));
		case IN_RANGE:
			return range.isCompletelyBetween(new YearRange(low), new YearRange(high));
		case EXACT:
			return range.includes(low);
		default:
			return false;
		}
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Year", asString()); //$NON-NLS-1$
	}
	
	public String asString() {
		switch (searchType) {
		case LESSER:
			return "< " + high; //$NON-NLS-1$
		case GREATER:
			return low + " <"; //$NON-NLS-1$
		case IN_RANGE:
			return low + " < X < " + high; //$NON-NLS-1$
		case EXACT:
			return "== " + low; //$NON-NLS-1$
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

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}

	public int getLow() {
		return low;
	}

	public void setLow(int low) {
		this.low = low;
	}
}
