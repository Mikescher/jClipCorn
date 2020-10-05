package de.jClipCorn.database.databaseElement.caches;

import de.jClipCorn.database.databaseElement.CCSeason;

@SuppressWarnings("HardCodedStringLiteral")
public class SeasonCache extends CalculationCache<CCSeason>
{
	public static final String IS_VIEWED                     = "IS_VIEWED";
	public static final String IS_UNVIEWED                   = "IS_UNVIEWED";
	public static final String TAGS                          = "TAGS";
	public static final String LENGTH                        = "LENGTH";
	public static final String MAXIMUM_ADDDATE               = "MAXIMUM_ADDDATE";
	public static final String MINIMUM_ADDDATE               = "MINIMUM_ADDDATE";
	public static final String AVERAGE_ADDDATE               = "AVERAGE_ADDDATE";
	public static final String FORMAT                        = "FORMAT";
	public static final String FILESIZE                      = "FILESIZE";
	public static final String VIEWED_COUNT                  = "VIEWED_COUNT";
	public static final String COMMON_EPISODE_LENGTH         = "COMMON_EPISODE_LENGTH";
	public static final String AVERAGE_EPISODE_LENGTH        = "AVERAGE_EPISODE_LENGTH";
	public static final String CONSENS_EPISODE_LENGTH        = "CONSENS_EPISODE_LENGTH";
	public static final String NEXT_EPISODE_NUMBER           = "NEXT_EPISODE_NUMBER";
	public static final String COMMON_PATH_START             = "COMMON_PATH_START";
	public static final String FIRST_EPISODE_NUMBER          = "FIRST_EPISODE_NUMBER";
	public static final String LAST_EPISODE_NUMBER           = "LAST_EPISODE_NUMBER";
	public static final String IS_CONTINOOUS_EPISODE_NUMBERS = "IS_CONTINOOUS_EPISODE_NUMBERS";
	public static final String FULL_VIEW_COUNT               = "FULL_VIEW_COUNT";
	public static final String MEDIAINFO_CATEGORY            = "MEDIAINFO_CATEGORY";

	private final CCSeason source;

	public SeasonCache(CCSeason source)
	{
		this.source = source;
	}

	@Override
	protected CCSeason getSource()
	{
		return source;
	}

	@Override
	protected ICalculationCache getOwnerCache()
	{
		return source.getSeries().getCache();
	}
}
