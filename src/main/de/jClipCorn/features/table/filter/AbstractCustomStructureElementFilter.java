package de.jClipCorn.features.table.filter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;

public abstract class AbstractCustomStructureElementFilter extends AbstractCustomFilter {

	public AbstractCustomStructureElementFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		if (e instanceof CCMovie) 
			return includes((CCMovie)e); 
		else if (e instanceof CCSeries) 
			return includes((CCSeries)e); 
		else if (e instanceof CCEpisode) 
			return includes((CCEpisode)e); 
		else if (e instanceof CCSeason) 
			return includes((CCSeason)e);
		else
			return false;
	}

	public boolean includes(CCMovie m) { 
		return false; 
	}
	
	public boolean includes(CCSeries s) { 
		return false; 
	}
	
	public boolean includes(CCEpisode s) { 
		return false; 
	}
	
	public boolean includes(CCSeason s) { 
		return false; 
	}
	
}
