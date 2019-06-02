package de.jClipCorn.util.colorquantizer.smartk8.caches;

import de.jClipCorn.util.colorquantizer.smartk8.ColorModelEnum;
import de.jClipCorn.util.colorquantizer.smartk8.IColorCache;
import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.util.HashMap;
import java.util.List;

public abstract class BaseColorCache implements IColorCache
{
	private final HashMap<Integer, Integer> cache;

	protected ColorModelEnum ColorModel;

	public abstract boolean getIsColorModelSupported();

	protected BaseColorCache()
	{
		cache = new HashMap<>();
	}

	public void ChangeColorModel(ColorModelEnum colorModel)
	{
		ColorModel = colorModel;
	}

	protected abstract void OnCachePalette(List<RGBColor> palette);

	protected abstract int OnGetColorPaletteIndex(RGBColor color);

	@Override
	public void Prepare()
	{
		cache.clear();
	}

	@Override
	public void CachePalette(List<RGBColor> palette)
	{
		OnCachePalette(palette);
	}

	@Override
	public int GetColorPaletteIndex(RGBColor color)
	{
		int key = color.toRGB();

		return cache.compute(key, (k, old) ->
		{
			if (old != null) return old; else return OnGetColorPaletteIndex(color);
		});
	}
}
