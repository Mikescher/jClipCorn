package de.jClipCorn.util.colorquantizer.smartk8;

import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.colorquantizer.octree.OctreeException;
import de.jClipCorn.util.colorquantizer.smartk8.caches.EuclideanDistanceColorCache;
import de.jClipCorn.util.colorquantizer.util.RGBColor;
import de.jClipCorn.util.colorquantizer.util.XYCoord;

import java.awt.image.BufferedImage;
import java.util.List;

public class K8QuantizerWrapper implements ColorQuantizer
{
	private final BaseColorCacheQuantizer quant;

	private List<RGBColor> palette;

	public K8QuantizerWrapper(BaseColorCacheQuantizer q) {
		quant = q;
	}

	@Override
	public void analyze(BufferedImage img, int colorcount) {
		quant.ChangeCacheProvider(new EuclideanDistanceColorCache());
		
		quant.OnPrepare(img);

		for (XYCoord crd : quant.GetPointPath(img.getWidth(), img.getHeight())) quant.AddColor(RGBColor.FromRGB(img.getRGB(crd.X, crd.Y)), crd.X, crd.Y);

		palette = quant.GetPalette(colorcount);
	}

	@Override
	public List<RGBColor> getPalette() {
		return palette;
	}

	@Override
	public BufferedImage quantize(BufferedImage img) {
		BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {

				int colindex = quant.GetPaletteIndex(RGBColor.FromRGB(img.getRGB(x, y)), x, y);

				RGBColor col = palette.get(colindex);

				out.setRGB(x, y, col.toRGB());
			}
		}

		return out;
	}

	@Override
	public int getPaletteIndex(RGBColor c, int x, int y) {
		return quant.GetPaletteIndex(c, x, y);
	}
}
