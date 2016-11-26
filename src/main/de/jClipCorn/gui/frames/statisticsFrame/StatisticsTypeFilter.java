package de.jClipCorn.gui.frames.statisticsFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.ICCDatedElement;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.stream.CCStream;

public enum StatisticsTypeFilter {
	MOVIES,
	SERIES,
	BOTH;
	
	public boolean containsMovies() {
		return this == MOVIES || this == BOTH;
	}
	
	public boolean containsSeries() {
		return this == SERIES || this == BOTH;
	}
	
	public boolean containsBoth() {
		return this == BOTH;
	}

	public boolean contains(StatisticsTypeFilter source) {
		switch (source) {
			case MOVIES: return containsMovies();
			case SERIES: return containsSeries();
			case BOTH: return containsBoth();
			
			default:
				CCLog.addDefaultSwitchError(this, source);
				return false;
		}
	}
	
	public CCStream<ICCPlayableElement> iteratorMoviesOrEpisodes(CCMovieList movielist) {
		switch (this) {
			case MOVIES: 
				return movielist.iteratorMovies().cast();
			case SERIES: 
				return movielist.iteratorEpisodes().cast();
			case BOTH:   
				return movielist.iteratorPlayables().cast();
			default:
				CCLog.addDefaultSwitchError(this, this);
				return null;
		}
	}
	
	public CCStream<CCDatabaseElement> iteratorMoviesOrSeries(CCMovieList movielist) {
		switch (this) {
			case MOVIES: 
				return movielist.iteratorMovies().cast();
			case SERIES: 
				return movielist.iteratorSeries().cast();
			case BOTH:   
				return movielist.iteratorElements().cast();
			default:
				CCLog.addDefaultSwitchError(this, this);
				return null;
		}
	}

	public CCStream<ICCDatedElement> iteratorMoviesOrSeason(CCMovieList movielist) {
		switch (this) {
			case MOVIES: 
				return movielist.iteratorMovies().cast();
			case SERIES: 
				return movielist.iteratorSeasons().cast();
			case BOTH:   
				return movielist.iteratorDatedElements().cast();
			default:
				CCLog.addDefaultSwitchError(this, this);
				return null;
		}
	}
}
