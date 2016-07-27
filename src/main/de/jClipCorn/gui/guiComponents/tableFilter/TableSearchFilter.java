package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.YearRange;

public class TableSearchFilter extends RowFilter<ClipTableModel, Object> {
	private final String searchTerm;
	
	public TableSearchFilter(String term) {
		super();
		this.searchTerm = term;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		CCDatabaseElement elem = ((CCDatabaseElement)e.getValue(ClipTableModel.COLUMN_TITLE));
		
		String title = elem.getTitle();
		
		if (StringUtils.containsIgnoreCase(title, searchTerm)) {
			return true;
		}
		
		String zyklus = ((CCMovieZyklus)e.getValue(ClipTableModel.COLUMN_ZYKLUS)).getTitle();
		if (StringUtils.containsIgnoreCase(zyklus, searchTerm)) {
			return true;
		}
		
		if (((CCMovieQuality)e.getValue(ClipTableModel.COLUMN_QUALITY)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (((CCMovieLanguage)e.getValue(ClipTableModel.COLUMN_LANGUAGE)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (((CCMovieFormat)e.getValue(ClipTableModel.COLUMN_FORMAT)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (((CCDate)e.getValue(ClipTableModel.COLUMN_DATE)).getSimpleStringRepresentation().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		CCMovieGenreList gl = (CCMovieGenreList) e.getValue(ClipTableModel.COLUMN_GENRE);
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
}
