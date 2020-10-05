package de.jClipCorn.database.databaseElement.caches;

import de.jClipCorn.util.datatypes.ITuple;
import de.jClipCorn.util.lambda.Func1to1;

import java.util.HashMap;

public abstract class CalculationCache<TOwner> implements ICalculationCache
{
	private final Object NOTFOUND = new Object();

	public static int CacheHits   = 0;
	public static int CacheMisses = 0;

	private final HashMap<String, Object> map = new HashMap<>();

	public void bust()
	{
		map.clear();
		var oc = getOwnerCache();
		if (oc != null) oc.bust();
	}

	protected abstract TOwner getSource();

	protected abstract ICalculationCache getOwnerCache();

	@SuppressWarnings("unchecked")
	private <TValue> TValue get(String fullkey, Func1to1<TOwner, TValue> o)
	{
		var v_map = map.getOrDefault(fullkey, NOTFOUND);
		if (v_map != NOTFOUND) return (TValue)v_map;

		var v_calc = o.invoke(getSource());
		map.put(fullkey, v_calc);
		return v_calc;
	}

	public <TValue> TValue get(String key, ITuple params, Func1to1<TOwner, TValue> o)
	{
		if (params == null)
			return get(key, o);
		else
			return get(key + "[" + params.toString() + "]", o);
	}

	public boolean getBool  (String key, ITuple params, Func1to1<TOwner, Boolean> o) { return get(key, o); }
	public int     getInt   (String key, ITuple params, Func1to1<TOwner, Integer> o) { return get(key, o); }
	public double  getDouble(String key, ITuple params, Func1to1<TOwner, Double>  o) { return get(key, o); }
}
