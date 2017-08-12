package de.jClipCorn.gui.guiComponents.tableFilter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;

public abstract class AbstractCustomMovieOrSeriesFilter extends AbstractCustomDatabaseElementFilter {

	@Override
	public boolean includes(CCDatabaseElement e) {
		if (e.isMovie()) 
			return includes((CCMovie)e); 
		else 
			return includes((CCSeries)e); 
	}

	public abstract boolean includes(CCMovie m);
	public abstract boolean includes(CCSeries s);
}
