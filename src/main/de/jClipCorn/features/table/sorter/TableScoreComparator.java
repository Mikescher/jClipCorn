package de.jClipCorn.features.table.sorter;

import java.util.Comparator;

import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;

public class TableScoreComparator implements Comparator<CCUserScore> {
	@Override
	public int compare(CCUserScore first, CCUserScore last) {
		return CCUserScore.compare(first, last);
	}
}
