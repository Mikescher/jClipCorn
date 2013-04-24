package de.jClipCorn.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

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
	
	private static Point getTopLeftNonTransparentPixel(BufferedImage i) {
		for (int x = 0; x < i.getWidth(); x++) {
			for (int y = 0; y < i.getHeight(); y++) {
				if ((i.getRGB(x, y) >> 24) != 0) {
					return new Point(x, y);
				}
			}
		}
		return new Point(i.getWidth() - 1, i.getHeight() - 1);
	}
	
	public static void drawBorder(BufferedImage i, Color c, int thick) {
		Graphics ig = i.getGraphics();
		ig.setColor(c);
		ig.fillRect(0, 0, thick, i.getHeight());
		ig.fillRect(0, 0, i.getWidth(), thick);
		ig.fillRect(i.getWidth() - thick, 0, thick, i.getHeight());
		ig.fillRect(0, i.getHeight() - thick, i.getWidth(), thick);
	}
	
	public static BufferedImage iconToImage(ImageIcon ic) {
		BufferedImage bi = new BufferedImage(ic.getIconWidth(), ic.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		bi.getGraphics().drawImage(ic.getImage(), 0, 0, null);
		return bi;
	}
	
	public static byte[] imageToByteArray(BufferedImage bi) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder( baos );
		try {
			encoder.encode(bi);
		} catch (ImageFormatException | IOException e) {
			CCLog.addError(e);
			return null;
		}
		
		return baos.toByteArray();
	}
	
	public static BufferedImage byteArrayToImage(byte[] imgarr) {
		try {
			return ImageIO.read(new ByteArrayInputStream(imgarr));
		} catch (IOException e) {
			CCLog.addError(e);
			return null;
		}
	}
}
