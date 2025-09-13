package de.jClipCorn.gui.resources;

import de.jClipCorn.gui.resources.reftypes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CachedResourceLoader {
	private static final Map<UUID, BufferedImage> mapImages = new HashMap<>();
	private static final Map<UUID, ImageIcon> mapIcons = new HashMap<>();

	public static BufferedImage getOrLoad(ImageRef ref)
	{
		BufferedImage result = mapImages.get(ref.id);
		if (result != null) return result;

		result = ref.createImage();

		mapImages.put(ref.id, result);

		return result;
	}

	public static ImageIcon getOrLoad(IconRef ref)
	{
		ImageIcon result = mapIcons.get(ref.id);
		if (result != null) return result;

		result = new ImageIcon(ref.img.get());
		mapIcons.put(ref.id, result);
		return result;
	}

	public static int getIconCacheCount() {
		return mapIcons.size();
	}

	public static int getImageCacheCount() {
		return mapImages.size();
	}
}
