package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.util.YearRange;

public class TableSearchFilter extends RowFilter<ClipTableModel, Object> {

	private final String searchTerm;
	
	public TableSearchFilter(String term) {
		super();
		this.searchTerm = term;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		String title = ((CCDatabaseElement)e.getValue(1)).getTitle();
		
		if (StringUtils.containsIgnoreCase(title, searchTerm)) {
			return true;
		}
		
		String zyklus = ((CCMovieZyklus)e.getValue(3)).getTitle();
		if (StringUtils.containsIgnoreCase(zyklus, searchTerm)) {
			return true;
		}
		
		if (e.getValue(4) instanceof CCMovieQuality) {
			if (((CCMovieQuality)e.getValue(4)).asString().equalsIgnoreCase(searchTerm)) {
				return true;
			}
		} else {
			if (((CCMovie)e.getValue(4)).getQuality().asString().equalsIgnoreCase(searchTerm)) {
				return true;
			}
		}
		
		
		if (((CCMovieLanguage)e.getValue(5)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (((CCMovieFormat)e.getValue(12)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		CCMovieGenreList gl = (CCMovieGenreList) e.getValue(6);
		for (int i = 0; i < gl.getGenreCount(); i++) {
			if (gl.getGenre(i).asString().equalsIgnoreCase(searchTerm)) {
				return true;
			}
		}
		
		if (((YearRange)e.getValue(13)).asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		return false;
	}
}
