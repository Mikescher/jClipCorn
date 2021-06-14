package de.jClipCorn.database.elementValues;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;

public class ELanguageListProp extends EProperty<CCDBLanguageList> {
	public ELanguageListProp(String name, CCDBLanguageList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(long v) {
		set(CCDBLanguageList.fromBitmask(v));
	}
}
