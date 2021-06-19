package de.jClipCorn.database.elementValues;

import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.util.exceptions.CCFormatException;

public class EGenreListProp extends EProperty<CCGenreList> {
	public EGenreListProp(String name, CCGenreList defValue, IPropertyParent p, EPropertyType t) {
		super(name, defValue, p, t);
	}

	public void set(long v) throws CCFormatException {
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
	public String serializeValueToString() {
		return get().serialize();
	}

	@Override
	public Object serializeValueToDatabaseValue() {
		return get().getAllGenres();
	}

	@Override
	public void deserializeValueFromString(String v) throws Exception {
		set(CCGenreList.deserialize(v));
	}

	@Override
	public void deserializeValueFromDatabaseValue(Object v) throws Exception {
		set((Long)v);
	}
}
