package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.util.RGBColor;
import java.util.List;

public interface IColorCache
{
	void Prepare();

	void CachePalette(List<RGBColor> palette);

	int GetColorPaletteIndex(RGBColor color);
}
