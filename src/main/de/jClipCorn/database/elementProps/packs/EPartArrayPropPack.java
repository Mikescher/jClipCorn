package de.jClipCorn.database.elementProps.packs;

import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.database.elementProps.impl.ECCPathProp;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.util.filesystem.CCPath;

public class EPartArrayPropPack {
	public final static int PARTCOUNT_MAX = 6; // 0 .. 5

	public final ECCPathProp Part0;
	public final ECCPathProp Part1;
	public final ECCPathProp Part2;
	public final ECCPathProp Part3;
	public final ECCPathProp Part4;
	public final ECCPathProp Part5;

	private CCPath[] _cache = null;
	private IEProperty[] _properties = null;
	private boolean _ignoreCacheUpdates = false;

	public EPartArrayPropPack(String namePrefix, CCPath _startValue, IPropertyParent parent, EPropertyType proptype)
	{
		Part0 = new ECCPathProp(namePrefix + "[0]", _startValue, parent, proptype);
		Part1 = new ECCPathProp(namePrefix + "[1]", _startValue, parent, proptype);
		Part2 = new ECCPathProp(namePrefix + "[2]", _startValue, parent, proptype);
		Part3 = new ECCPathProp(namePrefix + "[3]", _startValue, parent, proptype);
		Part4 = new ECCPathProp(namePrefix + "[4]", _startValue, parent, proptype);
		Part5 = new ECCPathProp(namePrefix + "[5]", _startValue, parent, proptype);

		Part0.addChangeListener((_1, _2, _3) -> updateCache());
		Part1.addChangeListener((_1, _2, _3) -> updateCache());
		Part2.addChangeListener((_1, _2, _3) -> updateCache());
		Part3.addChangeListener((_1, _2, _3) -> updateCache());
		Part4.addChangeListener((_1, _2, _3) -> updateCache());
		Part5.addChangeListener((_1, _2, _3) -> updateCache());

		updateCache();
	}

	private void updateCache() {
		if (_ignoreCacheUpdates) return;

		_cache = new CCPath[]
		{
			Part0.get(),
			Part1.get(),
			Part2.get(),
			Part3.get(),
			Part4.get(),
			Part5.get()
		};
	}

	public CCPath[] getAsArray() {
		return _cache;
	}

	public CCPath get(int idx) {
		return getAsArray()[idx];
	}

	public IEProperty[] getProperties()
	{
		if (_properties == null) _properties = listProperties();
		return _properties;
	}

	private IEProperty[] listProperties() {
		return new IEProperty[]
		{
			Part0,
			Part1,
			Part2,
			Part3,
			Part4,
			Part5,
		};
	}

	public void reset(int idx) {
		set(idx, CCPath.Empty);
	}

	public void set(int idx, CCPath v) {
		switch (idx) {
			case 0: Part0.set(v); break;
			case 1: Part1.set(v); break;
			case 2: Part2.set(v); break;
			case 3: Part3.set(v); break;
			case 4: Part4.set(v); break;
			case 5: Part5.set(v); break;

			default: throw new Error("Invalid PartArray Index: " + idx);
		}
	}

	public void set(CCPath p0, CCPath p1, CCPath p2, CCPath p3, CCPath p4, CCPath p5) {
		try
		{
			_ignoreCacheUpdates = true;

			Part0.set(p0);
			Part1.set(p1);
			Part2.set(p2);
			Part3.set(p3);
			Part4.set(p4);
			Part5.set(p5);
		}
		finally
		{
			_ignoreCacheUpdates = false;
			updateCache();
		}
	}
}
