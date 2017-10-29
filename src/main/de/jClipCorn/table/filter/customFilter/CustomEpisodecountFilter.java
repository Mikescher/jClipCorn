package de.jClipCorn.table.filter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilterDialogs.CustomEpisodecountFilterDialog;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.listener.FinishListener;

public class CustomEpisodecountFilter extends AbstractCustomStructureElementFilter {
	private int low = 1;
	private int high = 1;
	private DecimalSearchType searchType = DecimalSearchType.EXACT;
	
	@Override
	public boolean includes(CCMovie m) {
		return false;
	}

	@Override
	public boolean includes(CCSeries s) {
		double c = s.getEpisodeCount();

		switch (searchType) {
		case LESSER:
			return c < high;
		case GREATER:
			return c > low;
		case IN_RANGE:
			return c > low && c < high;
		case EXACT:
			return Math.round(c) == low;
		default:
			return false;
		}
	}

	@Override
	public boolean includes(CCSeason s) {
		double c = s.getEpisodeCount();

		switch (searchType) {
		case LESSER:
			return c < high;
		case GREATER:
			return c > low;
		case IN_RANGE:
			return c > low && c < high;
		case EXACT:
			return Math.round(c) == low;
		default:
			return false;
		}
	}

	@Override
	public boolean includes(CCEpisode e) {
		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Episodecount", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Episodecount").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		return AbstractCustomFilter.CUSTOMFILTERID_EPISODECOUNT;
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
		setLow(intval);
		
		try {
			intval = Integer.parseInt(paramsplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		setHigh(intval);
		
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
		return new CustomEpisodecountFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomEpisodecountFilter();
	}

	public static CustomEpisodecountFilter create(Integer data) {
		CustomEpisodecountFilter f = new CustomEpisodecountFilter();
		f.setLow(data);
		f.setSearchType(DecimalSearchType.EXACT);
		return f;
	}
}
