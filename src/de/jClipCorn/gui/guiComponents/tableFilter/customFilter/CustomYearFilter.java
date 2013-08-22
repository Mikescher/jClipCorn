package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.CCDate;
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
			return "X < " + high; //$NON-NLS-1$
		case GREATER:
			return low + " < X"; //$NON-NLS-1$
		case IN_RANGE:
			return low + " < X < " + high; //$NON-NLS-1$
		case EXACT:
			return "X == " + low; //$NON-NLS-1$
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
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_YEAR;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(low+"");
		b.append(",");
		b.append(high+"");
		b.append(",");
		b.append(searchType.asInt() + "");
		b.append("]");
		
		return b.toString();
	}
	
	@SuppressWarnings("nls")
	@Override
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = params.split(Pattern.quote(","));
		if (paramsplit.length != 3) return false;
		
		int intval;
		DecimalSearchType s;
		
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		if (!(intval >= CCDate.YEAR_MIN && intval < CCDate.YEAR_MAX)) return false;
		setLow(intval);
		
		try {
			intval = Integer.parseInt(paramsplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		if (!(intval >= CCDate.YEAR_MIN && intval < CCDate.YEAR_MAX)) return false;
		setHigh(intval);
		
		try {
			intval = Integer.parseInt(paramsplit[2]);
		} catch (NumberFormatException e) {
			return false;
		}
		s = DecimalSearchType.find(intval);
		if (s == null) return false;
		setSearchType(s);
		
		return true;
	}
}