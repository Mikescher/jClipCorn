package de.jClipCorn.gui.guiComponents.tableSorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;

public class TableFSKComparator implements Comparator<CCMovieFSK>{
	@Override
	public int compare(CCMovieFSK o1, CCMovieFSK o2) {
		return CCMovieFSK.compare(o1, o2);
	}
}
