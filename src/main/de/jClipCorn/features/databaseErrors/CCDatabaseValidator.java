package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.covertab.CCDefaultCoverCache;
import de.jClipCorn.database.covertab.ICoverCache;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.driver.PublicDatabaseInterface;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.RomanNumberFormatter;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.lambda.Func0to1WithIOException;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class CCDatabaseValidator extends AbstractDatabaseValidator {

	private static CCDatabaseValidator _inst = null;
	public static CCDatabaseValidator Inst()
	{
		if (_inst != null) return _inst;
		return _inst = new CCDatabaseValidator();
	}

	private CCDatabaseValidator() {
		super();
	}

	@Override
	@SuppressWarnings("Convert2MethodRef")
	protected void init() {

		// ###############################################
		// MOVIES
		// ###############################################
		{
			// Hole in Genres
			addMovieValidation(
					DatabaseErrorType.ERROR_INCONTINUOUS_GENRELIST,
					o -> o.ValidateMovies,
					mov -> mov.hasHoleInGenres(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_INCONTINUOUS_GENRELIST, mov));

			// Genre with wrong ID
			addMovieValidation(
					DatabaseErrorType.ERROR_WRONG_GENREID,
					o -> o.ValidateMovies,
					(mov, e) ->
					{
						for (int i = 0; i < mov.getGenreCount(); i++) {
							if (!mov.getGenre(i).isValid()) {
								e.add(DatabaseError.createSingleAdditional(DatabaseErrorType.ERROR_WRONG_GENREID, mov, i));
							}
						}
					});

			// Moviesize <> Real size
			addMovieValidation(
					DatabaseErrorType.ERROR_WRONG_FILESIZE,
					o -> o.ValidateVideoFiles,
					(mov, e) ->
					{
						CCFileSize size = CCFileSize.ZERO;
						for (int i = 0; i < mov.getPartcount(); i++) size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(mov.getAbsolutePart(i)));
						if (getRelativeDifference(size.getBytes(), mov.getFilesize().getBytes()) > getMaxSizeFileDrift()) {
							e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_FILESIZE, mov));
						}
					});

			// Genrecount <= 0
			addMovieValidation(
					DatabaseErrorType.ERROR_NOGENRE_SET,
					o -> o.ValidateMovies,
					mov -> mov.getGenreCount() <= 0,
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NOGENRE_SET, mov));

			// Genrecount <= 0
			addMovieValidation(
					DatabaseErrorType.ERROR_NOCOVERSET,
					o -> o.ValidateMovies,
					mov -> mov.getCoverID() == -1,
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NOCOVERSET, mov));

			// cover not found
			addMovieValidation(
					DatabaseErrorType.ERROR_COVER_NOT_FOUND,
					o -> o.ValidateCoverFiles,
					(mov, movielist) -> !movielist.getCoverCache().coverFileExists(mov.getCoverID()),
					(mov, movielist) -> DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_NOT_FOUND, mov));

			// no title set
			addMovieValidation(
					DatabaseErrorType.ERROR_TITLE_NOT_SET,
					o -> o.ValidateMovies,
					mov -> mov.getTitle().isEmpty(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, mov));

			// ZyklusID == 0
			addMovieValidation(
					DatabaseErrorType.ERROR_ZYKLUSNUMBER_IS_NEGONE,
					o -> o.ValidateMovies,
					mov -> !mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() == -1,
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_ZYKLUSNUMBER_IS_NEGONE, mov));

			// ZyklusID <> -1
			addMovieValidation(
					DatabaseErrorType.ERROR_ZYKLUSTITLE_IS_EMPTY,
					o -> o.ValidateMovies,
					mov -> mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() != -1,
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_ZYKLUSTITLE_IS_EMPTY, mov));

			// Hole in Parts
			addMovieValidation(
					DatabaseErrorType.ERROR_INCONTINUOUS_PARTS,
					o -> o.ValidateMovies,
					mov -> mov.hasHoleInParts(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_INCONTINUOUS_PARTS, mov));

			// Wrong Format
			addMovieValidation(
					DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS,
					o -> o.ValidateMovies,
					(mov, e) ->
					{
						boolean rform = false;
						for (int i = 0; i < mov.getPartcount(); i++) {
							CCFileFormat mfmt = CCFileFormat.getMovieFormat(PathFormatter.getExtension(mov.getPart(i)));
							if (mfmt != null && mfmt.equals(mov.getFormat())) rform = true;
						}
						if (!rform) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS, mov));
					});

			// Inexistent Paths
			addMovieValidation(
					DatabaseErrorType.ERROR_PATH_NOT_FOUND,
					o -> o.ValidateVideoFiles,
					(mov, e) ->
					{
						for (int i = 0; i < mov.getPartcount(); i++) {
							if (! new File(mov.getAbsolutePart(i)).exists()) {
								e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_PATH_NOT_FOUND, mov));
							}
						}
					});

			// Wrong AddDate
			addMovieValidation(
					DatabaseErrorType.ERROR_WRONG_ADDDATE,
					o -> o.ValidateMovies,
					mov -> mov.getAddDate().isLessEqualsThan(CCDate.getMinimumDate()) || mov.getAddDate().isGreaterThan(CCDate.getCurrentDate()),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_ADDDATE, mov));

			// Zyklus/Title ends/starts with a space
			addMovieValidation(
					DatabaseErrorType.ERROR_NOT_TRIMMED,
					o -> o.ValidateMovies,
					mov -> PathFormatter.isUntrimmed(mov.getTitle()) || PathFormatter.isUntrimmed(mov.getZyklus().getTitle()),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NOT_TRIMMED, mov));

			// Zyklus ends with Roman
			addMovieValidation(
					DatabaseErrorType.ERROR_ZYKLUS_ENDS_WITH_ROMAN,
					o -> o.ValidateMovies,
					mov -> RomanNumberFormatter.endsWithRoman(mov.getZyklus().getTitle()),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_ZYKLUS_ENDS_WITH_ROMAN, mov));

			// Duplicate Name
			addMovieValidation(
					DatabaseErrorType.ERROR_DUPLICATE_TITLE,
					o -> o.ValidateMovies,
					(mov, movielist, e) ->
					{
						for (CCMovie imov : movielist.iteratorMovies()) {
							if (StringUtils.equalsIgnoreCase(imov.getCompleteTitle(), mov.getCompleteTitle()) && (imov.getLanguage().isSubsetOf(mov.getLanguage()) || mov.getLanguage().isSubsetOf(imov.getLanguage()))) {
								if (mov.getLocalID() != imov.getLocalID()) {
									e.add(DatabaseError.createDouble(DatabaseErrorType.ERROR_DUPLICATE_TITLE, mov, imov));
								}
								break;
							}
						}
					});

			// Wrong filename
			addMovieValidation(
					DatabaseErrorType.ERROR_WRONG_FILENAME,
					o -> o.ValidateMovies,
					(mov, movielist, e) ->
					{
						boolean wrongfn = false;
						for (int i = 0; i < mov.getPartcount(); i++) {
							if (! PathFormatter.getFilenameWithExt(mov.getAbsolutePart(i)).equals(mov.generateFilename(i))) wrongfn = true;
						}
						if (wrongfn) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_FILENAME, mov));
					});

			// Watch never <> ViewedState
			addMovieValidation(
					DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER,
					o -> o.ValidateMovies,
					mov -> mov.isViewed() && mov.getTag(CCTagList.TAG_WATCH_NEVER),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER, mov));

			// Duplicate Genre
			addMovieValidation(
					DatabaseErrorType.ERROR_DUPLICATE_GENRE,
					o -> o.ValidateMovies,
					(mov, e) ->
					{
						int[] g_count = new int[256];
						for (int i = 0; i < CCGenreList.getMaxListSize(); i++) g_count[mov.getGenre(i).asInt()]++;

						for (int i = 1; i < 256; i++) {
							if (g_count[i] > 1) {
								e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DUPLICATE_GENRE, mov));
								break;
							}
						}
					});

			// Invalid path characters
			addMovieValidation(
					DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH,
					o -> o.ValidateMovies,
					(mov, e) ->
					{
						for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
							if (PathFormatter.containsIllegalPathSymbolsInSerializedFormat(mov.getPart(i))){
								e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH, mov));
								break;
							}
						}
					});

			// Invalid CCOnlineRef
			addMovieValidation(
					DatabaseErrorType.ERROR_INVALID_ONLINEREF,
					o -> o.ValidateMovies,
					mov -> !mov.getOnlineReference().isValid(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_ONLINEREF, mov));

			// History but not viewed
			addMovieValidation(
					DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED,
					o -> o.ValidateMovies,
					mov -> !mov.isViewed() && !mov.getViewedHistory().isEmpty(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED, mov));

			// History is invalid
			addMovieValidation(
					DatabaseErrorType.ERROR_INVALID_HISTORY,
					o -> o.ValidateMovies,
					mov -> !mov.getViewedHistory().isValid(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_HISTORY, mov));

			// Duplicate/Empty Groups
			addMovieValidation(
					DatabaseErrorType.ERROR_INVALID_GROUPLIST,
					o -> o.ValidateMovies,
					(mov, e) ->
					{
						Set<String> groupSet = new HashSet<>();
						for (CCGroup group : mov.getGroups()) {
							if (! groupSet.add(group.Name.toLowerCase().trim())) {
								e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_GROUPLIST, mov));
								break;
							}
							if (group.Name.toLowerCase().trim().isEmpty()) {
								e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_GROUPLIST, mov));
								break;
							}
						}
					});

			// Viewed but no history
			addMovieValidation(
					DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY,
					o -> o.ValidateMovies,
					mov -> mov.isViewed() && mov.getViewedHistory().isEmpty(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY, mov));

			// Cover too small
			addMovieValidation(
					DatabaseErrorType.ERROR_COVER_TOO_SMALL,
					o -> o.ValidateMovies,
					mov -> mov.getCoverDimensions().Item1 < ImageUtilities.BASE_COVER_WIDTH && mov.getCoverDimensions().Item2 < ImageUtilities.BASE_COVER_HEIGHT,
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_SMALL, mov));

			// Cover too big
			addMovieValidation(
					DatabaseErrorType.ERROR_COVER_TOO_BIG,
					o -> o.ValidateMovies,
					mov -> mov.getCoverDimensions().Item1 > ImageUtilities.getCoverWidth() && mov.getCoverDimensions().Item2 < ImageUtilities.getCoverHeight(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_BIG, mov));

			// Invalid Tag for movies
			addMovieValidation(
					DatabaseErrorType.ERROR_INVALID_GROUPLIST,
					o -> o.ValidateMovies,
					(mov, e) ->
					{
						for (CCSingleTag t : mov.getTags().iterate()) {
							if (!t.IsMovieTag) {e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_MOVIE, mov)); break; }
						}
					});

			// Not-normalized path
			addMovieValidation(
					DatabaseErrorType.ERROR_NON_NORMALIZED_PATH,
					o -> o.ValidateMovies,
					(mov, e) ->
					{
						for (int i = 0; i < mov.getPartcount(); i++) {
							if (!PathFormatter.getCCPath(mov.getAbsolutePart(i)).equals(mov.getPart(i))) {
								e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NON_NORMALIZED_PATH, mov));
								break;
							}
						}
					});

			// No language
			addMovieValidation(
					DatabaseErrorType.ERROR_NO_LANG,
					o -> o.ValidateMovies,
					mov -> mov.getLanguage().isEmpty(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NO_LANG, mov));

			// Muted language must be single
			addMovieValidation(
					DatabaseErrorType.ERROR_LANG_MUTED_SUBSET,
					o -> o.ValidateMovies,
					mov -> mov.getLanguage().contains(CCDBLanguage.MUTED) && !mov.getLanguage().isSingle(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_LANG_MUTED_SUBSET, mov));

			// MediaInfo not set
			addMovieValidation(
					DatabaseErrorType.ERROR_MEDIAINFO_UNSET,
					o -> o.ValidateMovies,
					mov -> mov.getMediaInfo().isUnset(),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_MEDIAINFO_UNSET, mov));

			// invalid MediaInfo
			addMovieValidation(
					DatabaseErrorType.ERROR_MEDIAINFO_INVALID,
					o -> o.ValidateMovies,
					mov -> mov.getMediaInfo().isSet() && (mov.getMediaInfo().validate()!=null),
					mov -> DatabaseError.createSingleAdditional(DatabaseErrorType.ERROR_MEDIAINFO_INVALID, mov, mov.getMediaInfo().validate()));

			// MediaInfo size does not match movie filesize
			addMovieValidation(
					DatabaseErrorType.ERROR_MEDIAINFO_SIZE_MISMATCH,
					o -> o.ValidateMovies,
					mov -> mov.getMediaInfo().isSet() && (mov.getPartcount()==1 && mov.getMediaInfo().getFilesize() != mov.getFilesize().getBytes()),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_MEDIAINFO_SIZE_MISMATCH, mov));

			// MediaInfo length does not match movie length
			addMovieValidation(
					DatabaseErrorType.ERROR_MEDIAINFO_LENGTH_MISMATCH,
					o -> o.ValidateMovies,
					mov -> mov.getMediaInfo().isSet() &&
							mov.getPartcount()==1 &&
							!mov.getTag(CCTagList.TAG_MISSING_TIME) &&
							!mov.getTag(CCTagList.TAG_FILE_CORRUPTED) &&
							!mov.getTag(CCTagList.TAG_WATCH_CAMRIP) &&
							!mov.getTag(CCTagList.TAG_WATCH_MICDUBBED) &&
							!mov.getTag(CCTagList.TAG_WRONG_LANGUAGE) &&
							isDiff(mov.getMediaInfo().getDurationInMinutes(), mov.getLength(), 0.10, 10),
					mov -> DatabaseError.createSingle(DatabaseErrorType.ERROR_MEDIAINFO_LENGTH_MISMATCH, mov));

			// Mediainfo does not match actual file
			addMovieValidation(
					DatabaseErrorType.ERROR_MEDIAINFO_FILE_CHANGED,
					o -> o.ValidateVideoFiles,
					(mov, e) ->
					{
						if (mov.getMediaInfo().isSet()) {
							try {
								BasicFileAttributes attr = Files.readAttributes(new File(mov.getAbsolutePart(0)).toPath(), BasicFileAttributes.class);
								boolean a = new File(mov.getAbsolutePart(0)).length() != mov.getMediaInfo().getFilesize();
								boolean b = attr.creationTime().toMillis() != mov.getMediaInfo().getCDate();
								boolean c = attr.lastModifiedTime().toMillis() != mov.getMediaInfo().getMDate();
								if (a||b||c) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_MEDIAINFO_FILE_CHANGED, mov));
							} catch (IOException ex) {
								/**/
							}
						}
					});
		}

		// ###############################################
		// SERIES
		// ###############################################
		{
			// no title set
			addSeriesValidation(
					DatabaseErrorType.ERROR_TITLE_NOT_SET,
					o -> o.ValidateSeries,
					series -> series.getTitle().isEmpty(),
					series -> DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, series));

			// Genre with wrong ID
			addSeriesValidation(
					DatabaseErrorType.ERROR_WRONG_GENREID,
					o -> o.ValidateSeries,
					(series, e) ->
					{
						for (int i = 0; i < series.getGenreCount(); i++) {
							if (! series.getGenre(i).isValid()) {
								e.add(DatabaseError.createSingleAdditional(DatabaseErrorType.ERROR_WRONG_GENREID, series, i));
							}
						}
					});

			// cover not set
			addSeriesValidation(
					DatabaseErrorType.ERROR_NOCOVERSET,
					o -> o.ValidateSeries,
					series -> series.getCoverID() == -1,
					series -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NOCOVERSET, series));

			// cover not found
			addSeriesValidation(
					DatabaseErrorType.ERROR_COVER_NOT_FOUND,
					o -> o.ValidateCoverFiles,
					(series, movielist) -> !movielist.getCoverCache().coverFileExists(series.getCoverID()),
					(series, movielist) -> DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_NOT_FOUND, series));

			// Wrong AddDate
			addSeriesValidation(
					DatabaseErrorType.ERROR_WRONG_ADDDATE,
					o -> o.ValidateSeries,
					series -> !series.isEmpty() && (series.getAddDate().isLessEqualsThan(CCDate.getMinimumDate()) || series.getAddDate().isGreaterThan(CCDate.getCurrentDate())),
					series -> DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_ADDDATE, series));

			// Zyklus/Title ends/starts with a space
			addSeriesValidation(
					DatabaseErrorType.ERROR_NOT_TRIMMED,
					o -> o.ValidateSeries,
					series -> PathFormatter.isUntrimmed(series.getTitle()),
					series -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NOT_TRIMMED, series));

			// Duplicate Genre
			addSeriesValidation(
					DatabaseErrorType.ERROR_DUPLICATE_GENRE,
					o -> o.ValidateSeries,
					(series, e) ->
					{
						int[] g_count = new int[256];
						for (int i = 0; i < CCGenreList.getMaxListSize(); i++) g_count[series.getGenre(i).asInt()]++;
						for (int i = 1; i < 256; i++) {
							if (g_count[i] > 1) { e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DUPLICATE_GENRE, series)); break; }
						}
					});

			// Invalid CCOnlineRef
			addSeriesValidation(
					DatabaseErrorType.ERROR_INVALID_ONLINEREF,
					o -> o.ValidateSeries,
					series -> !series.getOnlineReference().isValid(),
					series -> DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_ONLINEREF, series));

			// Duplicate/Empty Groups
			addSeriesValidation(
					DatabaseErrorType.ERROR_INVALID_GROUPLIST,
					o -> o.ValidateSeries,
					(series, e) ->
					{
						Set<String> groupSet = new HashSet<>();
						for (CCGroup group : series.getGroups()) {
							if (! groupSet.add(group.Name.toLowerCase().trim())) {
								e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_GROUPLIST, series));
								break;
							}
							if (group.Name.toLowerCase().trim().isEmpty()) {
								e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_GROUPLIST, series));
								break;
							}
							if (! CCGroup.isValidGroupName(group.Name)) {
								e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_GROUPLIST, series));
								break;
							}
						}
					});

			// Cover too small
			addSeriesValidation(
					DatabaseErrorType.ERROR_COVER_TOO_SMALL,
					o -> o.ValidateSeries,
					series -> series.getCoverDimensions().Item1 < ImageUtilities.BASE_COVER_WIDTH && series.getCoverDimensions().Item2 < ImageUtilities.BASE_COVER_HEIGHT,
					series -> DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_SMALL, series));

			// Cover too big
			addSeriesValidation(
					DatabaseErrorType.ERROR_COVER_TOO_BIG,
					o -> o.ValidateSeries,
					series -> series.getCoverDimensions().Item1 > ImageUtilities.getCoverWidth() && series.getCoverDimensions().Item2 < ImageUtilities.getCoverHeight(),
					series -> DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_BIG, series));

			// Invalid Tag for series
			addSeriesValidation(
					DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_SERIES,
					o -> o.ValidateSeries,
					(series, e) ->
					{
						for (CCSingleTag t : series.getTags().iterate()) {
							if (!t.IsSeriesTag) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_SERIES, series));
						}
					});
		}

		// ###############################################
		// SEASONS
		// ###############################################
		{
			// no title set
			addSeasonValidation(
					DatabaseErrorType.ERROR_TITLE_NOT_SET,
					o -> o.ValidateSeasons,
					season -> season.getTitle().isEmpty(),
					season -> DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, season));

			// cover not set
			addSeasonValidation(
					DatabaseErrorType.ERROR_NOCOVERSET,
					o -> o.ValidateSeries,
					season -> season.getCoverID() == -1,
					season -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NOCOVERSET, season));

			// cover not found
			addSeasonValidation(
					DatabaseErrorType.ERROR_COVER_NOT_FOUND,
					o -> o.ValidateCoverFiles,
					(season, movielist) -> !movielist.getCoverCache().coverFileExists(season.getCoverID()),
					(season, movielist) -> DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_NOT_FOUND, season));

			// Wrong AddDate
			addSeasonValidation(
					DatabaseErrorType.ERROR_WRONG_ADDDATE,
					o -> o.ValidateSeasons,
					season -> !season.isEmpty() && (season.getAddDate().isLessEqualsThan(CCDate.getMinimumDate()) || season.getAddDate().isGreaterThan(CCDate.getCurrentDate())),
					season -> DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_ADDDATE, season));

			// Zyklus/Title ends/starts with a space
			addSeasonValidation(
					DatabaseErrorType.ERROR_NOT_TRIMMED,
					o -> o.ValidateSeasons,
					season -> PathFormatter.isUntrimmed(season.getTitle()),
					season -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NOT_TRIMMED, season));

			// Non-continoous episode numbers
			addSeasonValidation(
					DatabaseErrorType.ERROR_NON_CONTINOOUS_EPISODES,
					o -> o.ValidateSeasons,
					season -> !season.isContinoousEpisodeNumbers(),
					season -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NON_CONTINOOUS_EPISODES, season));

			// Cover too small
			addSeasonValidation(
					DatabaseErrorType.ERROR_COVER_TOO_SMALL,
					o -> o.ValidateSeasons,
					season -> season.getCoverDimensions().Item1 < ImageUtilities.BASE_COVER_WIDTH && season.getCoverDimensions().Item2 < ImageUtilities.BASE_COVER_HEIGHT,
					season -> DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_SMALL, season));

			// Cover too big
			addSeasonValidation(
					DatabaseErrorType.ERROR_COVER_TOO_BIG,
					o -> o.ValidateSeasons,
					season -> season.getCoverDimensions().Item1 > ImageUtilities.getCoverWidth() && season.getCoverDimensions().Item2 < ImageUtilities.getCoverHeight(),
					season -> DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_BIG, season));
		}

		// ###############################################
		// EPISODES
		// ###############################################
		{
			// no title set
			addEpisodeValidation(
					DatabaseErrorType.ERROR_TITLE_NOT_SET,
					o -> o.ValidateEpisodes,
					episode -> episode.getTitle().isEmpty(),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, episode));

			// Wrong Format
			addEpisodeValidation(
					DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS,
					o -> o.ValidateEpisodes,
					episode -> !episode.getFormat().equals(CCFileFormat.getMovieFormat(PathFormatter.getExtension(episode.getPart()))),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS, episode));

			// Inexistent Paths
			addEpisodeValidation(
					DatabaseErrorType.ERROR_PATH_NOT_FOUND,
					o -> o.ValidateVideoFiles,
					episode -> !new File(episode.getAbsolutePart()).exists(),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_PATH_NOT_FOUND, episode));

			// Moviesize <> Real size
			addEpisodeValidation(
					DatabaseErrorType.ERROR_WRONG_FILESIZE,
					o -> o.ValidateVideoFiles,
					episode -> getRelativeDifference(new CCFileSize(FileSizeFormatter.getFileSize(episode.getAbsolutePart())).getBytes(), episode.getFilesize().getBytes()) > getMaxSizeFileDrift(),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_FILESIZE, episode));

			// Wrong AddDate
			addEpisodeValidation(
					DatabaseErrorType.ERROR_WRONG_ADDDATE,
					o -> o.ValidateEpisodes,
					episode -> episode.getAddDate().isLessEqualsThan(CCDate.getMinimumDate()) || episode.getAddDate().isGreaterThan(CCDate.getCurrentDate()),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_ADDDATE, episode));

			// Wrong LastViewedDate
			addEpisodeValidation(
					DatabaseErrorType.ERROR_WRONG_LASTVIEWEDDATE,
					o -> o.ValidateEpisodes,
					episode -> episode.getViewedHistoryLast().isGreaterThan(CCDate.getCurrentDate()),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_LASTVIEWEDDATE, episode));

			// Wrong Viewed State // Dar nicht benutzt werden - da leider durch den Import der alten CC-Database viele solche Fälle vorkommen
			//addEpisodeValidation(
			//		DatabaseErrorType.ERROR_LASTVIEWED_AND_ISVIEWED_IS_PARADOX,
			//		o -> o.ValidateEpisodes,
			//		episode -> episode.getLastViewed().isGreaterThan(MIN_DATE) ^ episode.isViewed(),
			//		episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_LASTVIEWED_AND_ISVIEWED_IS_PARADOX, episode));

			// Zyklus ends/starts with a space
			addEpisodeValidation(
					DatabaseErrorType.ERROR_NOT_TRIMMED,
					o -> o.ValidateEpisodes,
					episode -> PathFormatter.isUntrimmed(episode.getTitle()),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NOT_TRIMMED, episode));

			// MediaInfo size does not match movie filesize
			addEpisodeValidation(
					DatabaseErrorType.ERROR_MEDIAINFO_SIZE_MISMATCH,
					o -> o.ValidateEpisodes,
					episode -> episode.getMediaInfo().isSet() && (episode.getMediaInfo().getFilesize() != episode.getFilesize().getBytes()),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_MEDIAINFO_SIZE_MISMATCH, episode));

			// MediaInfo length does not match movie length
			addEpisodeValidation(
					DatabaseErrorType.ERROR_MEDIAINFO_LENGTH_MISMATCH,
					o -> o.ValidateEpisodes,
					episode -> episode.getMediaInfo().isSet() &&
							!episode.getTag(CCTagList.TAG_MISSING_TIME) &&
							!episode.getTag(CCTagList.TAG_FILE_CORRUPTED) &&
							!episode.getTag(CCTagList.TAG_WATCH_CAMRIP) &&
							!episode.getTag(CCTagList.TAG_WATCH_MICDUBBED) &&
							!episode.getTag(CCTagList.TAG_WRONG_LANGUAGE) &&
							isDiff(episode.getMediaInfo().getDurationInMinutes(), episode.getLength(), 0.33, 5),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_MEDIAINFO_LENGTH_MISMATCH, episode));

			// Mediainfo does not match actual file
			addEpisodeValidation(
					DatabaseErrorType.ERROR_MEDIAINFO_FILE_CHANGED,
					o -> o.ValidateVideoFiles,
					(episode, e) ->
					{
						if (episode.getMediaInfo().isSet()) {
							try {
								BasicFileAttributes attr = Files.readAttributes(new File(episode.getAbsolutePart()).toPath(), BasicFileAttributes.class);
								boolean a = new File(episode.getAbsolutePart()).length() != episode.getMediaInfo().getFilesize();
								boolean b = attr.creationTime().toMillis() != episode.getMediaInfo().getCDate();
								boolean c = attr.lastModifiedTime().toMillis() != episode.getMediaInfo().getMDate();
								if (a||b||c) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_MEDIAINFO_FILE_CHANGED, episode));
							} catch (IOException ex) {
								/**/
							}
						}
					});

			// Watch never <> ViewedState
			addEpisodeValidation(
					DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER,
					o -> o.ValidateEpisodes,
					episode -> episode.isViewed() && episode.getTag(CCTagList.TAG_WATCH_NEVER),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER, episode));

			// LastViewed too small
			addEpisodeValidation(
					DatabaseErrorType.ERROR_LASTWATCHED_TOO_OLD,
					o -> o.ValidateEpisodes,
					episode -> !episode.getViewedHistoryLast().isMinimum() && episode.getViewedHistoryLast().isLessThan(CCDate.create(1, 6, 1900)),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_LASTWATCHED_TOO_OLD, episode));

			// Folderstructure not formatted
			addEpisodeValidation(
					DatabaseErrorType.ERROR_INVALID_SERIES_STRUCTURE,
					o -> o.ValidateEpisodes,
					episode -> CCProperties.getInstance().PROP_VALIDATE_CHECK_SERIES_STRUCTURE.getValue() && ! episode.checkFolderStructure(),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_SERIES_STRUCTURE, episode));

			// Invalid path characters
			addEpisodeValidation(
					DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH,
					o -> o.ValidateEpisodes,
					episode -> PathFormatter.containsIllegalPathSymbolsInSerializedFormat(episode.getPart()),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH, episode));

			// History but not viewed
			addEpisodeValidation(
					DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED,
					o -> o.ValidateEpisodes,
					episode -> !episode.isViewed() && !episode.getViewedHistory().isEmpty(),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED, episode));

			// Viewed but no history
			addEpisodeValidation(
					DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY,
					o -> o.ValidateEpisodes,
					episode -> episode.isViewed() && episode.getViewedHistory().isEmpty(),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY, episode));

			// History is invalid
			addEpisodeValidation(
					DatabaseErrorType.ERROR_INVALID_HISTORY,
					o -> o.ValidateEpisodes,
					episode -> !episode.getViewedHistory().isValid(),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_HISTORY, episode));

			// Invalid Tag for episode
			addEpisodeValidation(
					DatabaseErrorType.ERROR_MEDIAINFO_FILE_CHANGED,
					o -> o.ValidateEpisodes,
					(episode, e) ->
					{
						for (CCSingleTag t : episode.getTags().iterate()) {
							if (!t.IsEpisodeTag) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_EPISODE, episode));
						}
					});

			// Not-normalized path
			addEpisodeValidation(
					DatabaseErrorType.ERROR_NON_NORMALIZED_PATH,
					o -> o.ValidateEpisodes,
					episode -> !PathFormatter.getCCPath(episode.getAbsolutePart()).equals(episode.getPart()),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NON_NORMALIZED_PATH, episode));

			// No language
			addEpisodeValidation(
					DatabaseErrorType.ERROR_NO_LANG,
					o -> o.ValidateEpisodes,
					episode -> episode.getLanguage().isEmpty(),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_NO_LANG, episode));

			// Muted language must be single
			addEpisodeValidation(
					DatabaseErrorType.ERROR_LANG_MUTED_SUBSET,
					o -> o.ValidateEpisodes,
					episode -> episode.getLanguage().contains(CCDBLanguage.MUTED) && !episode.getLanguage().isSingle(),
					episode -> DatabaseError.createSingle(DatabaseErrorType.ERROR_LANG_MUTED_SUBSET, episode));
		}
	}

	@Override
	protected void findCoverErrors(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
	{
		ICoverCache cc = movielist.getCoverCache();

		pcl.stepRootAndResetSub("Find cover errors",  //$NON-NLS-1$
				movielist.getElementCount() +         // Duplicate Covers
						movielist.getMovieCount() +           // Duplicate/Invalid CoverIDs
						movielist.getSeriesCount() +          // Duplicate/Invalid CoverIDs
						movielist.getSeasonCount() +          // Duplicate/Invalid CoverIDs
						cc.getCoverCount() +                  // Unused CoverIDs
						cc.getCoverCount());                  // Reused cover filenames

		// ###############################################
		// Duplicate Covers
		// ###############################################

		List<DatabaseCoverElement> cvrList = new ArrayList<>();

		for (CCDatabaseElement el : movielist.iteratorElements()) {
			pcl.stepSub(el.getFullDisplayTitle());

			cvrList.add(new DatabaseCoverElement(el.getCoverID(), el));

			if (el.isSeries()) {
				for (int j = 0; j < ((CCSeries)el).getSeasonCount(); j++) {
					cvrList.add(new DatabaseCoverElement(((CCSeries)el).getSeasonByArrayIndex(j).getCoverID(), ((CCSeries)el).getSeasonByArrayIndex(j)));
				}
			}
		}

		Collections.sort(cvrList);

		for (int i = 1; i < cvrList.size(); i++) {
			if (cvrList.get(i).equalsCover(cvrList.get(i-1))) {
				e.add(DatabaseError.createDouble(DatabaseErrorType.ERROR_DUPLICATE_COVERLINK, cvrList.get(i-1).getElement(), cvrList.get(i).getElement()));
			}
		}

		// ###############################################
		// Duplicate/Invalid CoverIDs
		// ###############################################

		Set<Integer> coverIDsTable = CCStreams.iterate(cc.listCovers()).map(p->p.ID).toSet();

		HashMap<Integer, ICCCoveredElement> coversDatabase = new HashMap<>();

		for (CCMovie m : movielist.iteratorMovies())
		{
			pcl.stepSub(m.getFullDisplayTitle());

			if (m.getCoverID() == -1) continue;

			if (coversDatabase.containsKey(m.getCoverID())) {
				e.add(DatabaseError.createDouble(DatabaseErrorType.ERROR_DUPLICATE_COVERID, m, coversDatabase.get(m.getCoverID())));
				continue;
			}

			coversDatabase.put(m.getCoverID(), m);

			if (!coverIDsTable.contains(m.getCoverID())) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVERID_NOT_FOUND, m));
		}

		for (CCSeries s : movielist.iteratorSeries())
		{
			pcl.stepSub(s.getTitle());

			if (s.getCoverID() == -1) continue;

			if (coversDatabase.containsKey(s.getCoverID())) {
				e.add(DatabaseError.createDouble(DatabaseErrorType.ERROR_DUPLICATE_COVERID, s, coversDatabase.get(s.getCoverID())));
				continue;
			}

			coversDatabase.put(s.getCoverID(), s);

			if (!coverIDsTable.contains(s.getCoverID())) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVERID_NOT_FOUND, s));
		}

		for (CCSeason s : movielist.iteratorSeasons())
		{
			pcl.stepSub(s.getSeries().getTitle() + " S" + s.getSeasonNumber()); //$NON-NLS-1$

			if (s.getCoverID() == -1) continue;

			if (coversDatabase.containsKey(s.getCoverID())) {
				e.add(DatabaseError.createDouble(DatabaseErrorType.ERROR_DUPLICATE_COVERID, s, coversDatabase.get(s.getCoverID())));
				continue;
			}

			coversDatabase.put(s.getCoverID(), s);

			if (!coverIDsTable.contains(s.getCoverID())) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVERID_NOT_FOUND, s));
		}

		// ###############################################
		// Unused CoverIDs
		// ###############################################

		for (CCCoverData cce : cc.listCovers()) {
			pcl.stepSub(cce.Filename);

			if (!coversDatabase.containsKey(cce.ID)) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_UNUSED_COVER_ENTRY, cce.Filename));
			}
		}

		// ###############################################
		// Reused cover filenames
		// ###############################################

		HashSet<String> coverFileMapping = new HashSet<>();
		for (CCCoverData cce : cc.listCovers()) {
			pcl.stepSub(cce.Filename);

			if (coverFileMapping.contains(cce.Filename.toLowerCase())) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DUPLICATE_REFERENCES_COVER_FILE, cce.Filename));
				continue;
			}
			coverFileMapping.add(cce.Filename.toLowerCase());
		}
	}

	@Override
	protected void findCoverFileErrors(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Validate cover files", 1); //$NON-NLS-1$

		ICoverCache cc = movielist.getCoverCache();
		List<Tuple<String, Func0to1WithIOException<BufferedImage>>> files = new ArrayList<>();
		if (cc instanceof CCDefaultCoverCache) files = ((CCDefaultCoverCache)cc).listCoversInFilesystem();

		pcl.setSubMax(files.size() + cc.getCoverCount());

		// ###############################################
		// Too much Cover in Folder
		// ###############################################

		List<DatabaseCoverElement> cvrList = new ArrayList<>();

		for (CCDatabaseElement el : movielist.iteratorElements()) {
			cvrList.add(new DatabaseCoverElement(el.getCoverID(), el));

			if (el.isSeries()) {
				for (int j = 0; j < ((CCSeries)el).getSeasonCount(); j++) {
					cvrList.add(new DatabaseCoverElement(((CCSeries)el).getSeasonByArrayIndex(j).getCoverID(), ((CCSeries)el).getSeasonByArrayIndex(j)));
				}
			}
		}

		for (int i = 0; i < files.size(); i++)
		{
			String cvrname = files.get(i).Item1;
			pcl.stepSub(cvrname);

			boolean found = false;
			for (int j = 0; j < cvrList.size(); j++)
			{
				CCCoverData cce = cc.getInfo(cvrList.get(j).getCoverID());
				found |= cce.Filename.equalsIgnoreCase(cvrname);
			}
			if (! found) {
				if (cc instanceof CCDefaultCoverCache) {
					e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NONLINKED_COVERFILE, new File(PathFormatter.combine(((CCDefaultCoverCache)cc).getCoverPath(), cvrname))));
				} else {
					e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NONLINKED_COVERFILE, cvrname));
				}
			}
		}

		// ###############################################
		// Cover metadata
		// ###############################################

		for (CCCoverData cce : cc.listCovers())
		{
			pcl.stepSub(cce.Filename);

			String fp = cc.getFilepath(cce);
			if (fp == null) continue;

			String checksum = "ERR"; //$NON-NLS-1$
			try (FileInputStream fis = new FileInputStream(new File(fp))) { checksum = DigestUtils.sha256Hex(fis).toUpperCase(); } catch (IOException ex) {/**/}
			if (! checksum.equals(cce.Checksum)) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_CHECKSUM_MISMATCH, cce.Filename));

			BufferedImage img = cc.getCover(cce);
			if (img.getWidth() != cce.Width || img.getHeight() != cce.Height)	 e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_DIMENSIONS_MISMATCH, cce.Filename));

			if (new File(fp).length() != cce.Filesize) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_FILESIZE_MISMATCH, cce.Filename));
		}
	}

	@Override
	protected void findDuplicateFiles(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Find duplicate files", 2 * movielist.getTotalDatabaseCount()); //$NON-NLS-1$

		boolean ignIFO = CCProperties.getInstance().PROP_VALIDATE_DUP_IGNORE_IFO.getValue();

		List<DatabaseFileElement> flList = new ArrayList<>();

		for (CCDatabaseElement el : movielist.iteratorElements()) {
			if (el.isMovie()) {
				for (int i = 0; i < ((CCMovie)el).getPartcount(); i++) {
					if (ignIFO && CCFileFormat.getMovieFormat(PathFormatter.getExtension(((CCMovie)el).getAbsolutePart(i))) == CCFileFormat.IFO) {
						continue;
					}

					flList.add(new DatabaseFileElement(((CCMovie)el).getAbsolutePart(i), ((CCMovie)el)));
				}
			} else if (el.isSeries()) {
				CCSeries s = ((CCSeries)el);
				for (int i = 0; i < s.getSeasonCount(); i++) {
					CCSeason se = s.getSeasonByArrayIndex(i);
					for (int j = 0; j < se.getEpisodeCount(); j++) {
						if (ignIFO && CCFileFormat.getMovieFormat(PathFormatter.getExtension(se.getEpisodeByArrayIndex(j).getAbsolutePart())) == CCFileFormat.IFO) {
							continue;
						}

						flList.add(new DatabaseFileElement(se.getEpisodeByArrayIndex(j).getAbsolutePart(), se.getEpisodeByArrayIndex(j)));
					}
				}
			}

			pcl.stepSub(el.getFullDisplayTitle());
		}

		Collections.sort(flList);

		for (int i = 1; i < flList.size(); i++) {
			if (!flList.get(i).getPath().isEmpty() && flList.get(i).equalsPath(flList.get(i-1))) {
				e.add(DatabaseError.createDouble(DatabaseErrorType.ERROR_DUPLICATE_FILELINK, flList.get(i-1).getElement(), flList.get(i).getElement()));
			}

			pcl.stepSub(PathFormatter.getFilenameWithExt(flList.get(i).getPath()));
		}
	}

	@Override
	protected void findErrorInGroups(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Validate groups", 4 * movielist.getGroupCount()); //$NON-NLS-1$

		Set<String> groupSet = new HashSet<>();
		for (CCGroup group : movielist.getGroupList()) {
			pcl.stepSub(group.Name);

			if (! groupSet.add(group.Name.toLowerCase().trim())) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_GROUP, group));
				break;
			}

			if (group.Name.toLowerCase().trim().isEmpty()) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_GROUP, group));
				break;
			}

			if (! group.Name.trim().equals(group.Name)) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_GROUP, group));
				break;
			}
		}

		for (CCGroup group : movielist.getGroupList()) {
			pcl.stepSub(group.Name);

			if (group.Parent.isEmpty()) continue;

			CCGroup parent = movielist.getGroupOrNull(group.Parent);
			if (parent == null) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_GROUP_PARENT, group));
				break;
			}

		}

		for (CCGroup group : movielist.getGroupList()) {
			pcl.stepSub(group.Name);

			CCGroup g = group;

			for(int i = 0; i < CCGroup.MAX_SUBGROUP_DEPTH; i++) {
				if (g.Parent.isEmpty()) {
					g = null;
					break;
				}

				CCGroup parent = movielist.getGroupOrNull(g.Parent);
				if (parent == null) {
					// parent is invalid - error was throw above
					g = null;
					break;
				} else {
					g = parent;
				}
			}

			if (g != null) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_GROUP_NESTING_TOO_DEEP, group));
			}

		}

		for (CCGroup group : movielist.getGroupList()) {
			pcl.stepSub(group.Name);

			if (movielist.getDatabaseElementsbyGroup(group).isEmpty() && movielist.getSubGroups(group).isEmpty()) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_UNUSED_GROUP, group));
			}
		}

	}

	@Override
	protected void findDuplicateOnlineRef(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Validate groups", movielist.getElementCount()); //$NON-NLS-1$

		Set<String> refSet = new HashSet<>();

		for (CCMovie el : movielist.iteratorMovies()) {
			for(CCSingleOnlineReference soref : el.getOnlineReference()) {
				if (! refSet.add(el.getLanguage().serializeToLong() + '_' + soref.toSerializationString())) {
					e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DUPLICATE_REF, el));
				}
			}

			pcl.stepSub(el.getFullDisplayTitle());
		}

		for (CCSeries el : movielist.iteratorSeries()) {
			for(CCSingleOnlineReference soref : el.getOnlineReference()) {
				if (! refSet.add(el.getCommonLanguages().serializeToLong() + '_' + soref.toSerializationString())) {
					e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DUPLICATE_REF, el));
				}
			}

			pcl.stepSub(el.getFullDisplayTitle());
		}
	}

	@Override
	@SuppressWarnings("nls")
	protected void findInternalDatabaseErrors(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
	{
		pcl.stepRootAndResetSub("Validate database layer", 7);

		PublicDatabaseInterface db = movielist.getInternalDatabaseDirectly();

		// ### 1 ###
		try
		{
			pcl.stepSub("Global ID uniqueness");

			List<Integer> ids_elm = db.querySQL("SELECT LOCALID  FROM ELEMENTS",              1, o -> (int)o[0]);
			List<Integer> ids_ser = db.querySQL("SELECT SERIESID FROM ELEMENTS WHERE TYPE=1", 1, o -> (int)o[0]);
			List<Integer> ids_sea = db.querySQL("SELECT SEASONID FROM SEASONS",               1, o -> (int)o[0]);
			List<Integer> ids_epi = db.querySQL("SELECT LOCALID  FROM EPISODES",              1, o -> (int)o[0]);

			List<Integer> duplicates = CCStreams
					.iterate(ids_elm, ids_ser, ids_sea, ids_epi)
					.groupBy(p->p)
					.filter(p->p.getValue().size()>1)
					.map(Map.Entry::getKey)
					.enumerate();

			for (int dup : duplicates) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_DUPLICATE_ID, dup));
			}

			int last_id = db.querySingleIntSQLThrow("SELECT CAST(IVALUE AS INTEGER) FROM INFO WHERE IKEY='LAST_ID'", 0);

			for (int tlid : CCStreams.iterate(ids_elm, ids_ser, ids_sea, ids_epi).filter(p -> p > last_id)) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_TOO_LARGE_ID, tlid));
			}
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}


		// ### 2 ###
		try
		{
			pcl.stepSub("Mismatch [TYPE] <> [SERIESID]");

			List<Integer> errs = db.querySQL("SELECT LOCALID FROM ELEMENTS WHERE (TYPE=0 AND SERIESID<>-1) OR (TYPE=1 AND SERIESID<0) OR (TYPE<>0 AND TYPE<>1)", 1, o -> (int)o[0]);

			for (int errid : errs) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_TYPE_SID_MISMATCH, errid));
			}
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}


		// ### 3 ###
		try
		{
			pcl.stepSub("Missing series entry");

			List<Integer> errs = db.querySQL("SELECT DISTINCT SEASONS.SERIESID FROM SEASONS LEFT JOIN ELEMENTS ON SEASONS.SERIESID=ELEMENTS.SERIESID WHERE ELEMENTS.LOCALID IS NULL", 1, o -> (int)o[0]);

			for (int errid : errs) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_MISSING_SERIES, errid));
			}
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}


		// ### 4 ###
		try
		{
			pcl.stepSub("Missing seasons entry");

			List<Integer> errs = db.querySQL("SELECT DISTINCT EPISODES.SEASONID FROM EPISODES LEFT JOIN SEASONS ON EPISODES.SEASONID=SEASONS.SEASONID WHERE SEASONS.SEASONID IS NULL", 1, o -> (int)o[0]);

			for (int errid : errs) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_MISSING_SEASON, errid));
			}
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}


		// ### 5 ###
		try
		{
			pcl.stepSub("Validate CoverIDs");

			List<Integer> cvr1 = db.querySQL("SELECT COVERID FROM ELEMENTS WHERE COVERID <> -1", 1, o -> (int)o[0]);
			List<Integer> cvr2 = db.querySQL("SELECT COVERID FROM SEASONS WHERE COVERID <> -1",  1, o -> (int)o[0]);

			List<Integer> cvrall = db.querySQL("SELECT ID FROM COVERS",  1, o -> (int)o[0]);

			List<Integer> duplicates = CCStreams
					.iterate(cvr1, cvr2)
					.groupBy(p->p)
					.filter(p->p.getValue().size()>1)
					.map(Map.Entry::getKey)
					.enumerate();

			for (int dup : duplicates) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_MULTI_REF_COVER, dup));
			}

			for (int cvrid : CCStreams.iterate(cvrall).filter(p -> !cvr1.contains(p) && !cvr2.contains(p) )) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_UNUSED_COVERID, cvrid));
			}

			for (int cvrid : CCStreams.iterate(cvr1, cvr2).filter(p -> !cvrall.contains(p) )) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_DANGLING_COVERID, cvrid));
			}
		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}

		// ### 6 ###
		try
		{
			pcl.stepSub("Validate History trigger");

			if (movielist.getHistory().isHistoryActive()) {
				if (!movielist.getHistory().testTrigger(true, new RefParam<>()))
					e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_HTRIGGER_ENABLED_ERR, null));
			} else {
				if (!movielist.getHistory().testTrigger(false, new RefParam<>()))
					e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_HTRIGGER_DISABLED_ERR, null));
			}

		}
		catch (Exception ex)
		{
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DB_EXCEPTION, ex));
		}

	}
}
