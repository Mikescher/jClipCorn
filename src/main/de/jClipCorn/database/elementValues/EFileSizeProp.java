package de.jClipCorn.database.elementValues;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;

public class EFileSizeProp extends EProperty<CCFileSize> {
	public EFileSizeProp(String name, CCFileSize defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(long v) {
		set(new CCFileSize(v));
	}
}
