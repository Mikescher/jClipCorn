package de.jClipCorn.database.databaseElement.caches;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;

public class EpisodeCache extends CalculationCache<CCEpisode>
{
	private final CCEpisode source;

	public EpisodeCache(CCEpisode source)
	{
		this.source = source;
	}

	@Override
	protected CCEpisode getSource()
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
		return source.getSeason().getCache();
	}
}
