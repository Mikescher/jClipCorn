package de.jClipCorn.database.databaseElement.caches;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.datatypes.ITuple;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

import java.util.HashMap;

public abstract class CalculationCache<TOwner> implements ICalculationCache
{
	private final Object NOTFOUND = new Object();

	public static int CacheHits          = 0;
	public static int CacheMisses        = 0;
	public static int PreInitRequests    = 0;
	public static int CacheInvalidations = 0;
	public static int CacheSizeTotal     = 0;

	private final HashMap<String, Object> map = new HashMap<>();

	public void bust()
	{
		if (CCMovieList.hasNoInstanceOrIsInitializingOrIsLoading()) return; // no busting while loading (otherwise we get *a lot* of cache invalidations)

		CacheInvalidations++;
		CacheSizeTotal -= map.size();

		map.clear();
		var oc = getOwnerCache();
		if (oc != null) oc.bust();
	}

	protected abstract TOwner getSource();

	protected abstract ICalculationCache getOwnerCache();

	@SuppressWarnings("unchecked")
	private <TValue> TValue get(String fullkey, Func1to1<TOwner, TValue> o)
	{
		if (CCMovieList.hasNoInstanceOrIsInitializingOrIsLoading()) { PreInitRequests++; return o.invoke(getSource()); }

		var v_map = map.getOrDefault(fullkey, NOTFOUND);
		if (v_map != NOTFOUND) { CacheHits++; return (TValue)v_map; }

		CacheMisses++;
		CacheSizeTotal++;
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

	public boolean getBool  (String key, ITuple params, Func1to1<TOwner, Boolean> o) { return get(key, params, o); }
	public int     getInt   (String key, ITuple params, Func1to1<TOwner, Integer> o) { return get(key, params, o); }
	public double  getDouble(String key, ITuple params, Func1to1<TOwner, Double>  o) { return get(key, params, o); }

	public CCStream<String> listCachedKeys()
	{
		var sn = this.getClass().getSimpleName();
		return CCStreams.iterate(map.keySet()).map(p -> sn+"."+p);
	}

	public static String formatCacheMisses()
	{
		var total = CacheHits + CacheMisses;

		if (total == 0) return String.valueOf(CacheMisses);

		return CacheMisses + " (" + ((CacheMisses * 100) / total) + "%)";
	}

	public static String formatCacheHits()
	{
		var total = CacheHits + CacheMisses;

		if (total == 0) return String.valueOf(CacheHits);

		return CacheHits + " (" + ((CacheHits * 100) / total) + "%)";
	}

	@Override
	protected void finalize() throws Throwable {
		CacheSizeTotal -= map.size();
		super.finalize();
	}
}
