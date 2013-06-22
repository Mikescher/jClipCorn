package de.jClipCorn.util.userdataProblem;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.PathFormatter;
import de.jClipCorn.util.RomanNumberFormatter;

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
	
	private final int pid; // Problem ID
	
	public UserDataProblem(int problemID) {
		this.pid = problemID;
	}
	
	public String getText() {
		
		return LocaleBundle.getString(String.format("UserDataErrors.ERROR_%02d", getPID())); //$NON-NLS-1$
	}
	
	public int getPID() {
		return pid;
	}
	
	//####################################################################################################################################################
	//####################################################################################################################################################
	//####################################################################################################################################################
	
	public static void testMovieData(List<UserDataProblem> ret, CCMovie mov, BufferedImage cvr, CCMovieList l, String p0, String p1, String p2, String p3, String p4, String p5, 
									String title, String zyklus, int zyklusID, int len, CCDate adddate, int oscore, int fskidx, int year, long fsize, String csExtn, 
									String csExta, int gen0, int gen1, int gen2, int gen3, int gen4, int gen5, int gen6, int gen7, int quality, int language) {
		
		int partcount = 0;
		
		if (! p0.isEmpty()) partcount++;
		if (! p1.isEmpty()) partcount++;
		if (! p2.isEmpty()) partcount++;
		if (! p3.isEmpty()) partcount++;
		if (! p4.isEmpty()) partcount++;
		if (! p5.isEmpty()) partcount++;
		
		//################################################################################################################
		
		if (p0.isEmpty() && p1.isEmpty() && p2.isEmpty() && p3.isEmpty() && p4.isEmpty() && p5.isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_PATH));
		}
		
		//################################################################################################################
		
		if ((p0.isEmpty() && ! p1.isEmpty()) || (p1.isEmpty() && ! p2.isEmpty()) || (p2.isEmpty() && ! p3.isEmpty()) || (p3.isEmpty() && ! p4.isEmpty()) || (p4.isEmpty() && ! p5.isEmpty())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_HOLE_IN_PATH));
		}
		
		//################################################################################################################
		
		if (title.isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
		}
		
		//################################################################################################################
		
		if (zyklus.isEmpty() && zyklusID != -1) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSID_IS_SET));
		}
		
		//################################################################################################################
		
		if (! zyklus.isEmpty() && zyklusID == -1) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSID_IS_NEGONE));
		}
		
		//################################################################################################################
		
		CCMovie foundM;
		if (! zyklus.isEmpty() && (foundM = l.findfirst(new CCMovieZyklus(zyklus, zyklusID))) != null) {
			if (mov == null || mov.getLocalID() != foundM.getLocalID()) {
				ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUS_ALREADY_EXISTS));
			}
		}
		
		//################################################################################################################
		
		if (len <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_LENGTH));
		}
		
		//################################################################################################################

		if (adddate.isLessEqualsThan(CCDate.getMinimumDate())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DATE_TOO_LESS));
		}
		
		//################################################################################################################
		
		if (oscore <= 0 || oscore >= 10) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_ONLINESCORE));
		}
		
		//################################################################################################################
		
		if (fskidx == (CCMovieFSK.values().length)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_FSK_NOT_SET));
		}
		
		//################################################################################################################
		
		List<String> extensions = new ArrayList<>();
		
		if (! p0.isEmpty()) {
			extensions.add(PathFormatter.getExtension(p0));
		}
		
		if (! p1.isEmpty()) {
			extensions.add(PathFormatter.getExtension(p1));
		}
		
		if (! p2.isEmpty()) {
			extensions.add(PathFormatter.getExtension(p2));
		}
		
		if (! p3.isEmpty()) {
			extensions.add(PathFormatter.getExtension(p3));
		}
		
		if (! p4.isEmpty()) {
			extensions.add(PathFormatter.getExtension(p4));
		}
		
		if (! p5.isEmpty()) {
			extensions.add(PathFormatter.getExtension(p5));
		}
		
		if (! (extensions.contains(csExtn) || extensions.contains(csExta))) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EXTENSION_UNEQUALS_FILENAME));
		}
		
		//################################################################################################################
		
		if (year <= CCDate.YEAR_MIN) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_YEAR));
		}
		
		//################################################################################################################
		
		if (fsize <= 0) {
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
		
		if (cvr == null) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER_SET));
		}
		
		//################################################################################################################
		
		if (PathFormatter.isUntrimmed(title) || PathFormatter.isUntrimmed(zyklus)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSORTITLE_HAS_LEADINGORTRAILING_SPACES));
		}
		
		//################################################################################################################
		
		if (RomanNumberFormatter.endsWithRoman(zyklus)) {
			ret.add(new UserDataProblem(PROBLEM_ZYKLUS_ENDS_WITH_ROMAN));
		}
		
		//################################################################################################################
		
		if (CCMovieQuality.calculateQuality(fsize, len, partcount).asInt() != quality) {
			ret.add(new UserDataProblem(PROBLEM_WRONG_QUALITY));
		}
		
		//################################################################################################################
		
		for (Iterator<CCMovie> it = l.iteratorMovies(); it.hasNext();) {
			CCMovie imov = it.next();
			
			if (StringUtils.equalsIgnoreCase(imov.getTitle(), title) && StringUtils.equalsIgnoreCase(imov.getZyklus().getTitle(), zyklus)  && imov.getLanguage().asInt() == language) {
				if (mov == null || mov.getLocalID() != imov.getLocalID()) {
					ret.add(new UserDataProblem(PROBLEM_TITLE_ALREADYEXISTS));
				}
				break;
			}
		}
		
		//################################################################################################################
		
		for (Iterator<CCDatabaseElement> it = l.iterator(); it.hasNext();) {
			CCDatabaseElement idel = it.next();
			
			if (idel.isMovie()) {
				if (isPathIncluded((CCMovie)idel, p0, p1, p2, p3, p4, p5)) {
					if (mov == null || mov.getLocalID() != idel.getLocalID()) {
						ret.add(new UserDataProblem(PROBLEM_FILE_ALREADYEXISTS));
					}
					break;
				}
			} else if (idel.isSeries()) {
				CCSeries ss = (CCSeries) idel;
				for (int i = 0; i < ss.getSeasonCount(); i++) {
					CCSeason seas = ss.getSeason(i);
					for (int j = 0; j < seas.getEpisodeCount(); j++) {
						if (isPathIncluded(seas.getEpisode(j), p0, p1, p2, p3, p4, p5)) {
							ret.add(new UserDataProblem(PROBLEM_FILE_ALREADYEXISTS));
							break;
						}
					}
				}
			}
		}
	}
	
	public static void testSeriesData(List<UserDataProblem> ret, BufferedImage cvr, String title, int oscore, int gen0, int gen1, int gen2, int gen3, int gen4, int gen5, int gen6, int gen7, int fskidx) {
		if (title.isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
		}
		
		//################################################################################################################
		
		if (oscore <= 0 || oscore >= 10) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_ONLINESCORE));
		}
		
		//################################################################################################################
		
		if (fskidx == (CCMovieFSK.values().length)) {
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
		
		if (cvr == null) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER_SET));
		}
		
		//################################################################################################################
		
		if (PathFormatter.isUntrimmed(title)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSORTITLE_HAS_LEADINGORTRAILING_SPACES));
		}
	}
	
	public static void testSeasonData(List<UserDataProblem> ret, BufferedImage cvr, String title, int year) {
		if (title.isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
		}
		
		//################################################################################################################
		
		if (year <= CCDate.YEAR_MIN) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_YEAR));
		}
		
		//################################################################################################################
		
		if (cvr == null) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_COVER_SET));
		}
		
		//################################################################################################################
		
		if (PathFormatter.isUntrimmed(title)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSORTITLE_HAS_LEADINGORTRAILING_SPACES));
		}
	}
	
	public static void testEpisodeData(List<UserDataProblem> ret, CCSeason season, CCEpisode episode, String title, int len, int epNum, CCDate adddate, CCDate lvdate, long fsize, String csExtn, String csExta, String part, int quality) {
		if (title.isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EMPTY_TITLE));
		}
		
		//################################################################################################################
		
		CCEpisode eqEp = season.getEpisodebyNumber(epNum);
		if (eqEp != null && eqEp != episode) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EPISODENUMBER_ALREADY_EXISTS));
		}
		
		//################################################################################################################
		
		if (len <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_LENGTH));
		}
		
		//################################################################################################################

		if (adddate.isLessEqualsThan(CCDate.getMinimumDate())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DATE_TOO_LESS));
		}
		
		//################################################################################################################

		if (lvdate.isLessThan(CCDate.getMinimumDate())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DATE_TOO_LESS));
		}

		//################################################################################################################

		if (! (PathFormatter.getExtension(part).equals(csExtn) || PathFormatter.getExtension(part).equals(csExta))) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_EXTENSION_UNEQUALS_FILENAME));
		}
		
		//################################################################################################################
		
		if (fsize <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_FILESIZE));
		}
		
		//################################################################################################################
		
		if (part.isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_PATH));
		}
		
		//################################################################################################################
		
		if (CCMovieQuality.calculateQuality(fsize, len, 1).asInt() != quality) {
			ret.add(new UserDataProblem(PROBLEM_WRONG_QUALITY));
		}
		
		//################################################################################################################
		
		for (Iterator<CCDatabaseElement> it = season.getMovieList().iterator(); it.hasNext();) {
			CCDatabaseElement idel = it.next();
			
			if (idel.isMovie()) {
				if (isPathIncluded((CCMovie)idel, part)) {
					ret.add(new UserDataProblem(PROBLEM_FILE_ALREADYEXISTS));
					break;
				}
			} else if (idel.isSeries()) {
				CCSeries ss = (CCSeries) idel;
				for (int i = 0; i < ss.getSeasonCount(); i++) {
					CCSeason seas = ss.getSeason(i);
					for (int j = 0; j < seas.getEpisodeCount(); j++) {
						if (isPathIncluded(seas.getEpisode(j), part)) {
							if (episode == null || episode != seas.getEpisode(j)) {
								ret.add(new UserDataProblem(PROBLEM_FILE_ALREADYEXISTS));
							}
							break;
						}
					}
				}
			}
		}
		
		//################################################################################################################
		
		if (PathFormatter.isUntrimmed(title)) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUSORTITLE_HAS_LEADINGORTRAILING_SPACES));
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
