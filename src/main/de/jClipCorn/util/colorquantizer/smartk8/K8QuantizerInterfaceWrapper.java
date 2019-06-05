package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.util.RGBColor;
import java.awt.image.BufferedImage;
import java.util.List;

public class K8QuantizerInterfaceWrapper implements ColorQuantizer
{
	private final ISmartK8ColorQuantizer quant;

	private List<RGBColor> palette;

	public K8QuantizerInterfaceWrapper(ISmartK8ColorQuantizer q) {
		quant = q;
	}

	@Override
	public void analyze(BufferedImage img, int colorcount) throws ColorQuantizerException {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				quant.AddColor(RGBColor.FromRGB(img.getRGB(x, y)));
			}
		}

		palette = quant.GetPalette(colorcount);
	}

	@Override
	public List<RGBColor> getPalette() {
		return palette;
	}

	@Override
	public BufferedImage quantize(BufferedImage img) throws ColorQuantizerException {
		BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {

				int colindex = quant.GetPaletteIndex(RGBColor.FromRGB(img.getRGB(x, y)));

				RGBColor col = palette.get(colindex);

				out.setRGB(x, y, col.toRGB());
			}
		}

		return out;
	}

	@Override
	public int getPaletteIndex(RGBColor c, int x, int y) throws ColorQuantizerException {
		return quant.GetPaletteIndex(c);
	}
}
