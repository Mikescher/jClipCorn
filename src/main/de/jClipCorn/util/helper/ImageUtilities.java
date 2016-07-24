package de.jClipCorn.util.helper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.log.CCLog;

public class ImageUtilities {
	public final static int COVER_WIDTH  = 182;
	public final static int COVER_HEIGHT = 254;
	public final static double COVER_RATIO = COVER_WIDTH / (COVER_HEIGHT * 1d);
	
	private final static int SERIES_MASK_WIDTH  = 56;
	private final static int SERIES_MASK_HEIGHT = 56;
	
	public static BufferedImage resizeCoverImage(BufferedImage bi) {
		return resizeImageProportional(bi, COVER_WIDTH, COVER_HEIGHT, COVER_RATIO);
	}
	
	public static BufferedImage resizeHalfCoverImage(BufferedImage bi) {
		return resizeImageProportional(bi, COVER_WIDTH / 2, COVER_HEIGHT / 2, COVER_RATIO);
	}
	
	public static boolean isCorrectlySized(BufferedImage bb) {
		return isCorrectlySized(bb, COVER_WIDTH, COVER_HEIGHT);
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
    
	private static boolean isCorrectlySized(BufferedImage bb, int prefWidth, int prefHeight) {
		return (bb != null) && (bb.getWidth() == prefWidth) && (bb.getHeight() == prefHeight);
	}
	
	public static void makeSeriesCover(BufferedImage cvr) {
		Point tl = getTopLeftNonTransparentPixel(cvr);
		tl.setLocation(Math.min(tl.x, COVER_WIDTH - SERIES_MASK_WIDTH), Math.min(tl.y, COVER_HEIGHT - SERIES_MASK_HEIGHT)); // Nicht zu weit rechts | Nicht zu weit unten
		
		cvr.getGraphics().drawImage(CachedResourceLoader.getImage(Resources.IMG_COVER_SERIES_MASK), tl.x, tl.y, null);
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
		
		for (int i = 0; i < a.length; i++) {
			a[i] = 0;
		}
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
}