package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs.CustomSearchFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.YearRange;
import de.jClipCorn.util.listener.FinishListener;

public class CustomSearchFilter extends AbstractCustomFilter {
	private String searchTerm = ""; //$NON-NLS-1$
	
	@Override
	@SuppressWarnings("nls")
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		CCDatabaseElement elem = ((CCDatabaseElement)e.getValue(ClipTableModel.COLUMN_TITLE));
		
		String title = elem.getTitle();
		
		if (StringUtils.containsIgnoreCase(title.replace(".", ""), searchTerm.replace(".", ""))) {
			return true;
		}
		
		String zyklus = ((CCMovieZyklus)e.getValue(ClipTableModel.COLUMN_ZYKLUS)).getTitle();
		if (StringUtils.containsIgnoreCase(zyklus.replace(".", ""), searchTerm.replace(".", ""))) {
			return true;
		}
		
		if (((CCQuality)e.getValue(ClipTableModel.COLUMN_QUALITY)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (((CCDBLanguage)e.getValue(ClipTableModel.COLUMN_LANGUAGE)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (((CCFileFormat)e.getValue(ClipTableModel.COLUMN_FORMAT)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (((CCDate)e.getValue(ClipTableModel.COLUMN_DATE)).toStringSerialize().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (((CCDate)e.getValue(ClipTableModel.COLUMN_DATE)).toStringSQL().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (((CCDate)e.getValue(ClipTableModel.COLUMN_DATE)).toStringUINormal().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		CCGenreList gl = (CCGenreList) e.getValue(ClipTableModel.COLUMN_GENRE);
		for (int i = 0; i < gl.getGenreCount(); i++) {
			if (gl.getGenre(i).asString().equalsIgnoreCase(searchTerm)) {
				return true;
			}
		}
		
		if (((YearRange)e.getValue(ClipTableModel.COLUMN_YEAR)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (elem.getGroups().containsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (elem.getOnlineReference().isSet() && (elem.getOnlineReference().toSerializationString().equals(searchTerm) || elem.getOnlineReference().id.equals(searchTerm))) {
			return true;
		}
		
		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Search", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Search").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		return searchTerm;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_SEARCH;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(AbstractCustomFilter.escape(searchTerm));
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

		setSearchTerm(AbstractCustomFilter.descape(paramsplit[0]));
		
		return true;
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomSearchFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomSearchFilter();
	}

	public static AbstractCustomFilter create(String data) {
		CustomSearchFilter f = new CustomSearchFilter();
		f.setSearchTerm(data);
		return f;
	}
}
