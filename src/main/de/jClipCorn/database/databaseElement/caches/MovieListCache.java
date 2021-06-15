package de.jClipCorn.database.databaseElement.caches;

import de.jClipCorn.database.CCMovieList;

@SuppressWarnings("HardCodedStringLiteral")
public class MovieListCache extends CalculationCache<CCMovieList>
{
	public static final String TOTAL_DATABASE_COUNT         = "TOTAL_DATABASE_COUNT";
	public static final String TOTAL_DATABASE_ELEMENT_COUNT = "TOTAL_DATABASE_ELEMENT_COUNT";
	public static final String VIEWED_COUNT                 = "VIEWED_COUNT";
	public static final String TOTAL_LENGTH                 = "TOTAL_LENGTH";
	public static final String TOTAL_SIZE                   = "TOTAL_SIZE";
	public static final String MOVIE_COUNT                  = "MOVIE_COUNT";
	public static final String SERIES_COUNT                 = "SERIES_COUNT";
	public static final String EPISODE_COUNT                = "EPISODE_COUNT";
	public static final String SEASON_COUNT                 = "SEASON_COUNT";
	public static final String ZYKLUS_LIST                  = "ZYKLUS_LIST";
	public static final String YEAR_LIST                    = "YEAR_LIST";
	public static final String GENRE_LIST                   = "GENRE_LIST";
	public static final String COMMON_SERIES_PATH           = "COMMON_SERIES_PATH";
	public static final String GUESS_SERIES_ROOT_PATH       = "GUESS_SERIES_ROOT_PATH";
	public static final String COMMON_MOVIES_PATH           = "COMMON_MOVIES_PATH";
	public static final String HAS_MOVIES                   = "HAS_MOVIES";
	public static final String HAS_SERIES                   = "HAS_SERIES";

	private final CCMovieList source;

	public MovieListCache(CCMovieList source)
	{
		this.source = source;
	}

	@Override
	protected CCMovieList getSource()
	{
		return source;
	}

	@Override
	protected CCMovieList getMovieList() {
		return source;
	}

	@Override
	protected ICalculationCache getOwnerCache()
	{
		return null;
	}
}
