package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.util.List;

public interface ISmartK8ColorQuantizer
{
	void AddColor(RGBColor color);

	List<RGBColor> GetPalette(int colorCount) throws ColorQuantizerException;

	int GetPaletteIndex(RGBColor color);

	int GetColorCount();

	void Clear();
}
