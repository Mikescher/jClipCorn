package de.jClipCorn.database.elementProps.packs;

import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.database.elementProps.impl.EStringProp;
import de.jClipCorn.util.Str;

public class EPartArrayPropPack {
	public final static int PARTCOUNT_MAX = 6; // 0 .. 5

	public final EStringProp Part0;
	public final EStringProp Part1;
	public final EStringProp Part2;
	public final EStringProp Part3;
	public final EStringProp Part4;
	public final EStringProp Part5;

	private String[] _cache = null;
	private IEProperty[] _properties = null;
	private boolean _ignoreCacheUpdates = false;

	public EPartArrayPropPack(String namePrefix, String[] _startValue, IPropertyParent parent, EPropertyType proptype)
	{
		Part0 = new EStringProp(namePrefix + "[0]", Str.Empty, parent, proptype);
		Part1 = new EStringProp(namePrefix + "[1]", Str.Empty, parent, proptype);
		Part2 = new EStringProp(namePrefix + "[2]", Str.Empty, parent, proptype);
		Part3 = new EStringProp(namePrefix + "[3]", Str.Empty, parent, proptype);
		Part4 = new EStringProp(namePrefix + "[4]", Str.Empty, parent, proptype);
		Part5 = new EStringProp(namePrefix + "[5]", Str.Empty, parent, proptype);

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

		_cache = new String[]
		{
			Part0.get(),
			Part1.get(),
			Part2.get(),
			Part3.get(),
			Part4.get(),
			Part5.get()
		};
	}

	public String[] getAsArray() {
		return _cache;
	}

	public String get(int idx) {
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
		set(idx, Str.Empty);
	}

	public void set(int idx, String v) {
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

	public void set(String p0, String p1, String p2, String p3, String p4, String p5) {
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
