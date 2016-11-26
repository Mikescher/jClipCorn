package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
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
}
