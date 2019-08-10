package de.jClipCorn.features.databaseErrors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.covertab.CCDefaultCoverCache;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.covertab.ICoverCache;

import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.RomanNumberFormatter;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.lambda.Func0to1WithIOException;

public class DatabaseValidator {
	private final static CCDate MIN_DATE = CCDate.getMinimumDate();
	
	public static void startValidate(List<DatabaseError> e, CCMovieList ml, DatabaseValidatorOptions opt, DoubleProgressCallbackListener pcl)
	{
		boolean validateElements = opt.ValidateMovies || opt.ValidateSeries || opt.ValidateSeasons || opt.ValidateEpisodes;

		int outerCount = 0;
		if (validateElements) outerCount++;               // [1]
		if (opt.ValidateCovers) outerCount++;             // [2]
		if (opt.ValidateCoverFiles) outerCount++;         // [3]
		if (opt.ValidateAdditional) outerCount++;         // [4]
		if (opt.ValidateGroups) outerCount++;             // [5]
		if (opt.ValidateOnlineReferences) outerCount++;   // [6]

		pcl.setMaxAndResetValueBoth(outerCount+1, 1);

		if (validateElements)
		{
			pcl.stepRootAndResetSub("Validate database elements", ml.getTotalDatabaseElementCount()); //$NON-NLS-1$

			for (CCDatabaseElement el : ml.getRawList()) {

				if (el.isMovie()) {
					CCMovie mov = (CCMovie) el;
					pcl.stepSub(mov.getFullDisplayTitle());

					if (opt.ValidateMovies) validateMovie(e, ml, mov, opt);
				}
				else // is Series
				{
					CCSeries series = (CCSeries) el;
					pcl.stepSub(series.getFullDisplayTitle());

					if (opt.ValidateSeries) validateSeries(e, ml, series, opt);

					for (int seasi = 0; seasi < series.getSeasonCount(); seasi++) {
						CCSeason season = series.getSeasonByArrayIndex(seasi);
						pcl.stepSub(series.getTitle() + " - S" + season.getSeasonNumber()); //$NON-NLS-1$

						if (opt.ValidateSeasons) validateSeason(e, ml, season, opt);

						for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
							CCEpisode episode = season.getEpisodeByArrayIndex(epi);
							pcl.stepSub(series.getTitle() + " - S" + season.getSeasonNumber() + "E" + episode.getEpisodeNumber()); //$NON-NLS-1$ //$NON-NLS-2$

							if (opt.ValidateEpisodes) validateEpisode(e, episode, opt);
						}
					}
				}

			}
		}
		
		if (opt.ValidateCovers) findCoverErrors(e, ml, pcl);

		if (opt.ValidateCoverFiles) findCoverFileErrors(e, ml, pcl);

		if (opt.ValidateAdditional) findDuplicateFiles(e, ml, pcl);

		if (opt.ValidateGroups) findErrorInGroups(e, ml, pcl);

		if (opt.ValidateOnlineReferences) findDuplicateOnlineRef(e, ml, pcl);
		
