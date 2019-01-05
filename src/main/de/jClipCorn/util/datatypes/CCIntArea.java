package de.jClipCorn.util.datatypes;

import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.datetime.YearRange;
import de.jClipCorn.util.stream.CCStream;

public class CCIntArea {
	public int low;
	public int high;
	
	public DecimalSearchType type;
	
	public CCIntArea(int l, int h, DecimalSearchType t) {
		low = l;
		high = h;
		type = t;
	}

	public CCIntArea() {
		low = 0;
		high = 0;
		
		type = DecimalSearchType.EXACT;
	}

	public void setExact(int d) {
		low = d;
		type = DecimalSearchType.EXACT;
	}

	public void setGreater(int d) {
		low = d;
		type = DecimalSearchType.GREATER;
	}

	public void setLesser(int d) {
		high = d;
		type = DecimalSearchType.LESSER;
	}

	public void setInRange(int dStart, int dEnd) {
		low = dStart;
		high = dEnd;
		type = DecimalSearchType.IN_RANGE;
	}

	public boolean contains(int c) {

		switch (type) {
			case LESSER:
				return c < high;
				
			case GREATER:
				return c > low;
				
			case IN_RANGE:
				return c > low && c < high;
				
			case EXACT:
				return c == low;
				
			default:
				CCLog.addDefaultSwitchError(this, type);
				return false;
		}
	}

	public boolean contains(double c) {

		switch (type) {
			case LESSER:
				return c < high;
				
			case GREATER:
				return c > low;
				
			case IN_RANGE:
				return c > low && c < high;
				
			case EXACT:
				return Math.round(c) == low;
				
			default:
				CCLog.addDefaultSwitchError(this, type);
				return false;
		}
	}

	public boolean contains(YearRange range) {

		switch (type) {
			case LESSER:
				return range.isCompletelySmallerThan(new YearRange(high));
				
			case GREATER:
				return range.isCompletelyGreaterThan(new YearRange(low));
				
			case IN_RANGE:
				return range.isCompletelyBetween(new YearRange(low), new YearRange(high));
				
			case EXACT:
				return range.includes(low);
				
			default:
				CCLog.addDefaultSwitchError(this, type);
				return false;
		}
	}

	public boolean contains(CCStream<Integer> c) {
		return c.any(this::contains);
	}
}
