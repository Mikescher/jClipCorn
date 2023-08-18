package de.jClipCorn.util.helper;

import java.awt.*;

public class ColorHelper
{
	public static Color mix(Color ca, Color cb, double percent)
	{
		var r = (int) (ca.getRed()   * percent + cb.getRed()   * (1.0 - percent));
		var g = (int) (ca.getGreen() * percent + cb.getGreen() * (1.0 - percent));
		var b = (int) (ca.getBlue()  * percent + cb.getBlue()  * (1.0 - percent));

		return new Color(r, g, b);
	}
}
