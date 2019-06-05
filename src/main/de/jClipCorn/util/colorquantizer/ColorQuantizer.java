package de.jClipCorn.util.colorquantizer;

import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ColorQuantizer {

	void analyze(BufferedImage img, int colorcount) throws ColorQuantizerException;
	List<RGBColor> getPalette() throws ColorQuantizerException;
	BufferedImage quantize(BufferedImage img) throws ColorQuantizerException;

	int getPaletteIndex(RGBColor c, int x, int y) throws ColorQuantizerException;
}
