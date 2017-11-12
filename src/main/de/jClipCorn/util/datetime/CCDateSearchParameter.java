package de.jClipCorn.util.datetime;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.gui.log.CCLog;

public class CCDateSearchParameter {

	public enum DateSearchType { 
		CONTAINS,               // contains [A] at least once
		CONTAINS_NOT,           // does not contain any [A]
		CONTAINS_BETWEEN,       // contains at least one value between [A] and [B]
		CONTAINS_NOT_BETWEEEN,  // contains no value between [A] and [B]
		CONTAINS_ONLY_BETWEEN,  // all values are between [A] and [B]
	}
	
	public CCDate First;
	public CCDate Second;
	
	public DateSearchType Type;

	public CCDateSearchParameter() {
		First = CCDate.getCurrentDate();
		Second = CCDate.getCurrentDate();
		Type = DateSearchType.CONTAINS;
	}
	
	public CCDateSearchParameter(CCDate v1, CCDate v2, DateSearchType t) {
		First = v1;
		Second = v2;
		Type = t;
	}

	public boolean includes(CCDateTimeList l) {
		switch (Type) {
			case CONTAINS:
				return l.iterator().any(d -> d.date.isEqual(First));
				
			case CONTAINS_NOT:
				return ! l.iterator().any(d -> d.date.isEqual(First));
				
			case CONTAINS_BETWEEN:
				return l.iterator().any(d -> d.isGreaterEqualsThan(First) && d.isLessEqualsThan(Second));
				
			case CONTAINS_NOT_BETWEEEN:
				return l.iterator().all(d -> !(d.isGreaterEqualsThan(First) && d.isLessEqualsThan(Second)));
				
			case CONTAINS_ONLY_BETWEEN:
				return l.iterator().all(d -> d.isGreaterEqualsThan(First) && d.isLessEqualsThan(Second));
				
			default:
				CCLog.addDefaultSwitchError(this, Type);
				return false;
		}
		
	}
}
