package de.jClipCorn.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;

public class TableGenreComparator implements Comparator<CCGenreList>{
	@Override
	public int compare(CCGenreList o1, CCGenreList o2) {
		return CCGenreList.compare(o1, o2);
	}
}
