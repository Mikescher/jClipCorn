package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCPathList;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.filesystem.CCPath;

public class ECCPathListProp extends EProperty<CCPathList> implements IEProperty {
	
	public ECCPathListProp(String name, CCPathList val, IPropertyParent p, EPropertyType t) {
		super(name, val, p, t);
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
		set(CCPathList.createFromJSON(v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set(CCPathList.createFromJSON((String) v));
	}

	@Override
	public boolean valueEquals(CCPathList a, CCPathList b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}
	
	// Convenience methods for backwards compatibility with old Parts API
	public CCPath get(int index) {
		return get().get(index);
	}
	
	public void set(int index, CCPath path) {
		set(get().set(index, path));
	}
	
	public void set(int index, String path) {
		set(index, CCPath.create(path));
	}
	
	public void reset(int index) {
		set(index, CCPath.Empty);
	}

	public void set(CCPath p0, CCPath p1, CCPath p2, CCPath p3, CCPath p4, CCPath p5) {
		set(get().set(p0, p1, p2, p3, p4, p5));
	}
}