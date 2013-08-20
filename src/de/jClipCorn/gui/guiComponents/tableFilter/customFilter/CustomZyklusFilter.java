package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.StringMatchType;

public class CustomZyklusFilter extends AbstractCustomFilter {
	private String searchString = ""; //$NON-NLS-1$
	
	private StringMatchType stringMatch = StringMatchType.SM_INCLUDES;
	private boolean caseSensitive = true;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		String zyklus = ((CCMovieZyklus)e.getValue(ClipTableModel.COLUMN_ZYKLUS)).getTitle();
		
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
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Zyklus", getSearchString()); //$NON-NLS-1$
	}
}
