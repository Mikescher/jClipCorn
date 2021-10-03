package de.jClipCorn.features.table.filter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;

public abstract class AbstractCustomMovieOrSeriesFilter extends AbstractCustomDatabaseElementFilter {

	public AbstractCustomMovieOrSeriesFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCDatabaseElement e) {
		if (e.isMovie()) 
			return includes(e.asMovie());
		else 
			return includes(e.asSeries());
	}

	public abstract boolean includes(CCMovie m);
	public abstract boolean includes(CCSeries s);
}
