package de.jClipCorn.database.history;

import de.jClipCorn.util.Str;

public class CCHistorySingleChange {
	public String Field;
	public String OldValue;
	public String NewValue;

	public CCHistorySingleChange(String field, String oldValue, String newValue) {
		Field = field;
		OldValue = oldValue;
		NewValue = newValue;
	}

	public boolean isTrivialViewedChange() {
		if (Str.equals(Field, "VIEWED")) return true; //$NON-NLS-1$
		if (Str.equals(Field, "VIEWED_HISTORY") && OldValue!=null && NewValue!=null && NewValue.length()>OldValue.length() && Str.equals(OldValue, NewValue.substring(0, OldValue.length()))) return true; //$NON-NLS-1$
		return false;
	}
}
