package de.jClipCorn.online.cover.imdb;

import java.util.HashMap;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.helper.RegExHelper;

@SuppressWarnings("nls")
public class AgeRatingParser {
	private final static String REGEX_FSK_VALUE = "[0-9]+"; // [0-9]+
	
	private static HashMap<String, Integer> ratingsMap;
	
	//URL: http://en.wikipedia.org/wiki/Motion_picture_rating_system
	//URL: http://en.wikipedia.org/wiki/Television_content_rating_systems
	static {
		ratingsMap = new HashMap<>();
		
		// put all in lowercase !!!
		
		ratingsMap.put("unrated", -1); 
		ratingsMap.put("not rated", -1);
		ratingsMap.put("(banned)", -1); 
		ratingsMap.put("banned", -1); 
		ratingsMap.put("approved", -1); 
		
		ratingsMap.put("t", 0); 
		ratingsMap.put("a", 0);
		ratingsMap.put("l", 0); 
		ratingsMap.put("g", 0);
		ratingsMap.put("u", 0);
		ratingsMap.put("i", 0);
		ratingsMap.put("s", 0);
		ratingsMap.put("k", 0); 
		ratingsMap.put("te", 0); 
		ratingsMap.put("su", 0);
		ratingsMap.put("aa", 0);
		ratingsMap.put("al", 0);
		ratingsMap.put("0+", 0);
		ratingsMap.put("pt", 0);
		ratingsMap.put("atp", 0);
		ratingsMap.put("apt", 0);
		ratingsMap.put("btl", 0);
		ratingsMap.put("p/g", 0);
		ratingsMap.put("all", 0);
		ratingsMap.put("tv-y", 0);
		ratingsMap.put("tv-g", 0);
		ratingsMap.put("pere", 0);
		ratingsMap.put("alle", 0);
		ratingsMap.put("o.al.", 0);
		ratingsMap.put("fsk 0", 0);
		ratingsMap.put("livre", 0);
		ratingsMap.put("unrestricted", 0);
		ratingsMap.put("general audience", 0);
		ratingsMap.put("ohne altersbeschränkung", 0);
		
		ratingsMap.put("m/4", 4);
		ratingsMap.put("bo-a", 4);
		
		ratingsMap.put("6+", 6);
		ratingsMap.put("m/6", 6);
		ratingsMap.put("mg6", 6);
		ratingsMap.put("ms-6", 6);
		ratingsMap.put("fsk 6", 6);
		
		ratingsMap.put("7+", 7);
		ratingsMap.put("i7", 7);
		ratingsMap.put("y7", 7);
		ratingsMap.put("k-7", 7);
		ratingsMap.put("tv-y7", 7);
		
		ratingsMap.put("bo", 8);
		
		ratingsMap.put("pg", 10);
		ratingsMap.put("dp", 10);
		ratingsMap.put("-10", 10);
		ratingsMap.put("i10", 10);
		ratingsMap.put("y10", 10);
		ratingsMap.put("10v", 10);
		ratingsMap.put("10m", 10);
		ratingsMap.put("pg/dp", 10);
		ratingsMap.put("tv-pg", 10);
		
		ratingsMap.put("k-11", 11);
		
		ratingsMap.put("b", 12);
		ratingsMap.put("u/a", 12);
		ratingsMap.put("-12", 12);
		ratingsMap.put("i12", 12);
		ratingsMap.put("y12", 12);
		ratingsMap.put("12+", 12);
		ratingsMap.put("12a", 12);
		ratingsMap.put("m/12", 12);
		ratingsMap.put("k-12", 12);
		ratingsMap.put("ms-12", 12);
		ratingsMap.put("pg-12", 12);
		ratingsMap.put("fsk 12", 12);
		
		ratingsMap.put("m", 13);
		ratingsMap.put("ua", 13);
		ratingsMap.put("13+", 13);
		ratingsMap.put("iia", 13);
		ratingsMap.put("p13", 13);
		ratingsMap.put("r13", 13);
		ratingsMap.put("13v", 13);
		ratingsMap.put("pg13", 13);
		ratingsMap.put("bo-r", 13);
		ratingsMap.put("k-13", 13);
		ratingsMap.put("r-13", 13);
		ratingsMap.put("pg-13", 13);
		
		ratingsMap.put("14+", 14);
		ratingsMap.put("k-14", 14);
		ratingsMap.put("14a", 14);
		ratingsMap.put("vm14", 14);
		ratingsMap.put("tv-14", 14);
		
		ratingsMap.put("ma", 15);
		ratingsMap.put("15+", 15);
		ratingsMap.put("15a", 15);
		ratingsMap.put("b15", 15);
		ratingsMap.put("r15+", 15);
		ratingsMap.put("r-15", 15);
		ratingsMap.put("k-15", 15);
		ratingsMap.put("15pg", 15);
		ratingsMap.put("ma15+", 15);
		
		ratingsMap.put("e16", 16);
		ratingsMap.put("16+", 16);
		ratingsMap.put("knt", 16);
		ratingsMap.put("ena", 16);
		ratingsMap.put("-16", 16);
		ratingsMap.put("iib", 16);
		ratingsMap.put("r16", 16);
		ratingsMap.put("m/16", 16);
		ratingsMap.put("k-16", 16);
		ratingsMap.put("nc16", 16);
		ratingsMap.put("fsk 16", 16);
		
		ratingsMap.put("r", 17);
		ratingsMap.put("17+", 17);
		ratingsMap.put("k-17", 17);
		ratingsMap.put("nc-17", 17);
		ratingsMap.put("tv-ma", 17);
		
		ratingsMap.put("x", 18);
		ratingsMap.put("e18", 18);
		ratingsMap.put("r18", 18);
		ratingsMap.put("x18", 18);
		ratingsMap.put("m18", 18);
		ratingsMap.put("18a", 18);
		ratingsMap.put("18+", 18);
		ratingsMap.put("-18", 18);
		ratingsMap.put("iii", 18);
		ratingsMap.put("18/S", 18);
		ratingsMap.put("18/V", 18);
		ratingsMap.put("m/18", 18);
		ratingsMap.put("18+r", 18);
		ratingsMap.put("k-18", 18);
		ratingsMap.put("r-18", 18);
		ratingsMap.put("r18+", 18);
		ratingsMap.put("vm18", 18);
		ratingsMap.put("nc-17", 18);
		ratingsMap.put("tv-ma", 18);
		ratingsMap.put("fsk 18", 18);
		
		ratingsMap.put("d", 19);
		ratingsMap.put("xxx", 19);
		ratingsMap.put("19+", 19);
		
		ratingsMap.put("20+", 20);
		
		ratingsMap.put("ha", 21);
		ratingsMap.put("r21", 21);
		ratingsMap.put("r(a)", 21);
		ratingsMap.put("adult", 21);
	}
	
	public static int getMinimumAge(String rate, String url) {
		rate = rate.trim().toLowerCase();
		
		//################## STEP 1 ##################
		
		Integer age = ratingsMap.get(rate);
		if (age != null) {
			return age;
		}
		
		//################## STEP 2 ##################
		
		try {
			int dcAge = Integer.parseInt(rate);
			return dcAge;
		} catch (NumberFormatException e) {
			//no problemos
		}
		
		//################## STEP 3 ##################

		try {
			int dcAge = Integer.parseInt(RegExHelper.find(REGEX_FSK_VALUE, rate));
			return dcAge;
		} catch (NumberFormatException e) {
			//no problemos
		}
		
		//################## STEP 4 ##################
		
		CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.CouldNotParseFSK", rate, url));
		
		return -1;
	}

	public static CCFSK getFSK(String rate, String url) {
		int v = getMinimumAge(rate, url);
		
		if (v < 0) return null;
		
		return CCFSK.getNearest(v);
	}
}
