package de.jClipCorn.database.util.iterators;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

public class DirectEpisodesIterator extends CCSimpleStream<CCEpisode> {
	private CCSeries series;
	
	private int posCurr_season = 0;
	private int posCurr_episode = -1;
	
	public DirectEpisodesIterator(CCSeries _series) {
		series = _series;
	}

	@Override
	public CCEpisode nextOrNothing(boolean first) {
		for (;;) {
			posCurr_episode++;
			
			if (posCurr_season >= series.getSeasonCount()) 
				return finishStream();
			
			if (posCurr_episode < series.getSeasonByArrayIndex(posCurr_season).getEpisodeCount()) 
				return series.getSeasonByArrayIndex(posCurr_season).getEpisodeByArrayIndex(posCurr_episode);
			
			posCurr_season++;
			posCurr_episode = -1;
		}
	}

	@Override
	protected CCStream<CCEpisode> cloneFresh() {
		return new DirectEpisodesIterator(series);
	}
}
