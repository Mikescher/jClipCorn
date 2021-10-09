package de.jClipCorn.database.elementProps.packs;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.database.elementProps.impl.EShortProp;

public class EOnlineScorePropPack extends EPropertyPack {
	public final EShortProp Numerator;
	public final EShortProp Denominator;

	private CCOnlineScore _cache = null;
	private IEProperty[] _properties = null;
	private boolean _ignoreCacheUpdates = false;

	public EOnlineScorePropPack(String namePrefix, CCOnlineScore startValue, IPropertyParent parent, EPropertyType proptype)
	{
		Numerator   = new EShortProp(namePrefix + ".Numerator",   startValue.Numerator,   parent, proptype);
		Denominator = new EShortProp(namePrefix + ".Denominator", startValue.Denominator, parent, proptype);

		Numerator.addChangeListener((_1, _2, _3) -> updateCache());
		Denominator.addChangeListener((_1, _2, _3) -> updateCache());

		updateCache();
	}

	private void updateCache() {
		if (_ignoreCacheUpdates) return;

		_cache = CCOnlineScore.create(Numerator.get(), Denominator.get());
	}

	public CCOnlineScore get() {
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
			Numerator,
			Denominator,
		};
	}

	public void set(short n, short d) {
		try
		{
			_ignoreCacheUpdates = true;

			Numerator.set(n);
			Denominator.set(d);
		}
		finally
		{
			_ignoreCacheUpdates = false;
			updateCache();
		}
	}

	public void set(CCOnlineScore v) {
		try
		{
			_ignoreCacheUpdates = true;

			Numerator.set(v.Numerator);
			Denominator.set(v.Denominator);
		}
		finally
		{
			_ignoreCacheUpdates = false;
			updateCache();
		}
	}
}
