package de.jClipCorn.table.filter.customFilter;

import java.awt.Component;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.AbstractCustomMovieOrSeriesFilter;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilterDialogs.CustomZyklusFilterDialog;
import de.jClipCorn.util.datatypes.StringMatchType;
import de.jClipCorn.util.listener.FinishListener;

public class CustomZyklusFilter extends AbstractCustomMovieOrSeriesFilter {
	private String searchString = ""; //$NON-NLS-1$
	
	private StringMatchType stringMatch = StringMatchType.SM_INCLUDES;
	private boolean caseSensitive = true;
	
	@Override
	public boolean includes(CCMovie m) {
		String zyklus = m.getZyklus().getTitle();
		
		String search = searchString;
		
		if (caseSensitive) {
			zyklus = zyklus.toLowerCase();
			search = search.toLowerCase();
		}
		
		switch (stringMatch) {
		case SM_STARTSWITH:
			return zyklus.startsWith(search);
		case SM_INCLUDES:
			return zyklus.indexOf(search) != -1;
		case SM_ENDSWITH:
			return zyklus.endsWith(search);
		case SM_EQUALS:
			return zyklus.equals(search);
		default:
			break;
		}
		
		return false;
	}

	@Override
	public boolean includes(CCSeries m) {
		return false;
	}

	@Override
	public boolean includes(CCSeason m) {
		return false;
	}

	public StringMatchType getStringMatch() {
		return stringMatch;
	}

	public void setStringMatch(StringMatchType stringMatch) {
		this.stringMatch = stringMatch;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Zyklus", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Zyklus").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		String appendix = (caseSensitive) ? (" [Xx]") : (" [XX]"); //$NON-NLS-1$ //$NON-NLS-2$
		
		switch (stringMatch) {
		case SM_STARTSWITH:
			return getSearchString() + " ***" + appendix; //$NON-NLS-1$
		case SM_INCLUDES:
			return "*** " + getSearchString() + " ***" + appendix; //$NON-NLS-1$ //$NON-NLS-2$
		case SM_ENDSWITH:
			return "*** " + getSearchString() + appendix; //$NON-NLS-1$
		case SM_EQUALS:
			return "== " + getSearchString() + appendix; //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ZYKLUS;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(AbstractCustomFilter.escape(searchString));
		b.append(",");
		b.append(stringMatch.asInt()+"");
		b.append(",");
		b.append((caseSensitive)?("1"):("0"));
		b.append("]");
		
		return b.toString();
	}
	
	@Override
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = AbstractCustomFilter.splitParameterFromExport(params);
		if (paramsplit.length != 3) return false;
		
		int intval;
		StringMatchType s;
		
		setSearchString(AbstractCustomFilter.descape(paramsplit[0]));
		
		try {
			intval = Integer.parseInt(paramsplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		s = StringMatchType.getWrapper().find(intval);
		if (s == null) return false;
		setStringMatch(s);
		
		try {
			intval = Integer.parseInt(paramsplit[2]);
		} catch (NumberFormatException e) {
			return false;
		}
		
		boolean f;
		if (intval == 0) f = false;
		else if (intval == 1) f = true;
		else return false;
		
		setCaseSensitive(f);
		
		return true;
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomZyklusFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomZyklusFilter();
	}

	public static AbstractCustomFilter create(String data) {
		CustomZyklusFilter f = new CustomZyklusFilter();
		f.setSearchString(data);
		f.setCaseSensitive(true);
		f.setStringMatch(StringMatchType.SM_EQUALS);
		return f;
	}

	public static CustomZyklusFilter create(CCMovieZyklus data) {
		CustomZyklusFilter f = new CustomZyklusFilter();
		f.setSearchString(data.getTitle());
		f.setCaseSensitive(true);
		f.setStringMatch(StringMatchType.SM_EQUALS);
		return f;
	}
}
