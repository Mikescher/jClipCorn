package de.jClipCorn.features.databaseErrors;

public class DatabaseValidatorOptions {
	public boolean ValidateMovies;
	public boolean ValidateSeries;
	public boolean ValidateSeasons;
	public boolean ValidateEpisodes;

	public boolean ValidateCovers;
	public boolean ValidateCoverFiles;
	public boolean ValidateVideoFiles;
	public boolean ValidateGroups;
	public boolean ValidateOnlineReferences;

	public boolean ValidateDuplicateFilesByPath;
	public boolean ValidateDuplicateFilesByMediaInfo;
	public boolean ValidateDatabaseConsistence;
	public boolean ValidateSeriesStructure;
	public boolean FindEmptyDirectories;

	public boolean IgnoreDuplicateIfos;

	public DatabaseValidatorOptions() { }
}
