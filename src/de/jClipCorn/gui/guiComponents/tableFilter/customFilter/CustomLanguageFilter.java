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
}
