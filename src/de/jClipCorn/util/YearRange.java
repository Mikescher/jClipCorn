package de.jClipCorn.util;

public class YearRange {
	private int year1;
	private int year2;
	
	public YearRange(int s1, int s2) {
		this.year1 = s1;
		this.year2 = s2;
	}
	
	public YearRange(int s1) {
		this.year1 = s1;
		this.year2 = -1;
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
		if (isTimePoint()) {
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
