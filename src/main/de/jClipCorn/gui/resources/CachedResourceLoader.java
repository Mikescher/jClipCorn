package de.jClipCorn.gui.resources;

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
	private static Map<ImageRef, BufferedImage> mapImages1 = new HashMap<>();
	private static Map<ImageRef, ImageIcon> mapImages2 = new HashMap<>();
	
	private static Map<IconRef, BufferedImage> mapIcon1 = new HashMap<>();
	private static Map<IconRef, ImageIcon> mapIcon2 = new HashMap<>();
	
	public static void preload(IconRef ref) {
		getIcon(ref);
	}

	public static void preload(ImageRef ref) {
		getIcon(ref);
	}
	
	public static BufferedImage getImage(ImageRef name) {
		try {
			BufferedImage result = mapImages1.get(name);
			if (result == null) {
				result = ImageIO.read(Main.class.getResource(name.path));
				mapImages1.put(name, result);
			}
			return result;
		} catch (IOException | IllegalArgumentException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.RessourceNotFound", name.path)); //$NON-NLS-1$
			return new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		}
	}
	
	public static ImageIcon getIcon(ImageRef name) {
		ImageIcon result = mapImages2.get(name);
		if (result == null) {
			result = new ImageIcon(getImage(name));
			mapImages2.put(name, result);
		}
		return result;
	}
	
	public static BufferedImage getImage(IconRef name) {
		try {
			BufferedImage result = mapIcon1.get(name);
			if (result == null) {
				result = ImageIO.read(Main.class.getResource(name.path));
				mapIcon1.put(name, result);
			}
			return result;
		} catch (IOException | IllegalArgumentException e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.RessourceNotFound", name.path)); //$NON-NLS-1$
			return new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		}
	}
	
	public static ImageIcon getIcon(IconRef name) {
		ImageIcon result = mapIcon2.get(name);
		if (result == null) {
			result = new ImageIcon(getImage(name));
			mapIcon2.put(name, result);
		}
		return result;
	}
}
