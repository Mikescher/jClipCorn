package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomLanguageFilter extends AbstractCustomFilter {
	private CCMovieLanguage language = CCMovieLanguage.GERMAN;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return language.equals(e.getValue(ClipTableModel.COLUMN_LANGUAGE));
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Language", language); //$NON-NLS-1$
	}

	public CCMovieLanguage getLanguage() {
		return language;
	}

	public void setLanguage(CCMovieLanguage language) {
		this.language = language;
	}
	
	@Override
	public int getID() {
		return 7;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(language.asInt()+"");
		b.append("]");
		
		return b.toString();
	}
	
	@Override
	public boolean importFromString(String txt) {
		
	}
}
