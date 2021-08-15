package de.jClipCorn.util.colorquantizer.wadcolors;

import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.smartk8.ColorModelEnum;
import de.jClipCorn.util.colorquantizer.smartk8.ColorModelHelper;
import de.jClipCorn.util.colorquantizer.util.RGBColor;

import java.awt.image.BufferedImage;
import java.util.List;

// @source: https://alumni.media.mit.edu/~wad/color/palette.html
public class WadColorPaletteQuantizer implements ColorQuantizer {

	private static final RGBColor[] palette = new RGBColor[]
	{
		RGBColor.FromRGB(0,   0,   0),
		RGBColor.FromRGB(87,  87,  87),
		RGBColor.FromRGB(173, 35,  35),
		RGBColor.FromRGB(42,  75,  215),
		RGBColor.FromRGB(29,  105, 20),
		RGBColor.FromRGB(129, 74,  25),
		RGBColor.FromRGB(129, 38,  192),
		RGBColor.FromRGB(160, 160, 160),
		RGBColor.FromRGB(129, 197, 122),
		RGBColor.FromRGB(157, 175, 255),
		RGBColor.FromRGB(41,  208, 208),
		RGBColor.FromRGB(255, 146, 51),
		RGBColor.FromRGB(255, 238, 51),
		RGBColor.FromRGB(233, 222, 187),
		RGBColor.FromRGB(255, 205, 243),
		RGBColor.FromRGB(255, 255, 255),
	};

	private final List<RGBColor> paletteList = List.of(palette);

	@Override
	public void analyze(BufferedImage img, int colorcount) throws ColorQuantizerException {
		if (colorcount != 16) throw new ColorQuantizerException("WadColorPaletteQuantizer can only be used for 4bit palettes"); //$NON-NLS-1$
	}

	@Override
	public List<RGBColor> getPalette() {
		return paletteList;
	}

	@Override
	public BufferedImage quantize(BufferedImage img) throws ColorQuantizerException {
		BufferedImage output = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				output.setRGB(x, y, palette[getPaletteIndex(RGBColor.FromRGB(img.getRGB(x, y)), x, y)].toRGB());
			}
		}

		return output;
	}

	@Override
	public int getPaletteIndex(RGBColor c, int x, int y) {
		return ColorModelHelper.GetEuclideanDistance(c, ColorModelEnum.LabColorSpace, paletteList);
	}
}
