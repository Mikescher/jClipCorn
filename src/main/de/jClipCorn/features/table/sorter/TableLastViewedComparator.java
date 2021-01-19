package de.jClipCorn.features.table.sorter;

import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDateTime;

import java.util.Comparator;

public class TableLastViewedComparator implements Comparator<Opt<CCDateTime>>
{
	@Override
	public int compare(Opt<CCDateTime> o1, Opt<CCDateTime> o2)
	{
		if (!o1.isPresent() && !o2.isPresent()) return 0;

		if (!o1.isPresent()) return +1;
		if (!o2.isPresent()) return -1;

		return -1 * CCDateTime.compare(o1.get(), o2.get());
	}
}
