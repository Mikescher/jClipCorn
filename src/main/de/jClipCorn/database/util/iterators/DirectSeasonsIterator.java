package de.jClipCorn.database.util.iterators;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

public class DirectSeasonsIterator extends CCSimpleStream<CCSeason> {
	private CCSeries series;
	
	private int posCurr_season = 0;
	
	public DirectSeasonsIterator(CCSeries _series) {
		series = _series;
	}

	@Override
	public CCSeason nextOrNothing(boolean first) {
		if (posCurr_season >= series.getSeasonCount()) return finishStream();
		
		return series.getSeasonByArrayIndex(posCurr_season++);
	}

	@Override
	protected CCStream<CCSeason> cloneFresh() {
		return new DirectSeasonsIterator(series);
	}
}
