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
	public boolean ValidateNfoFiles;

	// When false (default) the NFO-content and poster checks only compare filesizes (cheap, no file reads).
	// When true the full checks are performed (read+compare NFO text, read+SHA256 the poster file) - much slower over network shares.
	public boolean ValidateNfoFullComparison;

	public boolean IgnoreDuplicateIfos;

	public DatabaseValidatorOptions() { }
}
