package de.jClipCorn.database.databaseErrors;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.RomanNumberFormatter;
import de.jClipCorn.util.listener.ProgressCallbackListener;

public class DatabaseValidator {
	private final static CCDate MIN_DATE = CCDate.getMinimumDate();
	
	public static void startValidate(List<DatabaseError> e, CCMovieList ml, ProgressCallbackListener pcl) {
		pcl.setMax(ml.getElementCount() * 5); // 1x Normal  +  2x  checkCover  +  2x CheckFiles
		pcl.reset();
		
		for (CCDatabaseElement el : ml.getRawList()) {
			if (el.isMovie()) {
				CCMovie mov = (CCMovie) el;
				validateMovie(e, ml, mov);
			} else { // is Series
				CCSeries series = (CCSeries) el;
				validateSeries(e, ml, series);

				for (int seasi = 0; seasi < series.getSeasonCount(); seasi++) {
					CCSeason season = series.getSeason(seasi);
					validateSeason(e, ml, season);
					
					for (int epi = 0; epi < season.getEpisodeCount(); epi++) {
						CCEpisode episode = season.getEpisode(epi);
						validateEpisode(e, episode);
					}
				}
			}
			
			pcl.step();
		}
		
		findCoverErrors(e, ml, pcl);
		findDuplicateFiles(e, ml, pcl);
		findErrorInGroups(e, ml, pcl);
		findDuplicateOnlineRef(e, ml, pcl);
		
		pcl.reset();
	}
	
	private static double getMaxSizeFileDrift() {
		return CCProperties.getInstance().PROP_VALIDATE_FILESIZEDRIFT.getValue() / 100d;
	}
	
	private static double getRelativeDifference(long size1, long size2) {
		double diff = Math.abs(size1 - size2);
		long average = (size1 + size2)/2;
		return diff / average;
	}
	
