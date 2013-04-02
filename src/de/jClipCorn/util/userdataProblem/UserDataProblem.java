package de.jClipCorn.util.userdataProblem;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.PathFormatter;

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
	//TODO Zylus ends with an Roman Letter
	
	private final int pid; // Problem ID
	
	public UserDataProblem(int problemID) {
		this.pid = problemID;
	}
	
	public String getText() {
		return LocaleBundle.getString("UserDataErrors.ERROR_" + getPID()); //$NON-NLS-1$
	}
	
	public int getPID() {
		return pid;
	}
	
	//####################################################################################################################################################
	//####################################################################################################################################################
	//####################################################################################################################################################
	
	public static void testMovieData(ArrayList<UserDataProblem> ret, BufferedImage cvr, CCMovieList l, String path0, String path1, String path2, String path3, String path4, String path5, 
									String title, String zyklus, int zyklusID, int len, CCDate adddate, int oscore, int fskidx, int year, long fsize, String csExtn, 
									String csExta, int gen0, int gen1, int gen2, int gen3, int gen4, int gen5, int gen6, int gen7) {
		
		if (path0.isEmpty() && path1.isEmpty() && path2.isEmpty() && path3.isEmpty() && path4.isEmpty() && path5.isEmpty()) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_NO_PATH));
		}
		
		//################################################################################################################
		
		if ((path0.isEmpty() && ! path1.isEmpty()) || (path1.isEmpty() && ! path2.isEmpty()) || (path2.isEmpty() && ! path3.isEmpty()) || (path3.isEmpty() && ! path4.isEmpty()) || (path4.isEmpty() && ! path5.isEmpty())) {
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
			if (! foundM.getPart(0).equals(path0)) {
				ret.add(new UserDataProblem(UserDataProblem.PROBLEM_ZYKLUS_ALREADY_EXISTS));
			}
		}
		
		//################################################################################################################
		
		if (len <= 0) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_INVALID_LENGTH));
		}
		
		//################################################################################################################

		if (adddate.isLessEqualsThan(CCDate.getNewMinimumDate())) {
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
		
		ArrayList<String> extensions = new ArrayList<>();
		
		if (! path0.isEmpty()) {
			extensions.add(PathFormatter.getExtension(path0));
		}
		
		if (! path1.isEmpty()) {
			extensions.add(PathFormatter.getExtension(path1));
		}
		
		if (! path2.isEmpty()) {
			extensions.add(PathFormatter.getExtension(path2));
		}
		
		if (! path3.isEmpty()) {
			extensions.add(PathFormatter.getExtension(path3));
		}
		
		if (! path4.isEmpty()) {
			extensions.add(PathFormatter.getExtension(path4));
		}
		
		if (! path5.isEmpty()) {
			extensions.add(PathFormatter.getExtension(path5));
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
	}
	
	public static void testSeriesData(ArrayList<UserDataProblem> ret, BufferedImage cvr, String title, int oscore, int gen0, int gen1, int gen2, int gen3, int gen4, int gen5, int gen6, int gen7, int fskidx) {
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
	}
	
	public static void testSeasonData(ArrayList<UserDataProblem> ret, BufferedImage cvr, String title, int year) {
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
	}
	
	public static void testEpisodeData(ArrayList<UserDataProblem> ret, CCSeason season, CCEpisode episode, String title, int len, int epNum, CCDate adddate, CCDate lvdate, long fsize, String csExtn, String csExta, String part) {
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

		if (adddate.isLessEqualsThan(CCDate.getNewMinimumDate())) {
			ret.add(new UserDataProblem(UserDataProblem.PROBLEM_DATE_TOO_LESS));
		}
		
		//################################################################################################################

		if (lvdate.isLessThan(CCDate.getNewMinimumDate())) {
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
	}
}
