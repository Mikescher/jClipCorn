package de.jClipCorn.features.table.sorter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.util.datetime.CCDate;

import java.util.Comparator;

public class TableDateComparator implements Comparator<CCDatabaseElement> {
	@Override
	public int compare(CCDatabaseElement o1, CCDatabaseElement o2)
	{
		var d1 = o1.getAddDate();
		var d2 = o2.getAddDate();

		if (o1.isSeries() && d1.isMinimum() && o1.asSeries().isEmpty()) d1 = CCDate.getMaximumDate();
		if (o2.isSeries() && d2.isMinimum() && o2.asSeries().isEmpty()) d2 = CCDate.getMaximumDate();

		return CCDate.compare(d1, d2);
	}
}
