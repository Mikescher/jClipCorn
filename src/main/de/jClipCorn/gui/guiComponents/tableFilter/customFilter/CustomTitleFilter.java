package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs.CustomTitleFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.StringMatchType;
import de.jClipCorn.util.listener.FinishListener;

public class CustomTitleFilter extends AbstractCustomDatabaseElementFilter {
	private String searchString = ""; //$NON-NLS-1$
	
	private StringMatchType stringMatch = StringMatchType.SM_INCLUDES;
	private boolean caseSensitive = true;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		String title = e.getTitle();
		
		String search = searchString;
		
		if (caseSensitive) {
			title = title.toLowerCase();
			search = search.toLowerCase();
		}
		
		switch (stringMatch) {
		case SM_STARTSWITH:
			return title.startsWith(search);
		case SM_INCLUDES:
			return title.indexOf(search) != -1;
		case SM_ENDSWITH:
			return title.endsWith(search);
		case SM_EQUALS:
			return title.equals(search);
		}
		
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
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Title", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Title").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		}
		
		return "?"; //$NON-NLS-1$
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_TITLE;
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
		return new CustomTitleFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomTitleFilter();
	}
}
