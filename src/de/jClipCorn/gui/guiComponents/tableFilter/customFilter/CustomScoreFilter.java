package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.util.regex.Pattern;

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
			return "X < " + low.asString(); //$NON-NLS-1$
		case GREATER:
			return low.asString() + " < X"; //$NON-NLS-1$
		case IN_RANGE:
			return low.asString() + " < X < " + high.asString(); //$NON-NLS-1$
		case EXACT:
			return "X == " + low.asString(); //$NON-NLS-1$
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
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_SCORE;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(low.asInt()+"");
		b.append(",");
		b.append(high.asInt()+"");
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
		CCMovieScore f;
		DecimalSearchType s;
		
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		f = CCMovieScore.find(intval);
		if (f == null) return false;
		setLow(f);
		
		try {
			intval = Integer.parseInt(paramsplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		f = CCMovieScore.find(intval);
		if (f == null) return false;
		setHigh(f);
		
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
