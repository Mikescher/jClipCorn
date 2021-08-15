package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EGenreListProp extends EProperty<CCGenreList> {
	public EGenreListProp(String name, CCGenreList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(long v) {
		set(new CCGenreList(v));
	}

	public void set(CCGenre genre, int idx) {
		set(get().getSetGenre(idx, genre));
	}

	public boolean tryAddGenre(CCGenre v) {
		CCGenreList l = get().getAddGenre(v);
		if (l == null) return false;
		set(l);
		return true;
	}

	public CCGenre get(int idx) {
		return get().getGenre(idx);
	}

	@Override
	public String serializeToString() {
		return get().serialize();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().getAllGenres();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCGenreList.deserialize(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set((Long)v);
	}

	@Override
	public boolean valueEquals(CCGenreList a, CCGenreList b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}
}
