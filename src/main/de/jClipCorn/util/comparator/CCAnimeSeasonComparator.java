package de.jClipCorn.util.comparator;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;

import java.util.Comparator;
import java.util.regex.Matcher;

public class CCAnimeSeasonComparator implements Comparator<String> {
	
	@Override
	public int compare(String o1, String o2) {

		var m1 = CCDatabaseElement.REGEX_ANIMESEASON.matcher(o1);
		var m2 = CCDatabaseElement.REGEX_ANIMESEASON.matcher(o2);

		if (m1.matches() && m2.matches()) {

			return Integer.compare(convert(m1), convert(m2));

		} else {
			if (m1.matches()) return -1;
			if (m2.matches()) return 1;

			return o1.compareTo(o2);
		}
	}

	@SuppressWarnings("nls")
	private int convert(Matcher m) {

		var season = -1;
		switch (m.group(1)) {
			case "Winter":
				season = 0;
				break;
			case "Spring":
				season = 1;
				break;
			case "Summer":
				season = 2;
				break;
			case "Fall":
				season = 3;
			default:
				break;
		}

		var year = Integer.parseInt(m.group(2));

		return year * 10000 + season;
	}

}