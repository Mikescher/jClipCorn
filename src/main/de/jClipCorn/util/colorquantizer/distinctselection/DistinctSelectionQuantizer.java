package de.jClipCorn.util.colorquantizer.distinctselection;

import de.jClipCorn.util.colorquantizer.smartk8.BaseColorCacheQuantizer;
import de.jClipCorn.util.colorquantizer.smartk8.FastRandom;
import de.jClipCorn.util.colorquantizer.smartk8.IColorCache;
import de.jClipCorn.util.colorquantizer.smartk8.caches.OctreeColorCache;
import de.jClipCorn.util.colorquantizer.util.RGBColor;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// @original https://www.codeproject.com/Articles/66341/A-Simple-Yet-Quite-Powerful-Palette-Quantizer-in-C
public class DistinctSelectionQuantizer extends BaseColorCacheQuantizer {

	private List<RGBColor> palette;
	private int foundColorCount;
	private HashMap<Integer, DistinctColorInfo> colorMap;

	private static Tuple<Boolean, List<DistinctColorInfo>> ProcessList(int colorCount, List<DistinctColorInfo> list, List<Func1to1<DistinctColorInfo, Integer>> comparers)
	{
		Func1to1<DistinctColorInfo, Integer> bestComparer = null;
		int maximalCount = 0;
		List<DistinctColorInfo> outputList = list;

		for (Func1to1<DistinctColorInfo, Integer> comparer : comparers)
		{
			List<DistinctColorInfo> filteredList = CCStreams.iterate(list).unique(comparer).enumerate();

			int filteredListCount = filteredList.size();

			if (filteredListCount > colorCount && filteredListCount > maximalCount)
			{
				maximalCount = filteredListCount;
				bestComparer = comparer;
				outputList = filteredList;

				if (maximalCount <= colorCount) break;
			}
		}

		comparers.remove(bestComparer);
		boolean ok = comparers.size() > 0 && maximalCount > colorCount;

		return Tuple.Create(ok, outputList);
	}

	@Override
	protected void OnPrepare(BufferedImage image)
	{
		super.OnPrepare(image);

		OnFinish();
	}

	@Override
	protected IColorCache OnCreateDefaultCache()
	{
		// use OctreeColorCache best performance/quality
		return new OctreeColorCache();
	}

	@Override
	protected void OnAddColor(RGBColor color, int key, int x, int y)
	{
		colorMap.compute(key, (k, val) ->
		{
			if (val == null) return new DistinctColorInfo(color);
			return val.IncreaseCount();
		});
	}

	@Override
	protected List<RGBColor> OnGetPaletteToCache(int colorCount)
	{
		// otherwise calculate one
		palette.clear();

		// lucky seed :)
		FastRandom random = new FastRandom(13);

		List<DistinctColorInfo> colorInfoList = new ArrayList<>(colorMap.values());

		foundColorCount = colorInfoList.size();

		if (foundColorCount >= colorCount)
		{
			// shuffles the colormap
			colorInfoList = CCStreams
					.iterate(colorInfoList)
					.map(v -> Tuple.Create(v, random.Next(foundColorCount)))
					.autosortByProperty(e -> e.Item2)
					.map(v -> v.Item1)
					.enumerate();

			// workaround for backgrounds, the most prevalent color
			DistinctColorInfo background = CCStreams.iterate(colorInfoList).autoMaxValueOrDefault(info -> (info==null) ? 0 : info.Count, null);
			colorInfoList.remove(background);
			colorCount--;

			// generates catalogue
			List<Func1to1<DistinctColorInfo, Integer>> comparers = new ArrayList<>();
			comparers.add(dci -> dci.Hue);
			comparers.add(dci -> dci.Saturation);
			comparers.add(dci -> dci.Brightness);

			// take adequate number from each slot
			for(;;)
			{
				Tuple<Boolean, List<DistinctColorInfo>> rr = ProcessList(colorCount, colorInfoList, comparers);
				colorInfoList = rr.Item2;
				if (!rr.Item1) break;
			}

			int listColorCount = colorInfoList.size();

			if (listColorCount > 0)
			{
				int allowedTake = Math.min(colorCount, listColorCount);
				colorInfoList = CCStreams.iterate(colorInfoList).take(allowedTake).enumerate();
			}

			// adds background color first
			palette.add(RGBColor.FromRGB(background.Color));
		}

		// adds the selected colors to a final palette
		palette.addAll(CCStreams.iterate(colorInfoList).map(ci -> RGBColor.FromRGB(ci.Color)).enumerate());

		// returns our new palette
		return palette;
	}

	@Override
	protected int OnGetColorCount()
	{
		return foundColorCount;
	}

	@Override
	protected void OnFinish()
	{
		super.OnFinish();

		palette  = new ArrayList<>();
		colorMap = new HashMap<>();
	}
}
