package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.util.RGBColor;
import de.jClipCorn.util.datatypes.Tuple3;

import java.util.List;

public class ColorModelHelper
{
	public static long GetColorEuclideanDistance(ColorModelEnum colorModel, RGBColor requestedColor, RGBColor realColor)
	{
		Tuple3<Float, Float, Float> components = requestedColor.GetColorComponents(colorModel, realColor);
		return (long) (components.Item1 * components.Item1 + components.Item2 * components.Item2 + components.Item3 * components.Item3);
	}

	public static int GetEuclideanDistance(RGBColor color, ColorModelEnum colorModel, List<RGBColor> palette)
	{
		// initializes the best difference, set it for worst possible, it can only get better
		long leastDistance = Long.MAX_VALUE;
		int result = 0;

		for (int index = 0; index < palette.size(); index++)
		{
			RGBColor targetColor = palette.get(index);
			long distance = GetColorEuclideanDistance(colorModel, color, targetColor);

			// if a difference is zero, we're good because it won't get better
			if (distance == 0)
			{
				result = index;
				break;
			}

			// if a difference is the best so far, stores it as our best candidate
			if (distance < leastDistance)
			{
				leastDistance = distance;
				result = index;
			}
		}

		return result;
	}

}
