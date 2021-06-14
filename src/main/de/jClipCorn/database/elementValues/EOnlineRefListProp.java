package de.jClipCorn.database.elementValues;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReferenceList;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EOnlineRefListProp extends EProperty<CCOnlineReferenceList> {
	public EOnlineRefListProp(String name, CCOnlineReferenceList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(String v) throws CCFormatException {
		set(CCOnlineReferenceList.parse(v));
	}
}
