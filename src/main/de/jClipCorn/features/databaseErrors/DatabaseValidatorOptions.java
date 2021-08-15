package de.jClipCorn.features.databaseErrors;

public class DatabaseValidatorOptions {
	public final boolean ValidateMovies;
	public final boolean ValidateSeries;
	public final boolean ValidateSeasons;
	public final boolean ValidateEpisodes;

	public final boolean ValidateCovers;
	public final boolean ValidateCoverFiles;
	public final boolean ValidateVideoFiles;
	public final boolean ValidateGroups;
	public final boolean ValidateOnlineReferences;
	
	public final boolean ValidateAdditional;
	public final boolean ValidateDatabaseConsistence;


	public final boolean ValidateSeriesStructure;
	public final boolean IgnoreDuplicateIfos;


	public DatabaseValidatorOptions(
			boolean mov, boolean ser, boolean sea, boolean epi,
			boolean cvrs, boolean cvrFiles, boolean vidFiles, boolean grps, boolean orefs,
			boolean db, boolean addit,
			boolean serstruct, boolean ifodups)
	{
		ValidateMovies              = mov;
		ValidateSeries              = ser;
		ValidateSeasons             = sea;
		ValidateEpisodes            = epi;
		ValidateCovers              = cvrs;

		ValidateCoverFiles          = cvrFiles;
		ValidateVideoFiles          = vidFiles;
		ValidateGroups              = grps;
		ValidateOnlineReferences    = orefs;

		ValidateAdditional          = addit;
		ValidateDatabaseConsistence = db;

		ValidateSeriesStructure     = serstruct;
		IgnoreDuplicateIfos         = ifodups;
	}
}
