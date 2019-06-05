package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.util.List;

public interface ISmartK8ColorQuantizer
{
	void AddColor(RGBColor color) throws ColorQuantizerException;

	List<RGBColor> GetPalette(int colorCount) throws ColorQuantizerException;

	int GetPaletteIndex(RGBColor color) throws ColorQuantizerException;

	int GetColorCount() throws ColorQuantizerException;

	void Clear() throws ColorQuantizerException;
}
