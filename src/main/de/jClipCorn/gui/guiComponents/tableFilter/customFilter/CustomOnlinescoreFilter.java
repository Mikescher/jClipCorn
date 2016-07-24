package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs.CustomFilterDialog;
import de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs.CustomOnlinescoreFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.listener.FinishListener;

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

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Onlinescore").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		switch (searchType) {
		case LESSER:
			return "X < " + high.asInt()/2.0; //$NON-NLS-1$
		case GREATER:
			return low.asInt()/2.0 + " < X"; //$NON-NLS-1$
		case IN_RANGE:
			return low.asInt()/2.0 + " < X < " + high.asInt()/2.0; //$NON-NLS-1$
		case EXACT:
			return "X == " + low.asInt()/2.0; //$NON-NLS-1$
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
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ONLINESCORE;
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
		CCMovieOnlineScore f;
		DecimalSearchType s;
		
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		f = CCMovieOnlineScore.find(intval);
		if (f == null) return false;
		setLow(f);
		
		try {
			intval = Integer.parseInt(paramsplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		f = CCMovieOnlineScore.find(intval);
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

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomOnlinescoreFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomOnlinescoreFilter();
	}
}
