package de.jClipCorn.util.colorquantizer.mediancut;

import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.util.RGBColor;
import de.jClipCorn.util.datatypes.Tuple;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MedianCutCube
{
	// red bounds
	private short redLowBound;
	private short redHighBound;

	// green bounds
	private short greenLowBound;
	private short greenHighBound;

	// blue bounds
	private short blueLowBound;
	private short blueHighBound;

	private final List<RGBColor> colorList;

	public int PaletteIndex;

	public MedianCutCube(List<RGBColor> colors)
	{
		colorList = colors;
		Shrink();
	}

	public int RedSize() { return redHighBound - redLowBound; }

	public int GreenSize() { return greenHighBound - greenLowBound; }

	public int BlueSize() { return blueHighBound - blueLowBound; }

	public RGBColor Color()
	{
		int red = 0, green = 0, blue = 0;

		for (RGBColor value : colorList)
		{
			red += value.R;
			green += value.G;
			blue += value.B;
		}

		red   = colorList.size() == 0 ? 0 : red   / colorList.size();
		green = colorList.size() == 0 ? 0 : green / colorList.size();
		blue  = colorList.size() == 0 ? 0 : blue  / colorList.size();

		return RGBColor.FromRGB(red, green, blue);
	}

	private void Shrink()
	{
		redLowBound = greenLowBound = blueLowBound = 255;
		redHighBound = greenHighBound = blueHighBound = 0;

		for (RGBColor color : colorList)
		{
			if (color.R < redLowBound) redLowBound = color.R;
			if (color.R > redHighBound) redHighBound = color.R;
			if (color.G < greenLowBound) greenLowBound = color.G;
			if (color.G > greenHighBound) greenHighBound = color.G;
			if (color.B < blueLowBound) blueLowBound = color.B;
			if (color.B > blueHighBound) blueHighBound = color.B;
		}
	}

	public Tuple<MedianCutCube, MedianCutCube> SplitAtMedian(int componentIndex) throws ColorQuantizerException {
		List<RGBColor> colors;

		switch (componentIndex)
		{
			// red colors
			case 0:
				colors = colorList.stream().sorted(Comparator.comparingInt(a -> a.R)).collect(Collectors.toList());
				break;

			// green colors
			case 1:
				colors = colorList.stream().sorted(Comparator.comparingInt(a -> a.G)).collect(Collectors.toList());
				break;

			// blue colors
			case 2:
				colors = colorList.stream().sorted(Comparator.comparingInt(a -> a.B)).collect(Collectors.toList());
				break;

			default:
				throw new ColorQuantizerException("Only three color components are supported (R, G and B)."); //$NON-NLS-1$

		}

		// retrieves the median index (a half point)
		int medianIndex = colorList.size() >> 1;

		// creates the two half-cubes
		MedianCutCube firstMedianCutCube = new MedianCutCube(colors.subList(0, medianIndex));
		MedianCutCube secondMedianCutCube = new MedianCutCube(colors.subList(medianIndex, colors.size()));

		return Tuple.Create(firstMedianCutCube, secondMedianCutCube);
	}

	public void SetPaletteIndex(int newPaletteIndex)
	{
		PaletteIndex = newPaletteIndex;
	}

	public boolean IsColorIn(RGBColor color)
	{
		return (color.R >= redLowBound && color.R <= redHighBound) &&
				(color.G >= greenLowBound && color.G <= greenHighBound) &&
				(color.B >= blueLowBound && color.B <= blueHighBound);
	}
}
