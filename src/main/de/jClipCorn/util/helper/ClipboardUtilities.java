package de.jClipCorn.util.helper;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

// Use Clipboard.getData(flavor), never getContents(): getContents() eagerly fetches every format the owner
// offers, and image sources (browsers, GTK apps) offer png/jpeg/bmp/tiff/ico/... which they all encode on request.
public class ClipboardUtilities {
	private static Clipboard systemClip = Toolkit.getDefaultToolkit().getSystemClipboard();

	public static String getString() {
		try {
			return (String) systemClip.getData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException | IOException | IllegalStateException e) {
			return null;
		}
	}

	public static String getURL() {
		String data = getString();
		if (data == null) return null;

		try {
			new URL(data);
		} catch (MalformedURLException e) {
			return null;
		}

		return data;
	}

	public static BufferedImage getImage() {
		Object data;
		try {
			data = systemClip.getData(DataFlavor.imageFlavor);
		} catch (UnsupportedFlavorException | IOException | IllegalStateException e) {
			return null;
		}

		if (data instanceof BufferedImage bi) return bi;
		if (data instanceof Image img) return toBufferedImage(img);
		return null;
	}

	private static BufferedImage toBufferedImage(Image img) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		if (w <= 0 || h <= 0) return null;

		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return bi;
	}

	public static void setString(String cb) {
		systemClip.setContents(new StringSelection(cb), null);
	}
}
