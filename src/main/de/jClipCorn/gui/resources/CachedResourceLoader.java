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

	public static BufferedImage getOrLoad(DualMaskedImageRef ref)
	{
		BufferedImage result = mapImages.get(ref.id);
		if (result != null) return result;

		var i1 = ref.image1.getImage();
		var i2 = ref.image2.getImage();
		var m1 = ref.mask1.getImage();
		var m2 = ref.mask2.getImage();

		result = new BufferedImage(i1.getWidth(), i1.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < i1.getWidth(); x++)
		{
			for (int y = 0; y < i1.getHeight(); y++)
			{
				var a1 = (m1.getRGB(x, y) >> 24) & 0xFF;
				var a2 = (m2.getRGB(x, y) >> 24) & 0xFF;

				var c1 = i1.getRGB(x, y);
				var c2 = i2.getRGB(x, y);

				var r1 = ((c1 >> 16) & 0xFF)/255.0;
				var g1 = ((c1 >> 8)  & 0xFF)/255.0;
				var b1 = (c1         & 0xFF)/255.0;

				var r2 = ((c2 >> 16) & 0xFF)/255.0;
				var g2 = ((c2 >> 8)  & 0xFF)/255.0;
				var b2 = (c2         & 0xFF)/255.0;

				var ca1 = (a1 * ((c1 >> 24) & 0xFF))/(255.0*255.0);
				var ca2 = (a2 * ((c2 >> 24) & 0xFF))/(255.0*255.0);
				var ca3 = 1 - (1 - ca1) * (1 - ca2);

				if (ca3 < 1.0e-6)
				{
					result.setRGB(x, y, 0);
				}
				else
				{
					var r3 = r1 * ca1 / ca3 + r2 * ca2 * (1 - ca1) / ca3;
					var g3 = g1 * ca1 / ca3 + g2 * ca2 * (1 - ca1) / ca3;
					var b3 = b1 * ca1 / ca3 + b2 * ca2 * (1 - ca1) / ca3;

					var c3 = ((int)(ca3*255) << 24) | ((int)(r3*255) << 16) | ((int)(g3*255) << 8) | ((int)(b3*255));

					result.setRGB(x, y, c3);
				}
			}
		}

		mapImages.put(ref.id, result);
		return result;
	}

	public static ImageIcon getOrLoad(DualMaskedIconRef ref)
	{
		ImageIcon result = mapIcons.get(ref.id);
		if (result != null) return result;

		result = new ImageIcon(ref.inner_img.get());
		mapIcons.put(ref.id, result);
		return result;
	}
}
