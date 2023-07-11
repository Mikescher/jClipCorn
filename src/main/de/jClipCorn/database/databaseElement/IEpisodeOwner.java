package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.elementProps.impl.EStringProp;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.stream.CCStream;

public interface IEpisodeOwner {
	CCStream<CCEpisode> iteratorEpisodes();

	CCSeries getSeries();

	CCMovieList getMovieList();

	Opt<Integer> getCommonEpisodeLength();
	Opt<Integer> getConsensEpisodeLength();
	Opt<Integer> getAverageEpisodeLength();

	EStringProp title();
}
