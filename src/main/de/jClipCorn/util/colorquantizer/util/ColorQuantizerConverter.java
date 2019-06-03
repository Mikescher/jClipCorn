package de.jClipCorn.util.colorquantizer.util;

import de.jClipCorn.util.colorquantizer.ColorQuantizer;
import de.jClipCorn.util.colorquantizer.ColorQuantizerException;
import de.jClipCorn.util.helper.ImageUtilities;

import java.awt.image.BufferedImage;
import java.util.List;

public class ColorQuantizerConverter {

	public static byte[] quantizeTo4BitRaw(ColorQuantizer quant, BufferedImage img) throws ColorQuantizerException {
		int w = img.getWidth();
		int h = img.getHeight();

		if (w>128) throw new ColorQuantizerException("Image too wide ("+w+")"); //$NON-NLS-1$ //$NON-NLS-2$
		if (h>128) throw new ColorQuantizerException("Image too high ("+h+")"); //$NON-NLS-1$ //$NON-NLS-2$

		List<RGBColor> palette = quant.getPalette();

		byte[] output = new byte[ 2 + 16*3 + ((w*h+1)/2) ];

		output[0] = (byte)w;
		output[1] = (byte)h;

		for (int i = 0; i < 16; i++) {
			if (palette.size()>i) {
				output[2 + i*3    ] = (byte)palette.get(i).R;
				output[2 + i*3 + 1] = (byte)palette.get(i).G;
				output[2 + i*3 + 2] = (byte)palette.get(i).B;
			} else {
				output[2 + i*3    ] = 0;
				output[2 + i*3 + 1] = 0;
				output[2 + i*3 + 2] = 0;
			}
		}

		boolean coll = true;
		byte v = 0x00;
		int arridx = 2 + 16*3;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (coll) {
					v = (byte)quant.getPaletteIndex(RGBColor.FromRGB(img.getRGB(x, y)), x, y);
					coll = false;
				} else {
					byte v2 = (byte)quant.getPaletteIndex(RGBColor.FromRGB(img.getRGB(x, y)), x, y);
					output[arridx] = (byte)(((v<<4) & 0xF0) | ((v2) & 0x0F));
					arridx++;
					coll = true;
				}
			}
		}
		if (!coll) output[arridx] = (byte)(v<<4);

		return output;
	}

	public static BufferedImage quantizeFrom4BitRaw(byte[] data) throws ColorQuantizerException {
		int w = data[0];
		int h = data[1];

		if (w<0) throw new ColorQuantizerException("Invalid width ("+w+")"); //$NON-NLS-1$ //$NON-NLS-2$
		if (h<0) throw new ColorQuantizerException("Invalid height ("+h+")"); //$NON-NLS-1$ //$NON-NLS-2$

		RGBColor[] palette = new RGBColor[16];

		for (int i = 0; i < 16; i++) {
			short r = data[2 + i*3    ]; if (r<0) r+=256;
			short g = data[2 + i*3 + 1]; if (g<0) g+=256;
			short b = data[2 + i*3 + 2]; if (b<0) b+=256;

			palette[i] = RGBColor.FromRGB(r, g, b);
		}

		BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		int offset = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {

				if (offset % 2 == 0) {
					int idx = data[2 + 16*3 + offset/2];
					idx = (idx>>4)&0x0F;

					output.setRGB(x, y, palette[idx].toRGB());
				} else {
					int idx = data[2 + 16*3 + offset/2];
					idx = idx&0x0F;

					output.setRGB(x, y, palette[idx].toRGB());
				}

				offset++;
			}
		}

		return output;
	}

	public static BufferedImage shrink(BufferedImage img, int width) {
		int height = Math.toIntExact(Math.round((img.getHeight() * 1d / img.getWidth()) * width));

		return ImageUtilities.resize(img, width, height);
	}
}
