package de.jClipCorn.database.databaseElement.caches;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;

public class MovieCache extends CalculationCache<CCMovie>
{
	private final CCMovie source;

	public MovieCache(CCMovie source)
	{
		this.source = source;
	}

	@Override
	protected CCMovie getSource()
	{
		return source;
	}

	@Override
	protected CCMovieList getMovieList() {
		return source.getMovieList();
	}

	@Override
	protected ICalculationCache getOwnerCache()
	{
		return source.getMovieList().getCache();
	}
}
