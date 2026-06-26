package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.util.Str;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("nls")
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

	public static final String TOKEN_MOVIES           = "MOVIES";
	public static final String TOKEN_SERIES           = "SERIES";
	public static final String TOKEN_SEASONS          = "SEASONS";
	public static final String TOKEN_EPISODES         = "EPISODES";
	public static final String TOKEN_COVERS           = "COVERS";
	public static final String TOKEN_GROUPS           = "GROUPS";
	public static final String TOKEN_ONLINEREFS       = "ONLINEREFS";
	public static final String TOKEN_COVER_FILES      = "COVER_FILES";
	public static final String TOKEN_VIDEO_FILES      = "VIDEO_FILES";
	public static final String TOKEN_DATABASE         = "DATABASE";
	public static final String TOKEN_DUPLICATES_PATH  = "DUPLICATES_PATH";
	public static final String TOKEN_DUPLICATES_MI    = "DUPLICATES_MI";
	public static final String TOKEN_SERIES_STRUCTURE = "SERIES_STRUCTURE";
	public static final String TOKEN_EMPTY_DIRS       = "EMPTY_DIRS";
	public static final String TOKEN_NFO_FILES        = "NFO_FILES";
	public static final String TOKEN_NFO_FULL_COMPARE = "NFO_FULL_COMPARE";

	public Set<String> serialize() {
		var v = new HashSet<String>();
		if (ValidateMovies)                    v.add(TOKEN_MOVIES);
		if (ValidateSeries)                    v.add(TOKEN_SERIES);
		if (ValidateSeasons)                   v.add(TOKEN_SEASONS);
		if (ValidateEpisodes)                  v.add(TOKEN_EPISODES);
		if (ValidateCovers)                    v.add(TOKEN_COVERS);
		if (ValidateGroups)                    v.add(TOKEN_GROUPS);
		if (ValidateOnlineReferences)          v.add(TOKEN_ONLINEREFS);
		if (ValidateCoverFiles)                v.add(TOKEN_COVER_FILES);
		if (ValidateVideoFiles)                v.add(TOKEN_VIDEO_FILES);
		if (ValidateDatabaseConsistence)       v.add(TOKEN_DATABASE);
		if (ValidateDuplicateFilesByPath)      v.add(TOKEN_DUPLICATES_PATH);
		if (ValidateDuplicateFilesByMediaInfo) v.add(TOKEN_DUPLICATES_MI);
		if (ValidateSeriesStructure)           v.add(TOKEN_SERIES_STRUCTURE);
		if (FindEmptyDirectories)              v.add(TOKEN_EMPTY_DIRS);
		if (ValidateNfoFiles)                  v.add(TOKEN_NFO_FILES);
		if (ValidateNfoFullComparison)         v.add(TOKEN_NFO_FULL_COMPARE);
		return v;
	}

	public static DatabaseValidatorOptions deserialize(Collection<String> rawTokens) {
		var tokens = new HashSet<String>();
		for (String t : rawTokens) if (!Str.isNullOrWhitespace(t)) tokens.add(t.trim().toUpperCase());

		var o = new DatabaseValidatorOptions();
		o.ValidateMovies                    = tokens.contains(TOKEN_MOVIES);
		o.ValidateSeries                    = tokens.contains(TOKEN_SERIES);
		o.ValidateSeasons                   = tokens.contains(TOKEN_SEASONS);
		o.ValidateEpisodes                  = tokens.contains(TOKEN_EPISODES);
		o.ValidateCovers                    = tokens.contains(TOKEN_COVERS);
		o.ValidateGroups                    = tokens.contains(TOKEN_GROUPS);
		o.ValidateOnlineReferences          = tokens.contains(TOKEN_ONLINEREFS);
		o.ValidateCoverFiles                = tokens.contains(TOKEN_COVER_FILES);
		o.ValidateVideoFiles                = tokens.contains(TOKEN_VIDEO_FILES);
		o.ValidateDatabaseConsistence       = tokens.contains(TOKEN_DATABASE);
		o.ValidateDuplicateFilesByPath      = tokens.contains(TOKEN_DUPLICATES_PATH);
		o.ValidateDuplicateFilesByMediaInfo = tokens.contains(TOKEN_DUPLICATES_MI);
		o.ValidateSeriesStructure           = tokens.contains(TOKEN_SERIES_STRUCTURE);
		o.FindEmptyDirectories              = tokens.contains(TOKEN_EMPTY_DIRS);
		o.ValidateNfoFiles                  = tokens.contains(TOKEN_NFO_FILES);
		o.ValidateNfoFullComparison         = tokens.contains(TOKEN_NFO_FULL_COMPARE);
		return o;
	}

	public static DatabaseValidatorOptions parse(String spec) {
		if (Str.isNullOrWhitespace(spec)) return presetDefault();

		String s = spec.trim();
		if (s.equalsIgnoreCase("full"))    return presetFull();
		if (s.equalsIgnoreCase("default")) return presetDefault();
		if (s.equalsIgnoreCase("quick"))   return presetQuick();

		List<String> tokens = new ArrayList<>();
		for (String t : s.split("[,;\\s]+")) if (!Str.isNullOrWhitespace(t)) tokens.add(t);
		return deserialize(tokens);
	}

	public static DatabaseValidatorOptions presetFull() {
		var o = new DatabaseValidatorOptions();
		o.ValidateMovies                    = true;
		o.ValidateSeries                    = true;
		o.ValidateSeasons                   = true;
		o.ValidateEpisodes                  = true;
		o.ValidateCovers                    = true;
		o.ValidateCoverFiles                = true;
		o.ValidateVideoFiles                = true;
		o.ValidateGroups                    = true;
		o.ValidateOnlineReferences          = true;
		o.ValidateDuplicateFilesByPath      = true;
		o.ValidateDuplicateFilesByMediaInfo = true;
		o.ValidateDatabaseConsistence       = true;
		o.ValidateSeriesStructure           = true;
		o.FindEmptyDirectories              = true;
		o.ValidateNfoFiles                  = true;
		o.ValidateNfoFullComparison         = true;
		return o;
	}

	public static DatabaseValidatorOptions presetDefault() {
		var o = new DatabaseValidatorOptions();
		o.ValidateMovies                    = true;
		o.ValidateSeries                    = true;
		o.ValidateSeasons                   = true;
		o.ValidateEpisodes                  = true;
		o.ValidateGroups                    = true;
		o.ValidateOnlineReferences          = true;
		o.ValidateDuplicateFilesByPath      = true;
		o.ValidateDuplicateFilesByMediaInfo = true;
		o.ValidateDatabaseConsistence       = true;
		o.ValidateSeriesStructure           = true;
		o.FindEmptyDirectories              = true;
		o.ValidateNfoFiles                  = true;
		return o;
	}

	public static DatabaseValidatorOptions presetQuick() {
		var o = new DatabaseValidatorOptions();
		o.ValidateMovies                    = true;
		o.ValidateSeries                    = true;
		o.ValidateSeasons                   = true;
		o.ValidateEpisodes                  = true;
		o.ValidateGroups                    = true;
		o.ValidateOnlineReferences          = true;
		o.ValidateDuplicateFilesByPath      = true;
		o.ValidateDuplicateFilesByMediaInfo = true;
		o.ValidateSeriesStructure           = true;
		return o;
	}
}
