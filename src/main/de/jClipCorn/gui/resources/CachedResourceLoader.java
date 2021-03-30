package de.jClipCorn.gui.resources;

import de.jClipCorn.Main;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.reftypes.*;
import de.jClipCorn.util.helper.ImageUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CachedResourceLoader {
	private static final Map<UUID, BufferedImage> mapImages = new HashMap<>();
	private static final Map<UUID, ImageIcon> mapIcons = new HashMap<>();

	public static BufferedImage getOrLoad(SingleImageRef ref)
	{
		try
		{
			BufferedImage result = mapImages.get(ref.id);
			if (result != null) return result;

			result = ImageIO.read(Main.class.getResource(ref.path));
			mapImages.put(ref.id, result);
			return result;
		}
		catch (IOException | IllegalArgumentException e)
		{
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.RessourceNotFound", ref.path)); //$NON-NLS-1$
			return new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		}
	}

	public static BufferedImage getOrLoad(RecoloredSingleImageRef ref)
	{
		BufferedImage result = mapImages.get(ref.id);
		if (result != null) return result;

		BufferedImage img = ImageUtilities.deepCopyImage(ref.img.get());

		var ccol = ref.color.getRGB();

		Graphics2D g = img.createGraphics();
		g.setComposite(AlphaComposite.Src);
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				Color ca = new Color((img.getRGB(x, y) & 0xFF000000) | (ccol & 0x00FFFFFF), true);
				g.setColor(ca);
				g.fillRect(x, y, 1, 1);
			}
		}
		g.dispose();

		result = img;
		mapImages.put(ref.id, result);
		return result;
	}

	public static ImageIcon getOrLoad(SingleIconRef ref)
	{
		ImageIcon result = mapIcons.get(ref.id);
		if (result != null) return result;

		result = new ImageIcon(ref.img.get());
		mapIcons.put(ref.id, result);
		return result;
	}

	public static BufferedImage getOrLoad(CombinedImageRef ref)
	{
		BufferedImage result = mapImages.get(ref.id);
		if (result != null) return result;

		BufferedImage img = ImageUtilities.deepCopyImage(ref.layers[0].get());

		Graphics g = img.getGraphics();
		for (int i = 1; i < ref.layers.length; i++)
		{
			g.drawImage(ref.layers[i].get(), 0, 0, null);
		}

		result = img;
		mapImages.put(ref.id, result);
		return result;
	}

	public static ImageIcon getOrLoad(CombinedIconRef ref)
	{
		ImageIcon result = mapIcons.get(ref.id);
		if (result != null) return result;

		result = new ImageIcon(ref.inner_img.get());
		mapIcons.put(ref.id, result);
		return result;
	}
}
