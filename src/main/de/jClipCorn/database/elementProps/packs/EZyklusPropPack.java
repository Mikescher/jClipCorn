package de.jClipCorn.database.elementProps.packs;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.database.elementProps.impl.EIntProp;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.database.elementProps.impl.EStringProp;

public class EZyklusPropPack {
	public final EStringProp Title;
	public final EIntProp    Number;

	private CCMovieZyklus _cache = null;
	private IEProperty[] _properties = null;
	private boolean _ignoreCacheUpdates = false;

	public EZyklusPropPack(String namePrefix, CCMovieZyklus startValue, IPropertyParent parent, EPropertyType proptype)
	{
		Title  = new EStringProp(namePrefix + ".Title",  startValue.getTitle(),  parent, proptype);
		Number = new EIntProp(   namePrefix + ".Number", startValue.getNumber(), parent, proptype);

		Title.addChangeListener((_1, _2, _3) -> updateCache());
		Number.addChangeListener((_1, _2, _3) -> updateCache());

		updateCache();
	}

	private void updateCache() {
		if (_ignoreCacheUpdates) return;

		_cache = new CCMovieZyklus(Title.get(), Number.get());
	}

	public CCMovieZyklus get() {
		return _cache;
	}

	public IEProperty[] getProperties()
	{
		if (_properties == null) _properties = listProperties();
		return _properties;
	}

	private IEProperty[] listProperties() {
		return new IEProperty[]
		{
			Title,
			Number,
		};
	}

	public void set(String vTitle, int vNumber) {
		try
		{
			_ignoreCacheUpdates = true;

			Title .set(vTitle);
			Number.set(vNumber);
		}
		finally
		{
			_ignoreCacheUpdates = false;
			updateCache();
		}
	}

	public void set(CCMovieZyklus v) {
		try
		{
			_ignoreCacheUpdates = true;

			Title.set(v.getTitle());
			Number.set(v.getNumber());
		}
		finally
		{
			_ignoreCacheUpdates = false;
			updateCache();
		}
	}

	public void setTitle(String v) {
		Title.set(v);
	}

	public void setNumber(int v) {
		Number.set(v);
	}
}
