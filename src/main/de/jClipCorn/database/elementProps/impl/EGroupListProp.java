package de.jClipCorn.database.elementProps.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.lambda.Func1to1;

public class EGroupListProp extends EProperty<CCGroupList> {

	private final Func1to1<CCGroupList, CCGroupList> _insertCallbackFunc;

	private boolean _preventCallbackFunc = false;

	public EGroupListProp(String name, CCGroupList defValue, IPropertyParent p, EPropertyType t, Func1to1<CCGroupList, CCGroupList> icf) {
		super(name, defValue, p, t);
		_insertCallbackFunc = icf;
	}

	public boolean isEmpty() {
		return get().isEmpty();
	}

	public void set(String v) throws CCFormatException {
		set(CCGroupList.parseWithoutAddingNewGroups(parent.getMovieList(), v));
	}
	public void setFromMovieListWithoutDBUpdateOrCallback(CCGroupList v) {
		try
		{
			_preventCallbackFunc = true;
			set(v, true, true, false, true);
		}
		finally
		{
			_preventCallbackFunc = false;
		}
	}

	public void setFromMovieListWithoutCallback(CCGroupList v) {
		try
		{
			_preventCallbackFunc = true;
			set(v);
		}
		finally
		{
			_preventCallbackFunc = false;
		}
	}

	@Override
	protected CCGroupList onValueChanging(CCGroupList valOld, CCGroupList valNew)
	{
		if (_preventCallbackFunc) return valNew;
		if (!valOld.equals(valNew)) return _insertCallbackFunc.invoke(valNew); else return valNew;
	}

	@Override
	public String serializeToString() {
		return get().toSerializationString();
	}

	@Override
	public Object serializeToDatabaseValue() {
		return get().toSerializationString();
	}

	@Override
	public void deserializeFromString(String v) throws CCFormatException {
		set(CCGroupList.parseWithoutAddingNewGroups(parent.getMovieList(), v));
	}

	@Override
	public void deserializeFromDatabaseValue(Object v) throws CCFormatException {
		set(CCGroupList.parseWithoutAddingNewGroups(parent.getMovieList(), (String)v));
	}
}
