package de.jClipCorn.util.colorquantizer.smartk8.caches;

import de.jClipCorn.util.colorquantizer.smartk8.ColorModelEnum;
import de.jClipCorn.util.colorquantizer.smartk8.ColorModelHelper;
import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OctreeColorCache extends BaseColorCache
{
	private OctreeCacheNode root;

	@Override
	public boolean getIsColorModelSupported() { return false; }

	public OctreeColorCache()
	{
		ColorModel = ColorModelEnum.RedGreenBlue;
		root = new OctreeCacheNode();
	}

	@Override
	public void Prepare()
	{
		super.Prepare();
		root = new OctreeCacheNode();
	}

	@Override
	protected void OnCachePalette(List<RGBColor> palette)
	{
		int index = 0;

		for (RGBColor color : palette)
		{
			root.AddColor(color, index++, 0);
		}
	}

	@Override
	protected int OnGetColorPaletteIndex(RGBColor color)
	{
		HashMap<Integer, RGBColor> candidates = root.GetPaletteIndex(color, 0);

		int paletteIndex = 0;
		int index = 0;
		int colorIndex = ColorModelHelper.GetEuclideanDistance(color, ColorModel, new ArrayList<>(candidates.values()));

		for (int colorPaletteIndex : candidates.keySet())
		{
			if (index == colorIndex)
			{
				paletteIndex = colorPaletteIndex;
				break;
			}

			index++;
		}

		return paletteIndex;
	}
}
