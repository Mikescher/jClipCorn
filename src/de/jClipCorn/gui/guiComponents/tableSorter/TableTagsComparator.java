package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;

public class TableTagsComparator implements Comparator<CCMovieTags> {
	@Override
	public int compare(CCMovieTags o1, CCMovieTags o2) {
		return CCMovieTags.compare(o1, o2);
	}
}
