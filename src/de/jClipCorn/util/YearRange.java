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
}
