package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs.CustomUserScoreFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.listener.FinishListener;

public class CustomUserScoreFilter extends AbstractCustomDatabaseElementFilter {
	private CCUserScore low = CCUserScore.RATING_0;
	private CCUserScore high = CCUserScore.RATING_0;
	private DecimalSearchType searchType = DecimalSearchType.EXACT;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		CCUserScore sco = e.getScore();
		
		switch (searchType) {
		case LESSER:
			return sco != CCUserScore.RATING_NO && sco.asInt() < high.asInt();
		case GREATER:
			return sco != CCUserScore.RATING_NO && low.asInt() < sco.asInt();
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

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Score").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

	public CCUserScore getHigh() {
		return high;
	}

	public void setHigh(CCUserScore high) {
		this.high = high;
	}

	public CCUserScore getLow() {
		return low;
	}

	public void setLow(CCUserScore low) {
		this.low = low;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_USERSCORE;
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
		CCUserScore f;
		DecimalSearchType s;
		
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		f = CCUserScore.getWrapper().find(intval);
		if (f == null) return false;
		setLow(f);
		
		try {
			intval = Integer.parseInt(paramsplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		f = CCUserScore.getWrapper().find(intval);
		if (f == null) return false;
		setHigh(f);
		
		try {
			intval = Integer.parseInt(paramsplit[2]);
		} catch (NumberFormatException e) {
			return false;
		}
		s = DecimalSearchType.getWrapper().find(intval);
		if (s == null) return false;
		setSearchType(s);
		
		return true;
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomUserScoreFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomUserScoreFilter();
	}

	public static CustomUserScoreFilter create(CCUserScore data) {
		CustomUserScoreFilter f = new CustomUserScoreFilter();
		f.setSearchType(DecimalSearchType.EXACT);
		f.setLow(data);
		return f;
	}
}
