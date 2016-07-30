package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;

public class TableLanguageComparator implements Comparator<CCMovieLanguage>{
	@Override
	public int compare(CCMovieLanguage o1, CCMovieLanguage o2) {
		return CCMovieLanguage.compare(o1, o2);
	}
}