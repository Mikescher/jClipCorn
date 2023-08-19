package de.jClipCorn.features.statistics;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.util.stream.CCStream;

public enum StatisticsTypeFilter {
	STF_MOVIES,
	STF_SERIES,
	STF_SEASONS,
	STF_EPISODES,
	STF_MOVIES_AND_SERIES,
	STF_MOVIES_AND_SEASONS,
	STF_MOVIES_AND_EPISODES,
	STF_ALL;

	public CCStream<ICCDatabaseStructureElement> iterator(CCMovieList movielist) {
		switch (this)
		{
			case STF_MOVIES:              return movielist.iteratorMovies().cast();
			case STF_SERIES:              return movielist.iteratorSeries().cast();
			case STF_SEASONS:             return movielist.iteratorSeasons().cast();
			case STF_EPISODES:            return movielist.iteratorEpisodes().cast();
			case STF_MOVIES_AND_SERIES:   return movielist.iteratorElements().cast();
			case STF_MOVIES_AND_SEASONS:  return movielist.iteratorDatedElements().cast();
			case STF_MOVIES_AND_EPISODES: return movielist.iteratorPlayables().cast();
			case STF_ALL:                 return movielist.iteratorStructureElements().cast();
			default:                      throw new Error("invalid enum");
		}
	}
}
