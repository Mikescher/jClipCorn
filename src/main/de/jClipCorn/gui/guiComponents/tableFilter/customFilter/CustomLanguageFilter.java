package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs.CustomFilterDialog;
import de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs.CustomLanguageFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

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

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Language").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public CCMovieLanguage getLanguage() {
		return language;
	}

	public void setLanguage(CCMovieLanguage language) {
		this.language = language;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_LANGUAGE;
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
	
	@SuppressWarnings("nls")
	@Override
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = params.split(Pattern.quote(","));
		if (paramsplit.length != 1) return false;
		
		int intval;
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		
		CCMovieLanguage f = CCMovieLanguage.find(intval);
		if (f == null) return false;
		setLanguage(f);
		
		return true;
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomLanguageFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomLanguageFilter();
	}
}
