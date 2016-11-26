package de.jClipCorn.database.util.iterators;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.stream.CCStream;

public class DirectEpisodesIterator extends CCStream<CCEpisode> {
	private CCSeries series;
	
	private boolean active = true;

	private int posCurr_season = -1;
	private int posCurr_episode = -1;
	
	public DirectEpisodesIterator(CCSeries _series) {
		series = _series;
		
		skipToNextSeason();
	}

	@Override
	public boolean hasNext() {
		return active;
	}

	@Override
	public CCEpisode next() {
		CCSeason sea = series.getSeasonByArrayIndex(posCurr_season);
		CCEpisode ep = sea.getEpisodeByArrayIndex(posCurr_episode);
		
		skipToNextEpisode();
		
		return ep;
	}

	private void skipToNextEpisode() {
		CCSeason sea = series.getSeasonByArrayIndex(posCurr_season);
		
		posCurr_episode++;
		
		if (sea.getEpisodeCount() <= posCurr_episode) {
			skipToNextSeason();
		}
	}

	private void skipToNextSeason() {
		posCurr_episode = 0;
		
		do {
			posCurr_season++;
		} while (posCurr_season < series.getSeasonCount() && series.getSeasonByArrayIndex(posCurr_season).getEpisodeCount() == 0);
		
		if (series.getSeasonCount() <= posCurr_season) {
			active = false;
		}
	}

	@Override
	protected CCStream<CCEpisode> cloneFresh() {
		return new DirectEpisodesIterator(series);
	}
}
