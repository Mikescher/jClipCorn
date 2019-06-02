package de.jClipCorn.util.colorquantizer.smartk8.caches;

import de.jClipCorn.util.colorquantizer.smartk8.ColorModelEnum;
import de.jClipCorn.util.colorquantizer.smartk8.ColorModelHelper;
import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.util.List;

public class EuclideanDistanceColorCache extends BaseColorCache
{
	private List<RGBColor> palette;

	@Override
	public boolean getIsColorModelSupported()  { return true; }

	public EuclideanDistanceColorCache()
	{
		ColorModel = ColorModelEnum.RedGreenBlue;
	}

	public EuclideanDistanceColorCache(ColorModelEnum colorModel)
	{
		ColorModel = colorModel;
	}

	@Override
	protected void OnCachePalette(List<RGBColor> palette)
	{
		this.palette = palette;
	}

	@Override
	protected int OnGetColorPaletteIndex(RGBColor color)
	{
		return ColorModelHelper.GetEuclideanDistance(color, ColorModel, palette);
	}

}
