package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EDateTimeListProp extends EProperty<CCDateTimeList> {
	public EDateTimeListProp(String name, CCDateTimeList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	/** legacy comma separated format, see {@link CCDateTimeList#parse} */
	public void set(String v) throws CCFormatException {
		set(CCDateTimeList.parse(v));
	}

	/** legacy comma separated format, see {@link CCDateTimeList#parse} */
	public void setOnly(String v) throws CCFormatException {
		setOnly(CCDateTimeList.parse(v));
	}

	public void add(CCDateTime v) {
		set(get().add(v));
	}

	public void add(CCDateTime v, String comment) {
		set(get().add(v, comment));
	}

	@Override
	public String serializeToString() {
		return get().asJSONArray();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().asJSONArray();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCDateTimeList.fromJSONArray(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set(CCDateTimeList.fromJSONArray((String)v));
	}

	@Override
	public boolean valueEquals(CCDateTimeList a, CCDateTimeList b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.isEqual(b);
	}
}
