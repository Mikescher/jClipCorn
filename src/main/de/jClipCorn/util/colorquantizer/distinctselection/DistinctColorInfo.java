package de.jClipCorn.util.colorquantizer.distinctselection;

import de.jClipCorn.util.colorquantizer.util.RGBColor;

public class DistinctColorInfo {
	private static final int Factor = 5000000;

	// The original color.
	public final int Color;

	// A hue component of the color.
	public final int Hue;

	// A saturation component of the color.
	public final int Saturation;

	// A brightness component of the color.
	public final int Brightness;

	// The pixel presence count in the image.
	public int Count;

	public DistinctColorInfo(RGBColor color)
	{
		Color = color.toRGB();
		Count = 1;

		Hue        = (int)(color.getHue()        * Factor);
		Saturation = (int)(color.getSaturation() * Factor);
		Brightness = (int)(color.getBrightness() * Factor);
	}

	/// <summary>
	/// Increases the count of pixels of this color.
	/// </summary>
	public DistinctColorInfo IncreaseCount()
	{
		Count++;
		return this;
	}
}
