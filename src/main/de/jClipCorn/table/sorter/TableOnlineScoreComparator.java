package de.jClipCorn.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;

public class TableOnlineScoreComparator implements Comparator<CCOnlineScore>{
	@Override
	public int compare(CCOnlineScore o1, CCOnlineScore o2) {
		return CCOnlineScore.compare(o1, o2);
	}
}
