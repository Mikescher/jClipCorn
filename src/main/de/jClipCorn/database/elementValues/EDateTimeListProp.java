package de.jClipCorn.database.elementValues;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EDateTimeListProp extends EProperty<CCDateTimeList> {
	public EDateTimeListProp(String name, CCDateTimeList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(String v) throws CCFormatException {
		set(CCDateTimeList.parse(v));
	}

	public void add(CCDateTime v) {
		set(get().add(v));
	}
}
