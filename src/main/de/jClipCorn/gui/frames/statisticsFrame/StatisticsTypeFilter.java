package de.jClipCorn.gui.frames.statisticsFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.ICCDatedElement;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.cciterator.CCIterator;

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
				CCLog.addDefaultSwitchError(this);
				return false;
		}
	}
	
	public CCIterator<ICCPlayableElement> iteratorMoviesOrEpisodes(CCMovieList movielist) {
		switch (this) {
			case MOVIES: 
				return movielist.iteratorMovies().asCasted();
			case SERIES: 
				return movielist.iteratorEpisodes().asCasted();
			case BOTH:   
				return movielist.iteratorPlayables().asCasted();
			default:
				CCLog.addDefaultSwitchError(this);
				return null;
		}
	}
	
	public CCIterator<CCDatabaseElement> iteratorMoviesOrSeries(CCMovieList movielist) {
		switch (this) {
			case MOVIES: 
				return movielist.iteratorMovies().asCasted();
			case SERIES: 
				return movielist.iteratorSeries().asCasted();
			case BOTH:   
				return movielist.iteratorElements().asCasted();
			default:
				CCLog.addDefaultSwitchError(this);
				return null;
		}
	}

	public CCIterator<ICCDatedElement> iteratorMoviesOrSeason(CCMovieList movielist) {
		switch (this) {
			case MOVIES: 
				return movielist.iteratorMovies().asCasted();
			case SERIES: 
				return movielist.iteratorSeasons().asCasted();
			case BOTH:   
				return movielist.iteratorDatedElements().asCasted();
			default:
				CCLog.addDefaultSwitchError(this);
				return null;
		}
	}
}
