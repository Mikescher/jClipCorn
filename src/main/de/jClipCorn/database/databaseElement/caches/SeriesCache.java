package de.jClipCorn.database.databaseElement.caches;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeries;

@SuppressWarnings("HardCodedStringLiteral")
public class SeriesCache extends CalculationCache<CCSeries>
{
	public static final String IS_UNVIEWED                              = "IS_UNVIEWED";
	public static final String IS_VIEWED                                = "IS_VIEWED";
	public static final String EPISODE_COUNT                            = "EPISODE_COUNT";
	public static final String LENGTH                                   = "LENGTH";
	public static final String MAXIMUM_ADDDATE_BY_SEASON                = "MAXIMUM_ADDDATE_BY_SEASON";
	public static final String MINIMUM_ADDDATE                          = "MINIMUM_ADDDATE";
	public static final String MAXIMUM_ADDDATE                          = "MAXIMUM_ADDDATE";
	public static final String AVERAGE_ADDDATE                          = "AVERAGE_ADDDATE";
	public static final String FORMAT                                   = "FORMAT";
	public static final String FILESIZE                                 = "FILESIZE";
	public static final String YEAR_RANGE                               = "YEAR_RANGE";
	public static final String VIEWED_COUNT                             = "VIEWED_COUNT";
	public static final String COMMON_PATH_START                        = "COMMON_PATH_START";
	public static final String SERIES_ROOT_PATH                         = "SERIES_ROOT_PATH";
	public static final String FULL_VIEW_COUNT                          = "FULL_VIEW_COUNT";
	public static final String ALL_LANGUAGES                            = "ALL_LANGUAGES";
	public static final String COMMON_LANGUAGES                         = "COMMON_LANGUAGES";
	public static final String SEMI_COMMON_LANGUAGES                    = "SEMI_COMMON_LANGUAGES";
	public static final String ALL_SUBTITLES                            = "ALL_SUBTITLES";
	public static final String FOLDER_NAME_FOR_CREATED_FOLDER_STRUCTURE = "FOLDER_NAME_FOR_CREATED_FOLDER_STRUCTURE";
	public static final String AUTO_EPISODE_LENGTH                      = "AUTO_EPISODE_LENGTH";
	public static final String COMMON_EPISODE_LENGTH                    = "COMMON_EPISODE_LENGTH";
	public static final String AVERAGE_EPISODE_LENGTH                   = "AVERAGE_EPISODE_LENGTH";
	public static final String CONSENS_EPISODE_LENGTH                   = "CONSENS_EPISODE_LENGTH";
	public static final String MEDIAINFO_CATEGORY                       = "MEDIAINFO_CATEGORY";
	public static final String MEDIAINFO_LENGTH                         = "MEDIAINFO_LENGTH";
	public static final String LAST_VIEWED                              = "LAST_VIEWED";
	public static final String IS_EMPTY                                 = "IS_EMPTY";
	public static final String MAX_EPISODE_NUMBER                       = "MAX_EPISODE_NUMBER";

	private final CCSeries source;

	public SeriesCache(CCSeries source)
	{
		this.source = source;
	}

	@Override
	protected CCSeries getSource()
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
