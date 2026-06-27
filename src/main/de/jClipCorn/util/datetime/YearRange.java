package de.jClipCorn.util.datetime;

public class YearRange {
	private int year1;
	private int year2;
	private final boolean isSet;

	private static final YearRange UNSET = new YearRange();

	private YearRange() {
		this.year1 = 0;
		this.year2 = -1;
		this.isSet = false;
	}

	public YearRange(int s1, int s2) {
		this.year1 = s1;
		this.year2 = s2;
		this.isSet = true;
	}

	public YearRange(int s1) {
		this.year1 = s1;
		this.year2 = -1;
		this.isSet = true;
	}

	/**
	 * @return a YearRange that represents "no year at all" (e.g. a movie/season without a year, or an empty series)
	 */
	public static YearRange unset() {
		return UNSET;
	}

	public boolean isSet() {
		return isSet;
	}

	public int getYear1() {
		return year1;
	}

	public int getYear2() {
		return year2;
	}

	public void setYear1(int s1) {
		this.year1 = s1;
	}

	public void setYear2(int s2) {
		this.year2 = s2;
	}

	public boolean includes(int year) {
		if (!isSet) return false;
		if (isTimePoint()) {
			return year == year1;
		} else {
			return (year >= year1) && (year <= year2);
		}
	}

	public boolean isTimePoint() {
		return year2 < 0;
	}

	public String asString() {
		if (!isSet) {
			return ""; //$NON-NLS-1$
		} else if (isTimePoint()) {
			return getYear1() + ""; //$NON-NLS-1$
		} else {
			return getYear1() + " - " + getYear2(); //$NON-NLS-1$
		}
	}

	public int getMiddle() {
		if (isTimePoint()) {
			return getYear1();
		} else {
			return (getYear1() + getYear2()) / 2;
		}
	}

	public int getLowestYear() {
		return getYear1();
	}

	public int getHighestYear() {
		if (isTimePoint()) {
			return getYear1();
		} else {
			return getYear2();
		}
	}

	public static int compare(YearRange a, YearRange b) {
		if (a.isSet() != b.isSet()) return Boolean.compare(a.isSet(), b.isSet());
		return Integer.compare(a.getMiddle(), b.getMiddle());
	}

	public boolean isCompletelySmallerThan(YearRange r1) {
		return getHighestYear() < r1.getLowestYear();
	}

	public boolean isCompletelyGreaterThan(YearRange r1) {
		return getLowestYear() > r1.getHighestYear();
	}

	public boolean isCompletelyBetween(YearRange r1, YearRange r2) {
		if (compare(r1, r2) > 1) { // r1 < r2
			return isCompletelyGreaterThan(r2) && isCompletelySmallerThan(r1);
		} else {// r1 >= r2
			return isCompletelyGreaterThan(r1) && isCompletelySmallerThan(r2);
		}
	}
}
