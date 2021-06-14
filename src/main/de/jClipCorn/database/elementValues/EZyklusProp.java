package de.jClipCorn.database.elementValues;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

public class EZyklusProp extends EProperty<CCMovieZyklus> {
	public EZyklusProp(String name, CCMovieZyklus defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(String title, int num) {
		set(new CCMovieZyklus(title, num));
	}

	public void setTitle(String title) {
		set(get().getWithTitle(title));
	}

	public void setNumber(int num) {
		set(get().getWithNumber(num));
	}
}
