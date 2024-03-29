package de.jClipCorn.util.helper;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ImageUtilities {
	public final static int BASE_COVER_WIDTH  = 182;
	public final static int BASE_COVER_HEIGHT = 254;

	public final static int HALF_COVER_WIDTH  = BASE_COVER_WIDTH / 2;
	public final static int HALF_COVER_HEIGHT = BASE_COVER_HEIGHT / 2;
	
	public final static double COVER_RATIO = BASE_COVER_WIDTH / (BASE_COVER_HEIGHT * 1d);
	private final static double MAX_RATIO_DIFF = 0.02;
	
	private final static int SERIES_MASK_WIDTH  = 56;
	private final static int SERIES_MASK_HEIGHT = 56;

	public static int getCoverWidth(CCProperties ccprops) {
		return ccprops.PROP_DATABASE_MAX_COVER_SIZE.getValue().getWidth();
	}

	public static int getCoverHeight(CCProperties ccprops) {
		return ccprops.PROP_DATABASE_MAX_COVER_SIZE.getValue().getHeight();
	}
	
	public static BufferedImage resizeCoverImageForFullSizeUI(BufferedImage bi) {
		return resizeImageProportional(bi, BASE_COVER_WIDTH, BASE_COVER_HEIGHT, COVER_RATIO);
	}
	
	public static BufferedImage resizeCoverImageForHalfSizeUI(BufferedImage bi) {
		return resizeImageProportional(bi, HALF_COVER_WIDTH, HALF_COVER_HEIGHT, COVER_RATIO);
	}

	public static BufferedImage resizeCoverImageForStorage(BufferedImage bi, CCProperties ccprops) {
		return resizeImageToBounds(bi, BASE_COVER_WIDTH, BASE_COVER_HEIGHT, getCoverWidth(ccprops), getCoverHeight(ccprops));
	}

	public static Tuple<Integer, Integer> calcImageSizeForStorage(int widthCurr, int heightCurr, CCProperties ccprops) {
		return calcImagesizeToBounds(widthCurr, heightCurr, BASE_COVER_WIDTH, BASE_COVER_HEIGHT, getCoverWidth(ccprops), getCoverHeight(ccprops));
	}

	public static Tuple<Integer, Integer> calcImageSizeToFit(int widthCurr, int heightCurr, int fitWidth, int fitHeight) {
		return calcImagesizeToBounds(widthCurr, heightCurr, fitWidth, fitHeight, fitWidth, fitHeight);
	}
	
	private static BufferedImage resizeImageProportional(BufferedImage bi, final int newWidth, final int newHeight, final double newRatio) {
		if (isCorrectlySized(bi, newWidth, newHeight)) return bi;
		
		double ratio = bi.getWidth() / (bi.getHeight() * 1d);
		
		int nW;
		int nH;
		
		if (ratio > newRatio) {
			nW = newWidth;
			nH = (int) Math.round(1 / ratio * newWidth);
		} else {
			nH = newHeight;
			nW = (int) Math.round(ratio * newHeight);
		}
		
		nW = Math.max(1, nW);
		nH = Math.max(1, nH);
		
		int nX = (newWidth - nW) / 2;
		int nY = (newHeight - nH) / 2;
		
		BufferedImage result = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		
		result.createGraphics().drawImage(progressiveResize(bi, nW, nH), nX, nY, null);
		
		return result;
	}
	
	private static BufferedImage resizeImageToBounds(BufferedImage bi, int minW, int minH, int maxW, int maxH) {
		
		Tuple<Integer, Integer> size = calcImagesizeToBounds(bi.getWidth(), bi.getHeight(), minW, minH, maxW, maxH);

		if (size.Item1 == bi.getWidth() && size.Item2 == bi.getHeight()) return bi;
		
		return progressiveResize(bi, size.Item1, size.Item2);
		
	}
	
	private static Tuple<Integer, Integer> calcImagesizeToBounds(int w, int h, int minW, int minH, int maxW, int maxH) {
		if (w > maxW || h > maxH) {
			
			// make smaller -> set to [ maxW | maxH ]

			double ratio = w / (h * 1d);
			double newRatio = maxW / (maxH * 1d);
			
			int nW;
			int nH;
			
			if (ratio > newRatio) {
				nW = maxW;
				nH = (int) Math.round(1 / ratio * maxW);
			} else {
				nH = maxH;
				nW = (int) Math.round(ratio * maxH);
			}
			
			nW = Math.max(1, nW);
			nH = Math.max(1, nH);

			return Tuple.Create(nW, nH);
			
		} else if (w < minW || h < minH) {
			
			// make bigger ->  [ minW | minH ] but no bigger than [ maxW | maxH ]

			double ratio = w / (h * 1d);
			double newRatio = minW / (minH * 1d);

			int nW;
			int nH;

			if (ratio > newRatio) {
				nH = minH;
				nW = (int) Math.round(ratio * minH);
			} else {
				nW = minW;
				nH = (int) Math.round(1 / ratio * minW);
			}

			if (nW > maxW) {
				nH *= maxW / (1d * nW);
				nW  = maxW;
			}

			if (nH > maxH) {
				nW *= maxH / (1d * nH);
				nH  = maxH;
			}

			return Tuple.Create(nW, nH);
			
		} else {
			
			// ok
			
			return Tuple.Create(w, h);
		}
	}
	
	private static BufferedImage progressiveResize(BufferedImage source, int width, int height) {
        int imgw = Math.max(source.getWidth() / 2, width);
        int imgh = Math.max(source.getHeight() / 2, height);
        BufferedImage img = commonResize(source, imgw, imgh);
        while (imgw != width || imgh != height) {
            BufferedImage prev = img;
            imgw = Math.max(imgw / 2, width);
            imgh = Math.max(imgh / 2, height);
            img = commonResize(prev, imgw, imgh);
            prev.flush();
        }
        return img;
    }
	
    private static BufferedImage commonResize(BufferedImage source, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, source.getType());
        Graphics2D graphics = img.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(source, 0, 0, width, height, null);
        } finally {
            graphics.dispose();
        }
        return img;
    }
	
	public static BufferedImage resize(BufferedImage bi, int width, int height) {
		if (bi.getWidth() == width && bi.getHeight() == height) {
			return bi;
		}
		
		BufferedImage resizedImage = new BufferedImage(width, height, bi.getType());
		Graphics2D graphics = resizedImage.createGraphics();
		graphics.drawImage(bi, 0, 0, width, height, null);
		graphics.dispose();
		
		graphics.setComposite(AlphaComposite.Src);
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}
    
	private static boolean isCorrectlySized(BufferedImage bb, int prefWidth, int prefHeight) {
		return (bb != null) && (bb.getWidth() == prefWidth) && (bb.getHeight() == prefHeight);
	}
	
	public static void makeFullSizeSeriesCover(BufferedImage cvr) {
		
		cvr = resizeCoverImageForFullSizeUI(cvr);
		
		Point tl = getTopLeftNonTransparentPixel(cvr);
		tl.setLocation(Math.min(tl.x, BASE_COVER_WIDTH - SERIES_MASK_WIDTH), Math.min(tl.y, BASE_COVER_HEIGHT - SERIES_MASK_HEIGHT)); // Nicht zu weit rechts | Nicht zu weit unten
		
		cvr.getGraphics().drawImage(Resources.IMG_COVER_SERIES_MASK.get(), tl.x, tl.y, null);
	}
	
	public static Point getTopLeftNonTransparentPixel(BufferedImage i) {
		for (int x = 0; x < i.getWidth(); x++) {
			for (int y = 0; y < i.getHeight(); y++) {
				if ((i.getRGB(x, y) >> 24) != 0) {
					return new Point(x, y);
				}
			}
		}
		return new Point(i.getWidth() - 1, i.getHeight() - 1);
	}
	
	public static Point getTopRightNonTransparentPixel(BufferedImage i) {
		for (int x = i.getWidth()-1; x >= 0; x--) {
			for (int y = 0; y < i.getHeight(); y++) {
				if ((i.getRGB(x, y) >> 24) != 0) {
					return new Point(x, y);
				}
			}
		}
		return new Point(0, i.getHeight() - 1);
	}
	
	public static void drawBorder(BufferedImage i, Color c, int thick) {
		Graphics ig = i.getGraphics();
		
		ig.setColor(c);
		
		ig.fillRect(0, 0, thick, i.getHeight());
		ig.fillRect(0, 0, i.getWidth(), thick);
		ig.fillRect(i.getWidth() - thick, 0, thick, i.getHeight());
		ig.fillRect(0, i.getHeight() - thick, i.getWidth(), thick);
	}
	
	public static void drawActualBorder(BufferedImage i, Color c, int thick) {
		Rectangle rect = getNonTransparentImageRect(i);
		
		Graphics ig = i.getGraphics();
		
		ig.setColor(c);
		
		ig.fillRect(rect.x, rect.y, thick, rect.height);
		ig.fillRect(rect.x, rect.y, rect.width, thick);
		ig.fillRect(rect.x + rect.width - thick, rect.y, thick, rect.height);
		ig.fillRect(rect.x, rect.y + rect.height - thick, rect.width, thick);
	}
	
	public static Rectangle getNonTransparentImageRect(BufferedImage i) {
		int rx = 0;
		int ry = 0;
		int rwx = i.getWidth();
		int rhy = i.getHeight();
		
		for (int x = 0; x < rwx; x++) {
			if (isColumnFullyTransparent(i, x)) rx = x+1;
			else break;
		}
		
		for (int x = rwx-1; x >= 0; x--) {
			if (isColumnFullyTransparent(i, x)) rwx = x;
			else break;
		}
		
		for (int y = 0; y < rhy; y++) {
			if (isRowFullyTransparent(i, y)) ry = y+1;
			else break;
		}
		
		for (int y = rhy-1; y >= 0; y--) {
			if (isRowFullyTransparent(i, y)) rhy = y;
			else break;
		}
		
		if (rx >= (i.getWidth() - 1) || rwx <= 0 || rx >= rwx) {
			rx = 0;
			rwx = i.getWidth();
		}
		
		if (ry >= (i.getHeight()-1) || rhy <= 0 || ry >= rhy) {
			ry = 0;
			rhy = i.getHeight();
		}
		
		return new Rectangle(rx, ry, rwx - rx, rhy - ry);
	}
	
	public static Rectangle getNonColorImageRect(BufferedImage i, int col) {
		int rx = 0;
		int ry = 0;
		int rwx = i.getWidth();
		int rhy = i.getHeight();
		
		for (int x = 0; x < rwx; x++) {
			if (isColumnFullyColor(i, x, col)) rx = x+1;
			else break;
		}
		
		for (int x = rwx-1; x >= 0; x--) {
			if (isColumnFullyColor(i, x, col)) rwx = x;
			else break;
		}
		
		for (int y = 0; y < rhy; y++) {
			if (isRowFullyColor(i, y, col)) ry = y+1;
			else break;
		}
		
		for (int y = rhy-1; y >= 0; y--) {
			if (isRowFullyColor(i, y, col)) rhy = y;
			else break;
		}
		
		if (rx >= (i.getWidth() - 1) || rwx <= 0 || rx >= rwx) {
			rx = 0;
			rwx = i.getWidth();
		}
		
		if (ry >= (i.getHeight()-1) || rhy <= 0 || ry >= rhy) {
			ry = 0;
			rhy = i.getHeight();
		}
		
		return new Rectangle(rx, ry, rwx - rx, rhy - ry);
	}
	
	public static boolean isRowFullyTransparent(BufferedImage i, int row) {
		for (int x = 0; x < i.getWidth(); x++) {
			if (((i.getRGB(x, row) >> 24) & 0xFF) != 0) return false;
		}
		
		return true;
	}
	
	public static boolean isColumnFullyTransparent(BufferedImage i, int col) {
		for (int y = 0; y < i.getHeight(); y++) {
			if (((i.getRGB(col, y) >> 24) & 0xFF) != 0) return false;
		}
		
		return true;
	}
	
	public static boolean isRowFullyColor(BufferedImage i, int row, int clr) {
		for (int x = 0; x < i.getWidth(); x++) {
			if ((i.getRGB(x, row) ^ clr) != 0) return false;
		}
		
		return true;
	}
	
	public static boolean isColumnFullyColor(BufferedImage i, int col, int clr) {
		for (int y = 0; y < i.getHeight(); y++) {
			if ((i.getRGB(col, y) ^ clr) != 0) return false;
		}
		
		return true;
	}
	
	public static BufferedImage iconToImage(ImageIcon ic) {
		BufferedImage bi = new BufferedImage(ic.getIconWidth(), ic.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		bi.getGraphics().drawImage(ic.getImage(), 0, 0, null);
		return bi;
	}
	
	public static byte[] imageToByteArray(BufferedImage bi) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			ImageIO.write(bi, "png", baos ); //$NON-NLS-1$
			byte[] data = baos.toByteArray();
			
			baos.close();
			
			return data;
		} catch (IOException e) {
			CCLog.addError(e);
			return null;
		}
	}
	
	public static BufferedImage byteArrayToImage(byte[] imgarr) {
		try {
			return ImageIO.read(new ByteArrayInputStream(imgarr));
		} catch (IOException e) {
			CCLog.addError(e);
			return null;
		}
	}

	public static BufferedImage deepCopyImage(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static void alphaClearImage(BufferedImage img) {
		byte[] a = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();

		Arrays.fill(a, (byte) 0);
	}

	public static ImageIcon sliceImage(ImageIcon icn, double percStart, double percStop) {
		BufferedImage bi = iconToImage(icn);
		
		sliceImage(bi, percStart, percStop);
		
		return new ImageIcon(bi);
	}
	
	public static ImageIcon sliceImage(ImageIcon icn, int pixelStart, int pixelStop) {
		BufferedImage bi = iconToImage(icn);
		
		sliceImage(bi, pixelStart, pixelStop);
		
		return new ImageIcon(bi);
	}
	
	public static void sliceImage(BufferedImage img, double percStart, double percStop) {
		sliceImage(img, (int)(img.getWidth() * percStart), (int)(img.getWidth() * percStop));
	}
	
	public static void sliceImage(BufferedImage img, int pixelStart, int pixelStop) {
		Graphics2D g = img.createGraphics();
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		
		g.setColor(new Color(0, 0, 0, 0));
		
		if (pixelStart > 0)
			g.fillRect(0, 0, pixelStart, img.getHeight());

		if (pixelStop < img.getWidth())
			g.fillRect(pixelStop, 0, img.getWidth() - pixelStop, img.getHeight());
	}
	
	public static boolean isImageRatioAcceptable(int w, int h) {
		double cropdiff = Math.abs((w * 1d) / (h) - COVER_RATIO);
		
		return cropdiff <= MAX_RATIO_DIFF;
	}

	public static BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor) {
		return getScaledInstance(img, (int) Math.round(img.getWidth() * dScaleFactor), (int) Math.round(img.getHeight() * dScaleFactor));
	}
	
	public static BufferedImage getScaledInstance(BufferedImage img, int iImageWidth, int iImageHeight) {
		BufferedImage imgScale = img;
		
		if (img.getWidth() > iImageWidth || img.getHeight() > iImageHeight) {
			imgScale = getScaledDownInstance(img, iImageWidth, iImageHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
		} else {
			imgScale = getScaledUpInstance(img, iImageWidth, iImageHeight, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
		}

		return imgScale;
	}

	public static BufferedImage getScaledDownInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

		BufferedImage ret = img;

		if (targetHeight > 0 || targetWidth > 0) {
			int w, h;
			if (higherQuality) {
				// Use multi-step technique: start with original size, then
				// scale down in multiple passes with drawImage()
				// until the target size is reached
				w = img.getWidth();
				h = img.getHeight();
			} else {
				// Use one-step technique: scale directly from original
				// size to target size with a single drawImage() call
				w = targetWidth;
				h = targetHeight;
			}

			do {
				if (higherQuality && w > targetWidth) {
					w /= 2;
					if (w < targetWidth) {
						w = targetWidth;
					}
				}
				if (higherQuality && h > targetHeight) {
					h /= 2;
					if (h < targetHeight) {
						h = targetHeight;
					}
				}

				BufferedImage tmp = new BufferedImage(Math.max(w, 1), Math.max(h, 1), type);
				Graphics2D g2 = tmp.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
				g2.drawImage(ret, 0, 0, w, h, null);
				g2.dispose();

				ret = tmp;
			} while (w != targetWidth || h != targetHeight);
		} else {
			ret = new BufferedImage(1, 1, type);
		}

		return ret;
	}

	public static BufferedImage getScaledUpInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
		int type = BufferedImage.TYPE_INT_ARGB;

		BufferedImage ret = img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w < targetWidth) {
				w *= 2;
				if (w > targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h < targetHeight) {
				h *= 2;
				if (h > targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
			tmp = null;
		} while (w != targetWidth || h != targetHeight);
		return ret;
	}

}