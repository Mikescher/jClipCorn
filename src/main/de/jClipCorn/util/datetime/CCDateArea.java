package de.jClipCorn.util.datetime;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.DecimalSearchType;

public class CCDateArea {
	public CCDate low;
	public CCDate high;
	
	public DecimalSearchType type;
	
	public CCDateArea(CCDate l, CCDate h, DecimalSearchType t) {
		low = l;
		high = h;
		type = t;
	}

	public CCDateArea() {
		low = CCDate.getCurrentDate();
		high = CCDate.getCurrentDate();
		
		type = DecimalSearchType.EXACT;
	}

	public void setExact(CCDate d) {
		low = d;
		type = DecimalSearchType.EXACT;
	}

	public void setGreater(CCDate d) {
		low = d;
		type = DecimalSearchType.GREATER;
	}

	public void setLesser(CCDate d) {
		high = d;
		type = DecimalSearchType.LESSER;
	}

	public void setInRange(CCDate dStart, CCDate dEnd) {
		low = dStart;
		high = dEnd;
		type = DecimalSearchType.IN_RANGE;
	}

	public boolean contains(CCDate d) {

		switch (type) {
			case LESSER:   
				return d.isLessEqualsThan(high);
				
			case GREATER:  
				return d.isGreaterEqualsThan(low);
				
			case IN_RANGE: 
				return d.isGreaterEqualsThan(low) && d.isLessEqualsThan(high);
				
			case EXACT:    
				return d.isEqual(low);
				
			default:       
				CCLog.addDefaultSwitchError(this, type);
				return false;
		}
	}
	
}