		pcl.reset();
	}
	
	private static double getMaxSizeFileDrift() {
		return CCProperties.getInstance().PROP_VALIDATE_FILESIZEDRIFT.getValue() / 100d;
	}
	
	private static double getRelativeDifference(long size1, long size2)
	{
		double diff = Math.abs(size1 - size2);
		long average = (size1 + size2)/2;
		return diff / average;
	}
	
	private static void validateSeries(List<DatabaseError> e, CCMovieList movielist, CCSeries series, DatabaseValidatorOptions opt)
	{
		// ###############################################
		// no title set
		// ###############################################

		if (series.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, series));
		}
		
		// ###############################################
		// Genre with wrong ID
		// ###############################################

		for (int i = 0; i < series.getGenreCount(); i++) {
			if (! series.getGenre(i).isValid()) {
				e.add(DatabaseError.createSingleAdditional(DatabaseErrorType.ERROR_WRONG_GENREID, series, i));
			}
		}

		if (opt.ValidateCoverFiles)
		{
			// ###############################################
			// cover not set
			// ###############################################

			if (series.getCoverID() == -1) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NOCOVERSET, series));
			}
		}

		// ###############################################
		// cover not found
		// ###############################################

		if (! movielist.getCoverCache().coverFileExists(series.getCoverID())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_NOT_FOUND, series));
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (!series.isEmpty() && (series.getAddDate().isLessEqualsThan(MIN_DATE) || series.getAddDate().isGreaterThan(CCDate.getCurrentDate()))) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_ADDDATE, series));
		}

		// ###############################################
		// Zyklus/Title ends/starts with a space
		// ###############################################
		
		if (PathFormatter.isUntrimmed(series.getTitle())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NOT_TRIMMED, series));
		}
		
		// ###############################################
		// Duplicate Genre
		// ###############################################

		int[] g_count = new int[256];
		for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
			g_count[series.getGenre(i).asInt()]++;
		}
		
		for (int i = 1; i < 256; i++) {
			if (g_count[i] > 1) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DUPLICATE_GENRE, series));
				break;
			}
		}
		
		// ###############################################
		// Invalid CCOnlineRef
		// ###############################################

		if (! series.getOnlineReference().isValid()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_ONLINEREF, series));
		}
		
		// ###############################################
		// Duplicate/Empty Groups
		// ###############################################

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

		// ###############################################
		// Cover too small
		// ###############################################

		if (series.getCoverDimensions().Item1 < ImageUtilities.BASE_COVER_WIDTH && series.getCoverDimensions().Item2 < ImageUtilities.BASE_COVER_HEIGHT) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_SMALL, series));
		}

		// ###############################################
		// Cover too big
		// ###############################################

		if (series.getCoverDimensions().Item1 > ImageUtilities.getCoverWidth() && series.getCoverDimensions().Item2 < ImageUtilities.getCoverHeight()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_BIG, series));
		}

		// ###############################################
		// Invalid Tag for series
		// ###############################################

		for (CCSingleTag t : series.getTags().iterate()) {
			if (!t.IsSeriesTag) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_SERIES, series));
		}
	}

	private static void validateMovie(List<DatabaseError> e, CCMovieList movielist, CCMovie mov, DatabaseValidatorOptions opt)
	{
		// ###############################################
		// Hole in Genres
		// ###############################################

		if (mov.hasHoleInGenres()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INCONTINUOUS_GENRELIST, mov));
		}

		// ###############################################
		// Genre with wrong ID
		// ###############################################

		for (int i = 0; i < mov.getGenreCount(); i++) {
			if (!mov.getGenre(i).isValid()) {
				e.add(DatabaseError.createSingleAdditional(DatabaseErrorType.ERROR_WRONG_GENREID, mov, i));
			}
		}

		if (opt.ValidateVideoFiles)
		{
			// ###############################################
			// Moviesize <> Real size
			// ###############################################

			CCFileSize size = CCFileSize.ZERO;
			for (int i = 0; i < mov.getPartcount(); i++) {
				size = CCFileSize.addBytes(size, FileSizeFormatter.getFileSize(mov.getAbsolutePart(i)));
			}

			if (getRelativeDifference(size.getBytes(), mov.getFilesize().getBytes()) > getMaxSizeFileDrift()) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_FILESIZE, mov));
			}
		}

		// ###############################################
		// Genrecount <= 0
		// ###############################################

		if (mov.getGenreCount() <= 0) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NOGENRE_SET, mov));
		}

		// ###############################################
		// cover not set
		// ###############################################

		if (mov.getCoverID() == -1) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NOCOVERSET, mov));
		}

		if (opt.ValidateCoverFiles)
		{
			// ###############################################
			// cover not found
			// ###############################################

			if (!movielist.getCoverCache().coverFileExists(mov.getCoverID())) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_NOT_FOUND, mov));
			}
		}

		// ###############################################
		// no title set
		// ###############################################

		if (mov.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, mov));
		}

		// ###############################################
		// zyklusID == 0
		// ###############################################

		if (!mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() == -1) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_ZYKLUSNUMBER_IS_NEGONE, mov));
		}

		// ###############################################
		// ZyklusID <> -1
		// ###############################################

		if (mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() != -1) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_ZYKLUSTITLE_IS_EMPTY, mov));
		}

		// ###############################################
		// Hole in Parts
		// ###############################################

		if (mov.hasHoleInParts()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INCONTINUOUS_PARTS, mov));
		}

		// ###############################################
		// Wrong Format
		// ###############################################

		boolean rform = false;
		for (int i = 0; i < mov.getPartcount(); i++) {
			CCFileFormat mfmt = CCFileFormat.getMovieFormat(PathFormatter.getExtension(mov.getPart(i)));
			if (mfmt != null && mfmt.equals(mov.getFormat())) {
				rform = true;
			}
		}
		if (!rform) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS, mov));
		}

		if (opt.ValidateVideoFiles)
		{
			// ###############################################
			// Inexistent Paths
			// ###############################################

			for (int i = 0; i < mov.getPartcount(); i++) {
				if (! new File(mov.getAbsolutePart(i)).exists()) {
					e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_PATH_NOT_FOUND, mov));
				}
			}
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (mov.getAddDate().isLessEqualsThan(MIN_DATE) || mov.getAddDate().isGreaterThan(CCDate.getCurrentDate())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_ADDDATE, mov));
		}

		// ###############################################
		// Zyklus/Title ends/starts with a space
		// ###############################################
				
		if (PathFormatter.isUntrimmed(mov.getTitle()) || PathFormatter.isUntrimmed(mov.getZyklus().getTitle())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NOT_TRIMMED, mov));
		}
		
		// ###############################################
		// Zyklus ends with Roman
		// ###############################################
		
		if (RomanNumberFormatter.endsWithRoman(mov.getZyklus().getTitle())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_ZYKLUS_ENDS_WITH_ROMAN, mov));
		}
		
		// ###############################################
		// MediaInfo problems
		// ###############################################
		
		//TODO
		
		// ###############################################
		// Duplicate Name
		// ###############################################
		
		for (CCMovie imov : movielist.iteratorMovies()) {
			if (StringUtils.equalsIgnoreCase(imov.getCompleteTitle(), mov.getCompleteTitle()) && (imov.getLanguage().isSubsetOf(mov.getLanguage()) || mov.getLanguage().isSubsetOf(imov.getLanguage()))) {
				if (mov.getLocalID() != imov.getLocalID()) {
					e.add(DatabaseError.createDouble(DatabaseErrorType.ERROR_DUPLICATE_TITLE, mov, imov));
				}
				break;
			}
		}
		
		// ###############################################
		// Wrong filename
		// ###############################################
		
		boolean wrongfn = false;
		for (int i = 0; i < mov.getPartcount(); i++) {
			if (! PathFormatter.getFilenameWithExt(mov.getAbsolutePart(i)).equals(mov.generateFilename(i))) {
				wrongfn = true;
			}
		}
		if (wrongfn) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_FILENAME, mov));
		}
		
		// ###############################################
		// Watch never <> ViewedState
		// ###############################################
		
		if (mov.isViewed() && mov.getTag(CCTagList.TAG_WATCH_NEVER)) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER, mov));
		}
		
		// ###############################################
		// Duplicate Genre
		// ###############################################

		int[] g_count = new int[256];
		for (int i = 0; i < CCGenreList.getMaxListSize(); i++) {
			g_count[mov.getGenre(i).asInt()]++;
		}
		
		for (int i = 1; i < 256; i++) {
			if (g_count[i] > 1) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DUPLICATE_GENRE, mov));
				break;
			}
		}
		
		// ###############################################
		// Invalid path characters
		// ###############################################
		
		for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
			if (PathFormatter.containsIllegalPathSymbolsInSerializedFormat(mov.getPart(i))){
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH, mov));
				break;
			}
		}
		
		// ###############################################
		// Invalid CCOnlineRef
		// ###############################################

		if (! mov.getOnlineReference().isValid()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_ONLINEREF, mov));
		}
		
		// ###############################################
		// History but not viewed
		// ###############################################

		if (!mov.isViewed() && !mov.getViewedHistory().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED, mov));
		}
		
		// ###############################################
		// History is invalid
		// ###############################################

		if (! mov.getViewedHistory().isValid()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_HISTORY, mov));
		}
		
		// ###############################################
		// Duplicate/Empty Groups
		// ###############################################

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

		// ###############################################
		// Viewed but no history
		// ###############################################

		if (mov.isViewed() && mov.getViewedHistory().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY, mov));
		}

		// ###############################################
		// Cover too small
		// ###############################################

		if (mov.getCoverDimensions().Item1 < ImageUtilities.BASE_COVER_WIDTH && mov.getCoverDimensions().Item2 < ImageUtilities.BASE_COVER_HEIGHT) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_SMALL, mov));
		}

		// ###############################################
		// Cover too big
		// ###############################################

		if (mov.getCoverDimensions().Item1 > ImageUtilities.getCoverWidth() && mov.getCoverDimensions().Item2 < ImageUtilities.getCoverHeight()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_BIG, mov));
		}

		// ###############################################
		// Invalid Tag for movies
		// ###############################################

		for (CCSingleTag t : mov.getTags().iterate()) {
			if (!t.IsMovieTag) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_MOVIE, mov));
		}

		// ###############################################
		// Not-normalized path
		// ###############################################

		for (int i = 0; i < mov.getPartcount(); i++) {
			if (!PathFormatter.getCCPath(mov.getAbsolutePart(i)).equals(mov.getPart(i))) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NON_NORMALIZED_PATH, mov));
				break;
			}
		}

		// ###############################################
		// No language
		// ###############################################

		if (mov.getLanguage().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NO_LANG, mov));
		}

		// ###############################################
		// Muted language must be single
		// ###############################################

		if (mov.getLanguage().contains(CCDBLanguage.MUTED) && !mov.getLanguage().isSingle()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_LANG_MUTED_SUBSET, mov));
		}
	}

	private static void validateSeason(List<DatabaseError> e, CCMovieList movielist, CCSeason season, DatabaseValidatorOptions opt)
	{
		// ###############################################
		// no title set
		// ###############################################

		if (season.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, season));
		}

		if (opt.ValidateCoverFiles)
		{
			// ###############################################
			// cover not found
			// ###############################################

			if (! movielist.getCoverCache().coverFileExists(season.getCoverID())) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_NOT_FOUND, season));
			}
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (!season.isEmpty() && (season.getAddDate().isLessEqualsThan(MIN_DATE) || season.getAddDate().isGreaterThan(CCDate.getCurrentDate()))) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_ADDDATE, season));
		}

		// ###############################################
		// Zyklus/Title ends/starts with a space
		// ###############################################
		
		if (PathFormatter.isUntrimmed(season.getTitle())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NOT_TRIMMED, season));
		}

		// ###############################################
		// Non-continoous episode numbers
		// ###############################################
		
		if (! season.isContinoousEpisodeNumbers()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NON_CONTINOOUS_EPISODES, season));
		}

		// ###############################################
		// Cover too small
		// ###############################################

		if (season.getCoverDimensions().Item1 < ImageUtilities.BASE_COVER_WIDTH && season.getCoverDimensions().Item2 < ImageUtilities.BASE_COVER_HEIGHT) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_SMALL, season));
		}

		// ###############################################
		// Cover too big
		// ###############################################

		if (season.getCoverDimensions().Item1 > ImageUtilities.getCoverWidth() && season.getCoverDimensions().Item2 < ImageUtilities.getCoverHeight()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_TOO_BIG, season));
		}
	}

	private static void validateEpisode(List<DatabaseError> e, CCEpisode episode, DatabaseValidatorOptions opt)
	{
		// ###############################################
		// no title set
		// ###############################################

		if (episode.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, episode));
		}
		
		// ###############################################
		// Wrong Format
		// ###############################################

		if (! episode.getFormat().equals(CCFileFormat.getMovieFormat(PathFormatter.getExtension(episode.getPart())))) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS, episode));
		}

		if (opt.ValidateVideoFiles)
		{
			// ###############################################
			// Inexistent Paths
			// ###############################################

			if (! new File(episode.getAbsolutePart()).exists()) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_PATH_NOT_FOUND, episode));
			}
		}

		if (opt.ValidateVideoFiles)
		{
			// ###############################################
			// Moviesize <> Real size
			// ###############################################

			CCFileSize size = new CCFileSize(FileSizeFormatter.getFileSize(episode.getAbsolutePart()));

			if (getRelativeDifference(size.getBytes(), episode.getFilesize().getBytes()) > getMaxSizeFileDrift()) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_FILESIZE, episode));
			}
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (episode.getAddDate().isLessEqualsThan(MIN_DATE) || episode.getAddDate().isGreaterThan(CCDate.getCurrentDate())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_ADDDATE, episode));
		}
		
		// ###############################################
		// Wrong LastViewedDate
		// ###############################################
		
		if (episode.getViewedHistoryLast().isGreaterThan(CCDate.getCurrentDate())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_LASTVIEWEDDATE, episode));
		}
		
		// ###############################################
		// Wrong Viewed State
		// ###############################################
		
		//if (episode.getLastViewed().isGreaterThan(MIN_DATE) ^ episode.isViewed()) { // Dar nicht benutzt werden - da leider durch den Import der alten CC-Database viele solche Fälle vorkommen
		//	e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_15, episode));
		//}

		// ###############################################
		// Zyklus ends/starts with a space
		// ###############################################
		
		if (PathFormatter.isUntrimmed(episode.getTitle())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NOT_TRIMMED, episode));
		}

		// ###############################################
		// MediaInfo problems
		// ###############################################
		
		//TODO
		
		// ###############################################
		// Watch never <> ViewedState
		// ###############################################
		
		if (episode.isViewed() && episode.getTag(CCTagList.TAG_WATCH_NEVER)) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER, episode));
		}
		
		// ###############################################
		// LastViewed too small
		// ###############################################
		
		if (!episode.getViewedHistoryLast().isMinimum() && episode.getViewedHistoryLast().isLessThan(CCDate.create(1, 6, 1900))) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_LASTWATCHED_TOO_OLD, episode));
		}
		
		// ###############################################
		// Folderstructure not formatted
		// ###############################################
		
		if (CCProperties.getInstance().PROP_VALIDATE_CHECK_SERIES_STRUCTURE.getValue() && ! episode.checkFolderStructure()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_SERIES_STRUCTURE, episode));
		}
		
		// ###############################################
		// Invalid path characters
		// ###############################################

		if (PathFormatter.containsIllegalPathSymbolsInSerializedFormat(episode.getPart())){
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_CHARS_IN_PATH, episode));
		}
		
		// ###############################################
		// History but not viewed
		// ###############################################

		if (!episode.isViewed() && !episode.getViewedHistory().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_HISTORY_BUT_UNVIEWED, episode));
		}

		// ###############################################
		// Viewed but no history
		// ###############################################

		if (episode.isViewed() && episode.getViewedHistory().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_VIEWED_BUT_NO_HISTORY, episode));
		}
		
		// ###############################################
		// History is invalid
		// ###############################################

		if (! episode.getViewedHistory().isValid()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_INVALID_HISTORY, episode));
		}

		// ###############################################
		// Invalid Tag for episode
		// ###############################################

		for (CCSingleTag t : episode.getTags().iterate()) {
			if (!t.IsEpisodeTag) e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TAG_NOT_VALID_ON_EPISODE, episode));
		}

		// ###############################################
		// Not-normalized path
		// ###############################################

		if (! PathFormatter.getCCPath(episode.getAbsolutePart()).equals(episode.getPart())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NON_NORMALIZED_PATH, episode));
		}

		// ###############################################
		// No language
		// ###############################################

		if (episode.getLanguage().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NO_LANG, episode));
		}

		// ###############################################
		// Muted language must be single
		// ###############################################

		if (episode.getLanguage().contains(CCDBLanguage.MUTED) && !episode.getLanguage().isSingle()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_LANG_MUTED_SUBSET, episode));
		}
	}

	private static void findCoverErrors(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
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

		HashSet<Integer> coverIDsTable = CCStreams.iterate(cc.listCovers()).map(p->p.ID).toSet();

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

	private static void findCoverFileErrors(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
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

	private static void findDuplicateFiles(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
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

	private static void findErrorInGroups(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
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
	
	private static void findDuplicateOnlineRef(List<DatabaseError> e, CCMovieList movielist, DoubleProgressCallbackListener pcl)
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
}
