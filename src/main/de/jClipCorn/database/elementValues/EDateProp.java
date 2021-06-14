package de.jClipCorn.database.elementValues;

import de.jClipCorn.util.datetime.CCDate;

import java.sql.Date;

public class EDateProp extends EProperty<CCDate> {
	public EDateProp(String name, CCDate defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(Date v) {
		set(CCDate.create(v));
	}
}
