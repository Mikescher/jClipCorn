package de.jClipCorn.gui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.jClipCorn.Main;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;

public class CachedResourceLoader {
	private static Map<String, BufferedImage> mapImages = new HashMap<>();
	private static Map<String, ImageIcon> mapIcon = new HashMap<>();
	
	public static void preload(String name) {
		getImageIcon(name);
		getSmallImageIcon(name);
	}
	
	public static BufferedImage getImage(String name) {
		try {
			BufferedImage result = mapImages.get(name);
			if (result == null) {
				result = ImageIO.read(Main.class.getResource(name));
				mapImages.put(name, result);
			}
			return result;
		} catch (IOException e) {
			return new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		} catch (IllegalArgumentException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.RessourceNotFound", name)); //$NON-NLS-1$
			return null;
		}
	}
	
	public static ImageIcon getImageIcon(String name) {
		ImageIcon result = mapIcon.get(name);
		if (result == null) {
			result = new ImageIcon(getImage(name));
			mapIcon.put(name, result);
		}
		return result;
	}
	
	public static ImageIcon getSmallImageIcon(String name) {
		String shortFN = name.substring(0, name.lastIndexOf(46)) + "_16x16" + name.substring(name.lastIndexOf(46), name.length()); //$NON-NLS-1$
		//ASCII 46 = '.'
		
		if (Main.class.getResource(shortFN) != null) {
			return getImageIcon(shortFN);
		} else {
			return new ImageIcon(getResizedImage(name, 16, 16));
		}
	}
	
	public static BufferedImage getResizedImage(String name, int w, int h) {
		return resize(getImage(name), w, h);
	}
	
	public static ImageIcon getResizedImageIcon(String name, int w, int h) {
		return new ImageIcon(resize(getImage(name), w, h));
	}
	
	public static BufferedImage resize(BufferedImage bi, int width, int height) {
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
}
