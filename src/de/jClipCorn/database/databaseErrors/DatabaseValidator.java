package de.jClipCorn.database.databaseErrors;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.gui.frames.checkDatabaseFrame.DatabaseCoverElement;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.FileSizeFormatter;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.ProgressCallbackListener;

public class DatabaseValidator {
	private final static double MAX_SIZEDRIFT = 0.05; // 5% //TODO Make this 5% into a setting (and will perhaps even 0% work ?)
	private final static CCDate MIN_DATE = CCDate.getNewMinimumDate();
	
	public static void startValidate(ArrayList<DatabaseError> e, CCMovieList ml, ProgressCallbackListener pcl) {
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
			
			//TODO: Check internal Database (Double SeriesID, Doubel SeasonID, SeriesID without seasons, Season with SeriesID but without Series etc etc etc)
		}
		
		validateCover(e, ml, pcl);
	}
	
	private static double getRelativeDifference(long size1, long size2) {
		double diff = Math.abs(size1 - size2);
		long average = (size1 + size2)/2;
		return diff / average;
	}
	
	private static void validateSeries(ArrayList<DatabaseError> e, CCMovieList movielist, CCSeries series) {
		// ###############################################
		// no title set
		// ###############################################

		if (series.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_TITLE_NOT_SET, series));
		}
		
		// ###############################################
		// Genre with wrong ID
		// ###############################################

		for (int i = 0; i < series.getGenreCount(); i++) {
			if (! series.getGenre(i).isValid()) {
				e.add(DatabaseError.createSingleAdditional(DatabaseError.ERROR_WRONG_GENREID, series, i));
			}
		}
		
		// ###############################################
		// cover not set
		// ###############################################

		if (series.getCoverName().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_NOCOVERSET, series));
		}

		// ###############################################
		// cover not found
		// ###############################################

		if (! movielist.getCoverCache().coverExists(series.getCoverName())) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_COVER_NOT_FOUND, series));
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (series.getAddDate().isLessEqualsThan(MIN_DATE) || series.getAddDate().isGreaterThan(new CCDate())) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_WRONG_ADDDATE, series));
		}

		// ###############################################
		// Zyklus/Title ends/starts with a space
		// ###############################################
		
		if (series.getTitle().startsWith(" ") || series.getTitle().endsWith(" ")) { //$NON-NLS-1$ //$NON-NLS-2$
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_NOT_TRIMMED, series));
		}
	}

	private static void validateMovie(ArrayList<DatabaseError> e, CCMovieList movielist, CCMovie mov) {
		// ###############################################
		// Hole in Genres
		// ###############################################

		if (mov.hasHoleInGenres()) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_INCONTINUOUS_GENRELIST, mov));
		}

		// ###############################################
		// Genre with wrong ID
		// ###############################################

		for (int i = 0; i < mov.getGenreCount(); i++) {
			if (!mov.getGenre(i).isValid()) {
				e.add(DatabaseError.createSingleAdditional(DatabaseError.ERROR_WRONG_GENREID, mov, i));
			}
		}

		// ###############################################
		// Moviesize <> Real size
		// ###############################################

		CCMovieSize size = new CCMovieSize();
		for (int i = 0; i < mov.getPartcount(); i++) {
			size.add(FileSizeFormatter.getFileSize(mov.getAbsolutePart(i)));
		}

		if (getRelativeDifference(size.getBytes(), mov.getFilesize().getBytes()) > MAX_SIZEDRIFT) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_WRONG_FILESIZE, mov));
		}

		// ###############################################
		// Genrecount <= 0
		// ###############################################

		if (mov.getGenreCount() <= 0) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_NOGENRE_SET, mov));
		}

		// ###############################################
		// cover not set
		// ###############################################

		if (mov.getCoverName().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_NOCOVERSET, mov));
		}

		// ###############################################
		// cover not found
		// ###############################################

		if (! movielist.getCoverCache().coverExists(mov.getCoverName())) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_COVER_NOT_FOUND, mov));
		}

		// ###############################################
		// no title set
		// ###############################################

		if (mov.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_TITLE_NOT_SET, mov));
		}

		// ###############################################
		// zyklusID == 0
		// ###############################################

		if (!mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() == 0) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_ZYKLUSNUMBER_IS_ZERO, mov));
		}

		// ###############################################
		// ZyklusID <> -1
		// ###############################################

		if (mov.getZyklus().getTitle().isEmpty() && mov.getZyklus().getNumber() != -1) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_ZYKLUSNUMBER_IS_NEGONE, mov));
		}

		// ###############################################
		// Hole in Parts
		// ###############################################

		if (mov.hasHoleInParts()) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_INCONTINUOUS_PARTS, mov));
		}

		// ###############################################
		// Wrong Format
		// ###############################################

		boolean rform = false;
		for (int i = 0; i < mov.getPartcount(); i++) {
			if (CCMovieFormat.getMovieFormat(PathFormatter.getExtension(mov.getPart(i))).equals(mov.getFormat())) {
				rform = true;
			}
		}
		if (!rform) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_FORMAT_NOT_FOUND_IN_PARTS, mov));
		}

		// ###############################################
		// Inexistent Paths
		// ###############################################

		for (int i = 0; i < mov.getPartcount(); i++) {
			if (!new File(mov.getAbsolutePart(i)).exists()) {
				e.add(DatabaseError.createSingle(DatabaseError.ERROR_PATH_NOT_FOUND, mov));
			}
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (mov.getDate().isLessEqualsThan(MIN_DATE) || mov.getDate().isGreaterThan(new CCDate())) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_WRONG_ADDDATE, mov));
		}

		// ###############################################
		// Zyklus/Title ends/starts with a space
		// ###############################################
		
		if (mov.getTitle().startsWith(" ") || mov.getTitle().endsWith(" ") || mov.getZyklus().getTitle().startsWith(" ") || mov.getZyklus().getTitle().endsWith(" ")) {  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_NOT_TRIMMED, mov));
		}
	}

	private static void validateSeason(ArrayList<DatabaseError> e, CCMovieList movielist, CCSeason season) {
		// ###############################################
		// no title set
		// ###############################################

		if (season.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_TITLE_NOT_SET, season));
		}
		
		// ###############################################
		// cover not found
		// ###############################################

		if (! movielist.getCoverCache().coverExists(season.getCoverName())) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_COVER_NOT_FOUND, season));
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (season.getAddDate().isLessEqualsThan(MIN_DATE) || season.getAddDate().isGreaterThan(new CCDate())) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_WRONG_ADDDATE, season));
		}

		// ###############################################
		// Zyklus/Title ends/starts with a space
		// ###############################################
		
		if (season.getTitle().startsWith(" ") || season.getTitle().endsWith(" ")) {  //$NON-NLS-1$//$NON-NLS-2$
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_NOT_TRIMMED, season));
		}
	}

	private static void validateEpisode(ArrayList<DatabaseError> e, CCEpisode episode) {
		// ###############################################
		// no title set
		// ###############################################

		if (episode.getTitle().isEmpty()) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_TITLE_NOT_SET, episode));
		}
		
		// ###############################################
		// Wrong Format
		// ###############################################

		if (! CCMovieFormat.getMovieFormat(PathFormatter.getExtension(episode.getPart())).equals(episode.getFormat())) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_FORMAT_NOT_FOUND_IN_PARTS, episode));
		}

		// ###############################################
		// Inexistent Paths
		// ###############################################

		if (! new File(episode.getAbsolutePart()).exists()) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_PATH_NOT_FOUND, episode));
		}
		
		// ###############################################
		// Moviesize <> Real size
		// ###############################################

		CCMovieSize size = new CCMovieSize(FileSizeFormatter.getFileSize(episode.getAbsolutePart()));

		if (getRelativeDifference(size.getBytes(), episode.getFilesize().getBytes()) > MAX_SIZEDRIFT) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_WRONG_FILESIZE, episode));
		}
		
		// ###############################################
		// Wrong AddDate
		// ###############################################
		
		if (episode.getAddDate().isLessEqualsThan(MIN_DATE) || episode.getAddDate().isGreaterThan(new CCDate())) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_WRONG_ADDDATE, episode));
		}
		
		// ###############################################
		// Wrong LastViewedDate
		// ###############################################
		
		if (episode.getLastViewed().isGreaterThan(new CCDate())) {
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_WRONG_LASTVIEWEDDATE, episode));
		}
		
		// ###############################################
		// Wrong Viewed State
		// ###############################################
		
		//if (episode.getLastViewed().isGreaterThan(MIN_DATE) ^ episode.isViewed()) { // Dar nicht benutzt werden - da leider durch den Import der alten CC-Database viele solche Fälle vorkommen
		//	e.add(DatabaseError.createSingle(DatabaseError.ERROR_15, episode));
		//}

		// ###############################################
		// Zyklus/Title ends/starts with a space
		// ###############################################
		
		if (episode.getTitle().startsWith(" ") || episode.getTitle().endsWith(" ")) {  //$NON-NLS-1$//$NON-NLS-2$
			e.add(DatabaseError.createSingle(DatabaseError.ERROR_NOT_TRIMMED, episode));
		}
	}

	private static void validateCover(ArrayList<DatabaseError> e, CCMovieList movielist, ProgressCallbackListener pcl) {
		ArrayList<DatabaseCoverElement> cvrList = new ArrayList<>();
		
		for (int i = 0; i < movielist.getElementCount(); i++) {
			CCDatabaseElement el = movielist.getDatabaseElementBySort(i);
			
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
				e.add(DatabaseError.createDouble(DatabaseError.ERROR_DUPLICATE_COVERLINK, cvrList.get(i-1).getElement(), cvrList.get(i).getElement()));
			}
			
			pcl.step();
		}
	}
}
