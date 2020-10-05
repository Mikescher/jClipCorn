package de.jClipCorn.features.userdataProblem;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.datapacks.IEpisodeData;
import de.jClipCorn.database.databaseElement.datapacks.IMovieData;
import de.jClipCorn.database.databaseElement.datapacks.ISeasonData;
import de.jClipCorn.database.databaseElement.datapacks.ISeriesData;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.RomanNumberFormatter;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UserDataProblem {
	public final static int PROBLEM_NO_PATH = 1;
	public final static int PROBLEM_HOLE_IN_PATH = 2;
	public final static int PROBLEM_EMPTY_TITLE = 3;
	public final static int PROBLEM_ZYKLUSID_IS_SET = 4; // Zyklus is empty but ZyklusID != -1
	public final static int PROBLEM_ZYKLUSID_IS_NEGONE = 5; // Zyklus is set but ZyklusID is -1
	public final static int PROBLEM_NO_COVER_SET = 6;
	public final static int PROBLEM_INVALID_LENGTH = 7;
	public final static int PROBLEM_DATE_TOO_LESS = 8;
	public final static int PROBLEM_INVALID_ONLINESCORE = 9;
	public final static int PROBLEM_FSK_NOT_SET = 10;
	public final static int PROBLEM_INVALID_YEAR = 11;
	public final static int PROBLEM_INVALID_FILESIZE = 12;
	public final static int PROBLEM_EXTENSION_UNEQUALS_FILENAME = 13;
	public final static int PROBLEM_NO_GENRE_SET = 14;
	public final static int PROBLEM_HOLE_IN_GENRE = 15;
	public final static int PROBLEM_EPISODENUMBER_ALREADY_EXISTS = 16;
	public final static int PROBLEM_ZYKLUSORTITLE_HAS_LEADINGORTRAILING_SPACES = 17;
	public final static int PROBLEM_ZYKLUS_ALREADY_EXISTS = 18;
	public final static int PROBLEM_ZYKLUS_ENDS_WITH_ROMAN = 19;
	public final static int PROBLEM_WRONG_QUALITY = 20;
	public final static int PROBLEM_TITLE_ALREADYEXISTS = 21;
	public final static int PROBLEM_FILE_ALREADYEXISTS = 22;
	public final static int PROBLEM_DUPLICATE_GENRE = 23;
	public final static int PROBLEM_INVALID_PATH_CHARACTERS = 24;
	public final static int PROBLEM_INVALID_REFERENCE = 25;
	public final static int PROBLEM_NO_COVER = 26;
	public final static int PROBLEM_INPUT_FILE_NOT_FOUND = 27;
	public final static int PROBLEM_DESTINTAION_FILE_ALREADY_EXISTS = 28;
	public final static int PROBLEM_NO_LANG = 29;
	public final static int PROBLEM_LANG_MUTED_SUBSET = 30;
	public final static int PROBLEM_MEDIAINFO_UNSET = 31;
	public final static int PROBLEM_MEDIAINFO_WRONG_FILESIZE = 32;
	public final static int PROBLEM_MEDIAINFO_WRONG_DATA = 33;

	private final int pid; // Problem ID
	private final Object[] additional; // Problem ID

	public UserDataProblem(int problemID, String... param) {
		this.pid = problemID;
		additional = param;
	}
	
	public String getText() {
		return LocaleBundle.getFormattedString(String.format("UserDataErrors.ERROR_%02d", getPID()), additional); //$NON-NLS-1$
	}
	
	public int getPID() {
		return pid;
	}
	
	//####################################################################################################################################################
	//####################################################################################################################################################
	//####################################################################################################################################################
	
	public static void testMovieData(List<UserDataProblem> ret, CCMovieList ml, CCMovie movieSource, IMovieData newdata)
	{
		int partcount_nonempty = CCStreams.iterate(newdata.getParts()).count(p -> !Str.isNullOrWhitespace(p));

		var gen0 = newdata.getGenres().getGenre(0).asInt();
		var gen1 = newdata.getGenres().getGenre(1).asInt();
		var gen2 = newdata.getGenres().getGenre(2).asInt();
		var gen3 = newdata.getGenres().getGenre(3).asInt();
		var gen4 = newdata.getGenres().getGenre(4).asInt();
		var gen5 = newdata.getGenres().getGenre(5).asInt();
		var gen6 = newdata.getGenres().getGenre(6).asInt();
		var gen7 = newdata.getGenres().getGenre(7).asInt();

		var p0 = newdata.getParts().size() <= 0 ? Str.Empty : newdata.getParts().get(0);
		var p1 = newdata.getParts().size() <= 1 ? Str.Empty : newdata.getParts().get(1);
		var p2 = newdata.getParts().size() <= 2 ? Str.Empty : newdata.getParts().get(2);
		var p3 = newdata.getParts().size() <= 3 ? Str.Empty : newdata.getParts().get(3);
		var p4 = newdata.getParts().size() <= 4 ? Str.Empty : newdata.getParts().get(4);
		var p5 = newdata.getParts().size() <= 5 ? Str.Empty : newdata.getParts().get(5);

		//################################################################################################################
		
		if (partcount_nonempty == 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_PATH));
		}
		
		//################################################################################################################
		
		if (CCStreams.iterate(newdata.getParts()).any(p -> !Str.isNullOrWhitespace(p)) &&
			CCStreams.iterate(newdata.getParts()).findIndex(Str::isNullOrWhitespace) < CCStreams.iterate(newdata.getParts()).findLastIndex(p -> !Str.isNullOrWhitespace(p))) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_HOLE_IN_PATH));
		}
		
		//################################################################################################################
		
		if (newdata.getTitle().isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
		}
		
		//################################################################################################################
		
		if (newdata.getZyklus().getTitle().isEmpty() && newdata.getZyklus().getNumber() != -1) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSID_IS_SET));
		}
		
		//################################################################################################################
		
		if (! newdata.getZyklus().getTitle().isEmpty() && newdata.getZyklus().getNumber() == -1) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSID_IS_NEGONE));
		}
		
		//################################################################################################################
		
		CCMovie foundM;
		if (! newdata.getZyklus().getTitle().isEmpty() && (foundM = ml.findfirst(newdata.getZyklus())) != null) {
			if (movieSource == null || movieSource.getLocalID() != foundM.getLocalID()) {
				ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUS_ALREADY_EXISTS));
			}
		}
		
		//################################################################################################################
		
		if (newdata.getLength() <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_LENGTH));
		}
		
		//################################################################################################################

		if (newdata.getAddDate().isLessEqualsThan(CCDate.getMinimumDate())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DATE_TOO_LESS));
		}
		
		//################################################################################################################
		
		if (newdata.getOnlinescore() == null || newdata.getOnlinescore().asInt() <= 0 || newdata.getOnlinescore().asInt() > 10) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_ONLINESCORE));
		}
		
		//################################################################################################################
		
		if (newdata.getFSK() == null) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_FSK_NOT_SET));
		}
		
		//################################################################################################################

		List<String> extensions = new ArrayList<>();

		for (var p : newdata.getParts()) extensions.add(PathFormatter.getExtension(p).toLowerCase());

		if (! (extensions.contains(newdata.getFormat().asString().toLowerCase()) || extensions.contains(newdata.getFormat().asStringAlt().toLowerCase()))) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EXTENSION_UNEQUALS_FILENAME));
		}
		
		//################################################################################################################
		
		if (newdata.getYear() <= CCDate.YEAR_MIN) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_YEAR));
		}
		
		//################################################################################################################
		
		if (newdata.getFilesize().getBytes() <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_FILESIZE));
		}
		
		//################################################################################################################
		
		int gc = gen0 + gen1 + gen2 + gen3 + gen4 + gen5 + gen6 + gen7;

		if (gc <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_GENRE_SET));
		}
		//################################################################################################################
		
		if ((gen0 == 0 && gen1 != 0) || (gen1 == 0 && gen2 != 0) || (gen2 == 0 && gen3 != 0) || (gen3 == 0 && gen4 != 0) || (gen4 == 0 && gen5 != 0) || (gen5 == 0 && gen6 != 0) || (gen6 == 0 && gen7 != 0)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_HOLE_IN_GENRE));
		}
		
		//################################################################################################################

		int[] g_count = new int[256];
		g_count[gen0]++;
		g_count[gen1]++;
		g_count[gen2]++;
		g_count[gen3]++;
		g_count[gen4]++;
		g_count[gen5]++;
		g_count[gen6]++;
		g_count[gen7]++;
		
		for (int i = 1; i < 256; i++) {
			if (g_count[i] > 1) {
				ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DUPLICATE_GENRE));
				break;
			}
		}
		
		//################################################################################################################
		
		if (newdata.getCover() == null) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER_SET));
		}
		
		//################################################################################################################
		
		if (PathFormatter.isUntrimmed(newdata.getTitle()) || PathFormatter.isUntrimmed(newdata.getTitle())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSORTITLE_HAS_LEADINGORTRAILING_SPACES));
		}
		
		//################################################################################################################
		
		if (RomanNumberFormatter.endsWithRoman(newdata.getZyklus().getTitle())) {
			ret.add(new UserDataProblem(PROBLEM_ZYKLUS_ENDS_WITH_ROMAN));
		}
		
		//################################################################################################################

		if (!newdata.getMediaInfo().isSet()) {
			ret.add(new UserDataProblem(PROBLEM_MEDIAINFO_UNSET));
		} else {
			if (newdata.getMediaInfo().getFilesize() != newdata.getFilesize().getBytes() && partcount_nonempty == 1) ret.add(new UserDataProblem(PROBLEM_MEDIAINFO_WRONG_FILESIZE));
			String err = newdata.getMediaInfo().validate();
			if (err != null) ret.add(new UserDataProblem(PROBLEM_MEDIAINFO_WRONG_DATA, err));
		}

		//################################################################################################################
		
		for (CCMovie imov : ml.iteratorMovies()) {
			if (StringUtils.equalsIgnoreCase(imov.getTitle(), newdata.getTitle())
			&& 	StringUtils.equalsIgnoreCase(imov.getZyklus().getTitle(), newdata.getZyklus().getTitle())
			&&  imov.getLanguage() == newdata.getLanguage()) {
				if (movieSource == null || movieSource.getLocalID() != imov.getLocalID()) {
					ret.add(new UserDataProblem(PROBLEM_TITLE_ALREADYEXISTS));
				}
				break;
			}
		}
		
		//################################################################################################################
		
		for (CCDatabaseElement idel : ml.iteratorElements()) {
			if (idel.isMovie()) {
				if (isPathIncluded((CCMovie)idel, p0, p1, p2, p3, p4, p5)) {
					if (movieSource == null || movieSource.getLocalID() != idel.getLocalID()) {
						ret.add(new UserDataProblem(PROBLEM_FILE_ALREADYEXISTS));
					}
					break;
				}
			} else if (idel.isSeries()) {
				CCSeries ss = (CCSeries) idel;
				for (int i = 0; i < ss.getSeasonCount(); i++) {
					CCSeason seas = ss.getSeasonByArrayIndex(i);
					for (int j = 0; j < seas.getEpisodeCount(); j++) {
						if (isPathIncluded(seas.getEpisodeByArrayIndex(j), p0, p1, p2, p3, p4, p5)) {
							ret.add(new UserDataProblem(PROBLEM_FILE_ALREADYEXISTS));
							break;
						}
					}
				}
			}
		}
		
		//################################################################################################################
		
		if (PathFormatter.containsIllegalPathSymbolsInSerializedFormat(p0) || 
				PathFormatter.containsIllegalPathSymbolsInSerializedFormat(p1) || 
				PathFormatter.containsIllegalPathSymbolsInSerializedFormat(p2) || 
				PathFormatter.containsIllegalPathSymbolsInSerializedFormat(p3) || 
				PathFormatter.containsIllegalPathSymbolsInSerializedFormat(p4) || 
				PathFormatter.containsIllegalPathSymbolsInSerializedFormat(p5)) {
			
			ret.add(new UserDataProblem(PROBLEM_INVALID_PATH_CHARACTERS));
		}
		
		//################################################################################################################
		
		if (!newdata.getOnlineReference().isValid()) {
			ret.add(new UserDataProblem(PROBLEM_INVALID_REFERENCE));
		}

		//################################################################################################################
		
		if (newdata.getLanguage().isEmpty()) {
			ret.add(new UserDataProblem(PROBLEM_NO_LANG));
		}
		
		if (newdata.getLanguage().contains(CCDBLanguage.MUTED) && !newdata.getLanguage().isSingle()) {
			ret.add(new UserDataProblem(PROBLEM_LANG_MUTED_SUBSET));
		}
	}
	
	public static void testSeriesData(List<UserDataProblem> ret, CCMovieList ml, CCSeries seriesSource, ISeriesData newdata) {

		var gen0 = newdata.getGenres().getGenre(0).asInt();
		var gen1 = newdata.getGenres().getGenre(1).asInt();
		var gen2 = newdata.getGenres().getGenre(2).asInt();
		var gen3 = newdata.getGenres().getGenre(3).asInt();
		var gen4 = newdata.getGenres().getGenre(4).asInt();
		var gen5 = newdata.getGenres().getGenre(5).asInt();
		var gen6 = newdata.getGenres().getGenre(6).asInt();
		var gen7 = newdata.getGenres().getGenre(7).asInt();

		//################################################################################################################

		if (newdata.getTitle().isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
		}
		
		//################################################################################################################
		
		if (newdata.getOnlinescore() == null || newdata.getOnlinescore().asInt() <= 0 || newdata.getOnlinescore().asInt() > 10) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_ONLINESCORE));
		}
		
		//################################################################################################################
		
		if (newdata.getFSK() == null) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_FSK_NOT_SET));
		}
		
		//################################################################################################################
		
		int gc = gen0 + gen1 + gen2 + gen3 + gen4 + gen5 + gen6 + gen7;

		if (gc <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_GENRE_SET));
		}
		//################################################################################################################
		
		if ((gen0 == 0 && gen1 != 0) || (gen1 == 0 && gen2 != 0) || (gen2 == 0 && gen3 != 0) || (gen3 == 0 && gen4 != 0) || (gen4 == 0 && gen5 != 0) || (gen5 == 0 && gen6 != 0) || (gen6 == 0 && gen7 != 0)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_HOLE_IN_GENRE));
		}
		
		//################################################################################################################

		int[] g_count = new int[256];
		g_count[gen0]++;
		g_count[gen1]++;
		g_count[gen2]++;
		g_count[gen3]++;
		g_count[gen4]++;
		g_count[gen5]++;
		g_count[gen6]++;
		g_count[gen7]++;
		
		for (int i = 1; i < 256; i++) {
			if (g_count[i] > 1) {
				ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DUPLICATE_GENRE));
				break;
			}
		}
		
		//################################################################################################################
		
		if (newdata.getCover() == null) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER_SET));
		}
		
		//################################################################################################################
		
		if (PathFormatter.isUntrimmed(newdata.getTitle())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSORTITLE_HAS_LEADINGORTRAILING_SPACES));
		}
		
		//################################################################################################################

		if (!newdata.getOnlineReference().isValid()) {
			ret.add(new UserDataProblem(PROBLEM_INVALID_REFERENCE));
		}
	}
	
	public static void testSeasonData(List<UserDataProblem> ret, CCSeason seasonSource, ISeasonData newdata) {
		if (newdata.getTitle().isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
		}
		
		//################################################################################################################
		
		if (newdata.getYear() <= CCDate.YEAR_MIN) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_YEAR));
		}
		
		//################################################################################################################
		
		if (newdata.getCover() == null) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER_SET));
		}
		
		//################################################################################################################
		
		if (PathFormatter.isUntrimmed(newdata.getTitle())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSORTITLE_HAS_LEADINGORTRAILING_SPACES));
		}
	}
	
	public static void testEpisodeData(List<UserDataProblem> ret, IEpisodeOwner owner, CCEpisode episodeSource, IEpisodeData newdata) {
		if (newdata.getTitle().isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
		}
		
		//################################################################################################################

		if (episodeSource != null) {
			CCEpisode eqEp = episodeSource.getSeason().getEpisodeByNumber(newdata.getEpisodeNumber());
			if (eqEp != null && (eqEp != episodeSource)) {
				ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EPISODENUMBER_ALREADY_EXISTS));
			}
		}
		
		//################################################################################################################
		
		if (newdata.getLength() <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_LENGTH));
		}
		
		//################################################################################################################

		if (newdata.getAddDate().isLessEqualsThan(CCDate.getMinimumDate())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DATE_TOO_LESS));
		}
		
		//################################################################################################################

		for (CCDateTime lvdate : newdata.getViewedHistory().iterator().filter(d -> !d.isUnspecifiedDateTime())) {
			if (lvdate.isLessThan(CCDate.getMinimumDate())) {
				ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DATE_TOO_LESS));
			}
		}

		//################################################################################################################

		if (! (PathFormatter.getExtension(newdata.getPart()).equals(newdata.getFormat().asString()) || PathFormatter.getExtension(newdata.getPart()).equals(newdata.getFormat().asStringAlt()))) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EXTENSION_UNEQUALS_FILENAME));
		}
		
		//################################################################################################################
		
		if (newdata.getFilesize().getBytes() <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_FILESIZE));
		}
		
		//################################################################################################################
		
		if (newdata.getPart().isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_PATH));
		}
		
		//################################################################################################################

		if (!newdata.getMediaInfo().isSet()) {
			ret.add(new UserDataProblem(PROBLEM_MEDIAINFO_UNSET));
		} else {
			if (newdata.getMediaInfo().getFilesize() != newdata.getFilesize().getBytes()) ret.add(new UserDataProblem(PROBLEM_MEDIAINFO_WRONG_FILESIZE));
			String err = newdata.getMediaInfo().validate();
			if (err != null) ret.add(new UserDataProblem(PROBLEM_MEDIAINFO_WRONG_DATA, err));
		}

		//################################################################################################################

		if (owner != null) {
			for (CCDatabaseElement idel : owner.getMovieList().iteratorElements()) {
				if (idel.isMovie()) {
					if (isPathIncluded((CCMovie) idel, newdata.getPart())) {
						ret.add(new UserDataProblem(PROBLEM_FILE_ALREADYEXISTS));
						break;
					}
				} else if (idel.isSeries()) {
					CCSeries ss = (CCSeries) idel;
					for (int i = 0; i < ss.getSeasonCount(); i++) {
						CCSeason seas = ss.getSeasonByArrayIndex(i);
						for (int j = 0; j < seas.getEpisodeCount(); j++) {
							if (isPathIncluded(seas.getEpisodeByArrayIndex(j), newdata.getPart())) {
								if (episodeSource == null || episodeSource != seas.getEpisodeByArrayIndex(j)) {
									ret.add(new UserDataProblem(PROBLEM_FILE_ALREADYEXISTS));
								}
								break;
							}
						}
					}
				}
			}
		}
		
		//################################################################################################################
		
		if (PathFormatter.isUntrimmed(newdata.getTitle())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSORTITLE_HAS_LEADINGORTRAILING_SPACES));
		}

		//################################################################################################################
		
		if (PathFormatter.containsIllegalPathSymbolsInSerializedFormat(newdata.getPart())) {
			ret.add(new UserDataProblem(PROBLEM_INVALID_PATH_CHARACTERS));
		}

		//################################################################################################################
		
		if (newdata.getLanguage().isEmpty()) {
			ret.add(new UserDataProblem(PROBLEM_NO_LANG));
		}
		
		if (newdata.getLanguage().contains(CCDBLanguage.MUTED) && !newdata.getLanguage().isSingle()) {
			ret.add(new UserDataProblem(PROBLEM_LANG_MUTED_SUBSET));
		}
	}
	
	private static boolean isPathIncluded(CCMovie m, String p0) {
		for (int i = 0; i < m.getPartcount(); i++) {
			if (StringUtils.equalsIgnoreCase(m.getPart(i), p0)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean isPathIncluded(CCEpisode m, String p0) {
		return StringUtils.equalsIgnoreCase(m.getPart(), p0);
	}

	private static boolean isPathIncluded(CCMovie m, String p0, String p1, String p2, String p3, String p4, String p5) {
		for (int i = 0; i < m.getPartcount(); i++) {
			if (StringUtils.equalsIgnoreCase(m.getPart(i), p0)) {
				return true;
			}
			if (StringUtils.equalsIgnoreCase(m.getPart(i), p1)) {
				return true;
			}
			if (StringUtils.equalsIgnoreCase(m.getPart(i), p2)) {
				return true;
			}
			if (StringUtils.equalsIgnoreCase(m.getPart(i), p3)) {
				return true;
			}
			if (StringUtils.equalsIgnoreCase(m.getPart(i), p4)) {
				return true;
			}
			if (StringUtils.equalsIgnoreCase(m.getPart(i), p5)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean isPathIncluded(CCEpisode m, String p0, String p1, String p2, String p3, String p4, String p5) {
		if (StringUtils.equalsIgnoreCase(m.getPart(), p0)) {
			return true;
		}
		if (StringUtils.equalsIgnoreCase(m.getPart(), p1)) {
			return true;
		}
		if (StringUtils.equalsIgnoreCase(m.getPart(), p2)) {
			return true;
		}
		if (StringUtils.equalsIgnoreCase(m.getPart(), p3)) {
			return true;
		}
		if (StringUtils.equalsIgnoreCase(m.getPart(), p4)) {
			return true;
		}
		if (StringUtils.equalsIgnoreCase(m.getPart(), p5)) {
			return true;
		}

		return false;
	}
}
