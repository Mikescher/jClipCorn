package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.awt.image.BufferedImage;
import java.util.List;

// @original https://www.codeproject.com/Articles/66341/A-Simple-Yet-Quite-Powerful-Palette-Quantizer-in-C
public abstract class BaseColorCacheQuantizer extends BaseColorQuantizer
{
	private IColorCache colorCache;

	protected BaseColorCacheQuantizer()
	{
		colorCache = null;
	}

	public void ChangeCacheProvider(IColorCache colorCache)
	{
		this.colorCache = colorCache;
	}

	public void CachePalette(List<RGBColor> palette)
	{
		GetColorCache().CachePalette(palette);
	}

	private IColorCache GetColorCache()
	{
		// if there is no cache, it attempts to create a default cache; integrated in the quantizer
		IColorCache result = colorCache;
		if (result == null) colorCache = OnCreateDefaultCache();

		// if the cache exists; or default one was created for these purposes.. use it
		if (result == null)
		{
			String message = "The color cache is not initialized! Please use SetColorCache() method on quantizer."; //$NON-NLS-1$
			throw new NullPointerException(message);
		}

		// cache is fine, return it
		return result;
	}

	protected abstract IColorCache OnCreateDefaultCache();

	protected abstract List<RGBColor> OnGetPaletteToCache(int colorCount);

	@Override
	protected void OnPrepare(BufferedImage image)
	{
		super.OnPrepare(image);

		GetColorCache().Prepare();
	}

	@Override
	protected List<RGBColor> OnGetPalette(int colorCount)
	{
		// use optimization, or calculate new palette if color count is lower than unique color count
		List<RGBColor> palette = super.OnGetPalette(colorCount);
		if(palette == null) OnGetPaletteToCache(colorCount);

		GetColorCache().CachePalette(palette);
		return palette;
	}

	@Override
	protected int OnGetPaletteIndex(RGBColor color, int key, int x, int y)
	{
		int paletteIndex = super.OnGetPaletteIndex(color, key, x, y);

		// if not determined, use cache to determine the index
		if (paletteIndex == InvalidIndex)
		{
			paletteIndex = GetColorCache().GetColorPaletteIndex(color);
		}

		return paletteIndex;
	}
}