	private static void validateSeries(List<DatabaseError> e, CCMovieList movielist, CCSeries series) {
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
		
		// ###############################################
		// cover not set
		// ###############################################

		if (series.getCoverName().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NOCOVERSET, series));
		}

		// ###############################################
		// cover not found
		// ###############################################

		if (! movielist.getCoverCache().coverExists(series.getCoverName())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_NOT_FOUND, series));
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (series.getAddDate().isLessEqualsThan(MIN_DATE) || series.getAddDate().isGreaterThan(CCDate.getCurrentDate())) {
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
		for (int i = 0; i < CCMovieGenreList.getMaxListSize(); i++) {
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
	}

	private static void validateMovie(List<DatabaseError> e, CCMovieList movielist, CCMovie mov) {
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

		// ###############################################
		// Moviesize <> Real size
		// ###############################################

		CCMovieSize size = new CCMovieSize();
		for (int i = 0; i < mov.getPartcount(); i++) {
			size.add(FileSizeFormatter.getFileSize(mov.getAbsolutePart(i)));
		}

		if (getRelativeDifference(size.getBytes(), mov.getFilesize().getBytes()) > getMaxSizeFileDrift()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_FILESIZE, mov));
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

		if (mov.getCoverName().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NOCOVERSET, mov));
		}

		// ###############################################
		// cover not found
		// ###############################################

		if (! movielist.getCoverCache().coverExists(mov.getCoverName())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_NOT_FOUND, mov));
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
			CCMovieFormat mfmt = CCMovieFormat.getMovieFormat(PathFormatter.getExtension(mov.getPart(i)));
			if (mfmt != null && mfmt.equals(mov.getFormat())) {
				rform = true;
			}
		}
		if (!rform) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS, mov));
		}

		// ###############################################
		// Inexistent Paths
		// ###############################################

		for (int i = 0; i < mov.getPartcount(); i++) {
			if (! new File(mov.getAbsolutePart(i)).exists()) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_PATH_NOT_FOUND, mov));
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
		// Wrong Quality
		// ###############################################
		
		if (CCMovieQuality.calculateQuality(mov.getFilesize(), mov.getLength(), mov.getPartcount()) != mov.getQuality()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_QUALITY, mov));
		}
		
		// ###############################################
		// Duplicate Name
		// ###############################################
		
		for (CCMovie imov : movielist.iteratorMovies()) {
			if (StringUtils.equalsIgnoreCase(imov.getCompleteTitle(), mov.getCompleteTitle()) && imov.getLanguage() == mov.getLanguage()) {
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
		// Watch later <> ViewedState
		// ###############################################
		
		if (mov.isViewed() && mov.getTag(CCMovieTags.TAG_WATCH_LATER)) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_LATER, mov));
		}
		
		// ###############################################
		// Watch never <> ViewedState
		// ###############################################
		
		if (mov.isViewed() && mov.getTag(CCMovieTags.TAG_WATCH_NEVER)) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_NEVER, mov));
		}
		
		// ###############################################
		// Duplicate Genre
		// ###############################################

		int[] g_count = new int[256];
		for (int i = 0; i < CCMovieGenreList.getMaxListSize(); i++) {
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
	}

	private static void validateSeason(List<DatabaseError> e, CCMovieList movielist, CCSeason season) {
		// ###############################################
		// no title set
		// ###############################################

		if (season.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, season));
		}
		
		// ###############################################
		// cover not found
		// ###############################################

		if (! movielist.getCoverCache().coverExists(season.getCoverName())) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_COVER_NOT_FOUND, season));
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (season.getEpisodeCount() > 0 && (season.getAddDate().isLessEqualsThan(MIN_DATE) || season.getAddDate().isGreaterThan(CCDate.getCurrentDate()))) {
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
	}

	private static void validateEpisode(List<DatabaseError> e, CCEpisode episode) {
		// ###############################################
		// no title set
		// ###############################################

		if (episode.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_TITLE_NOT_SET, episode));
		}
		
		// ###############################################
		// Wrong Format
		// ###############################################

		if (! episode.getFormat().equals(CCMovieFormat.getMovieFormat(PathFormatter.getExtension(episode.getPart())))) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_FORMAT_NOT_FOUND_IN_PARTS, episode));
		}

		// ###############################################
		// Inexistent Paths
		// ###############################################

		if (! new File(episode.getAbsolutePart()).exists()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_PATH_NOT_FOUND, episode));
		}
		
		// ###############################################
		// Moviesize <> Real size
		// ###############################################

		CCMovieSize size = new CCMovieSize(FileSizeFormatter.getFileSize(episode.getAbsolutePart()));

		if (getRelativeDifference(size.getBytes(), episode.getFilesize().getBytes()) > getMaxSizeFileDrift()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_FILESIZE, episode));
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
		// Wrong Quality
		// ###############################################
		
		if (CCMovieQuality.calculateQuality(episode.getFilesize(), episode.getLength(), 1) != episode.getQuality()) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_WRONG_QUALITY, episode));
		}
		
		// ###############################################
		// Watch later <> ViewedState
		// ###############################################
		
		if (episode.isViewed() && episode.getTag(CCMovieTags.TAG_WATCH_LATER)) {
			e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_IMPOSSIBLE_WATCH_LATER, episode));
		}
		
		// ###############################################
		// Watch never <> ViewedState
		// ###############################################
		
		if (episode.isViewed() && episode.getTag(CCMovieTags.TAG_WATCH_NEVER)) {
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
	}

	private static void findCoverErrors(List<DatabaseError> e, CCMovieList movielist, ProgressCallbackListener pcl) {
		// ###############################################
		// Duplicate Covers
		// ###############################################
		
		List<DatabaseCoverElement> cvrList = new ArrayList<>();
		
		for (CCDatabaseElement el : movielist.iterator()) {
			cvrList.add(new DatabaseCoverElement(el.getCoverName(), el));
			
			if (el.isSeries()) {
				for (int j = 0; j < ((CCSeries)el).getSeasonCount(); j++) {
					cvrList.add(new DatabaseCoverElement(((CCSeries)el).getSeason(j).getCoverName(), ((CCSeries)el).getSeason(j)));
				}
			}
			
			pcl.step();
		}
		
		Collections.sort(cvrList);
		
		for (int i = 1; i < cvrList.size(); i++) {
			if (cvrList.get(i).equalsCover(cvrList.get(i-1))) {
				e.add(DatabaseError.createDouble(DatabaseErrorType.ERROR_DUPLICATE_COVERLINK, cvrList.get(i-1).getElement(), cvrList.get(i).getElement()));
			}
			
			pcl.step();
		}
		
		// ###############################################
		// Too much Cover in Folder
		// ###############################################
		
		final String prefix = CCProperties.getInstance().PROP_COVER_PREFIX.getValue();
		final String suffix = "." + CCProperties.getInstance().PROP_COVER_TYPE.getValue();  //$NON-NLS-1$
		
		String[] files = movielist.getCoverCache().getCoverDirectory().list(new FilenameFilter() {
			
			@Override
			public boolean accept(File path, String name) {
				return name.startsWith(prefix) && name.endsWith(suffix);
			}
		});
		
		for (int i = 0; i < files.length; i++) {
			String cvrname = PathFormatter.getFilenameWithExt(files[i]);
			boolean found = false;
			for (int j = 0; j < cvrList.size(); j++) {
				found |= cvrList.get(j).getCover().equalsIgnoreCase(cvrname); // All hayl the Shortcut evaluation
			}
			if (! found) {
				e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_NONLINKED_COVERFILE, new File(movielist.getCoverCache().getCoverPath() + files[i])));
			}
		}
	}

	private static void findDuplicateFiles(List<DatabaseError> e, CCMovieList movielist, ProgressCallbackListener pcl) {
		boolean ignIFO = CCProperties.getInstance().PROP_VALIDATE_DUP_IGNORE_IFO.getValue();
		
		List<DatabaseFileElement> flList = new ArrayList<>();
		
		for (CCDatabaseElement el : movielist.iterator()) {
			if (el.isMovie()) {
				for (int i = 0; i < ((CCMovie)el).getPartcount(); i++) {
					if (ignIFO && CCMovieFormat.getMovieFormat(PathFormatter.getExtension(((CCMovie)el).getAbsolutePart(i))) == CCMovieFormat.IFO) {
						continue;
					}
					
					flList.add(new DatabaseFileElement(((CCMovie)el).getAbsolutePart(i), ((CCMovie)el)));
				}
			} else if (el.isSeries()) {
				CCSeries s = ((CCSeries)el);
				for (int i = 0; i < s.getSeasonCount(); i++) {
					CCSeason se = s.getSeason(i);
					for (int j = 0; j < se.getEpisodeCount(); j++) {
						if (ignIFO && CCMovieFormat.getMovieFormat(PathFormatter.getExtension(se.getEpisode(j).getAbsolutePart())) == CCMovieFormat.IFO) {
							continue;
						}
						
						flList.add(new DatabaseFileElement(se.getEpisode(j).getAbsolutePart(), se.getEpisode(j)));
					}
				}
			}
			
			pcl.step();
		}
		
		Collections.sort(flList);
		
		for (int i = 1; i < flList.size(); i++) {
			if (!flList.get(i).getPath().isEmpty() && flList.get(i).equalsPath(flList.get(i-1))) {
				e.add(DatabaseError.createDouble(DatabaseErrorType.ERROR_DUPLICATE_FILELINK, flList.get(i-1).getElement(), flList.get(i).getElement()));
			}
			
			pcl.step();
		}
	}

	private static void findErrorInGroups(List<DatabaseError> e, CCMovieList movielist, ProgressCallbackListener pcl) {
		Set<String> groupSet = new HashSet<>();
		for (CCGroup group : movielist.getGroupList()) {
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
	}
	
	private static void findDuplicateOnlineRef(List<DatabaseError> e, CCMovieList movielist, ProgressCallbackListener pcl) {
		Set<String> refSet = new HashSet<>();

		for (CCDatabaseElement el : movielist.iterator()) {
			if (el.getOnlineReference().isSet()) {
				if (! refSet.add(el.getLanguage().asInt() + '_' + el.getOnlineReference().toSerializationString())) {
					e.add(DatabaseError.createSingle(DatabaseErrorType.ERROR_DUPLICATE_REF, el));
				}
			}
		}
	}
}
