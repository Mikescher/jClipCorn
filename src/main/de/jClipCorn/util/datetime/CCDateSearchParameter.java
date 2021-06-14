package de.jClipCorn.util.datetime;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public class CCDateSearchParameter {

	public enum DateSearchType implements ContinoousEnum<DateSearchType> { 
		CONTAINS(0),               // contains [A] at least once
		CONTAINS_NOT(1),           // does not contain any [A]
		CONTAINS_BETWEEN(2),       // contains at least one value between [A] and [B]
		CONTAINS_NOT_BETWEEEN(3),  // contains no value between [A] and [B]
		CONTAINS_ONLY_BETWEEN(4);  // all values are between [A] and [B]

		private final static String[] NAMES = { "CONTAINS", "CONTAINS_NOT", "CONTAINS_BETWEEN", "CONTAINS_NOT_BETWEEEN", "CONTAINS_ONLY_BETWEEN" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

		private final int id;

		private static final EnumWrapper<DateSearchType> wrapper = new EnumWrapper<>(CONTAINS);

		private DateSearchType(int val) { id = val; }
		
		public static EnumWrapper<DateSearchType> getWrapper() {
			return wrapper;
		}

		@Override
		public IEnumWrapper wrapper() {
			return getWrapper();
		}
		
		@Override
		public int asInt() { return id; }

		@Override
		public String asString() { return NAMES[asInt()]; }

		@Override
		public String[] getList() { return NAMES; }

		@Override
		public DateSearchType[] evalues() { return DateSearchType.values(); }
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
