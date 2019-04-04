package de.jClipCorn.gui.resources;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.jClipCorn.Main;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.reftypes.*;
import de.jClipCorn.util.helper.ImageUtilities;

public class CachedResourceLoader {
	private static Map<UUID, BufferedImage> mapImages = new HashMap<>();
	private static Map<UUID, ImageIcon> mapIcons = new HashMap<>();

	public static BufferedImage getOrLoad(SingleImageRef ref)
	{
		try
		{
			BufferedImage result = mapImages.get(ref.id);
			if (result == null)
			{
				result = ImageIO.read(Main.class.getResource(ref.path));
				mapImages.put(ref.id, result);
			}
			return result;
		}
		catch (IOException | IllegalArgumentException e)
		{
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.RessourceNotFound", ref.path)); //$NON-NLS-1$
			return new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		}
	}

	public static ImageIcon getOrLoad(SingleIconRef ref)
	{
		ImageIcon result = mapIcons.get(ref.id);
		if (result == null)
		{
			result = new ImageIcon(getOrLoad(ref.img));
			mapIcons.put(ref.id, result);
		}
		return result;
	}

	public static BufferedImage getOrLoad(CombinedImageRef ref)
	{
		BufferedImage result = mapImages.get(ref.id);
		if (result == null)
		{
			BufferedImage img = getOrLoad(ref.layers[0]);

			Graphics g = img.getGraphics();
			for (int i = 1; i < ref.layers.length; i++)
			{
				g.drawImage(getOrLoad(ref.layers[i]), 0, 0, null);
			}

			result = img;
			mapImages.put(ref.id, result);
		}
		return result;
	}

	public static ImageIcon getOrLoad(CombinedIconRef ref)
	{
		ImageIcon result = mapIcons.get(ref.id);
		if (result == null)
		{
			result = new ImageIcon(getOrLoad(ref.inner_img));
			mapIcons.put(ref.id, result);
		}
		return result;
	}
}
