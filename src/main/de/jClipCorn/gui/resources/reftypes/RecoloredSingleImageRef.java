package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.util.helper.ImageUtilities;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public final class RecoloredSingleImageRef extends ImageRef {
	public final ImageRef img;
	public final Color color;

	public RecoloredSingleImageRef(ImageRef inner, boolean preload, long recol) {
		super("image_recolored://[recolor=0x"+Long.toHexString(recol)+"]/{" + inner.ident + "}", preload); //$NON-NLS-1$
		img = inner;
		color = new Color((int)(recol&0xFFFFFF));
	}

	@Override
	public List<ResourceRef> getDirectDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void preload() {
		CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public BufferedImage get() {
		return CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public BufferedImage createImage() {
		BufferedImage bi = ImageUtilities.deepCopyImage(this.img.get());

		var ccol = this.color.getRGB();

		int tr = (ccol >> 16) & 0xFF;
		int tg = (ccol >> 8)  & 0xFF;
		int tb = ccol         & 0xFF;

		for (int x = 0; x < bi.getWidth(); x++)
		{
			for (int y = 0; y < bi.getHeight(); y++)
			{
				int argb = bi.getRGB(x, y);

				int a = (argb >> 24) & 0xFF;

				int r = (argb >> 16) & 0xFF;
				int g = (argb >> 8) & 0xFF;
				int b = argb & 0xFF;
				int gray = (r + g + b) / 3;

				int nr = (tr * (255 - gray) + 255 * gray) / 255;
				int ng = (tg * (255 - gray) + 255 * gray) / 255;
				int nb = (tb * (255 - gray) + 255 * gray) / 255;

				int newArgb = (a << 24) | (nr << 16) | (ng << 8) | nb;
				bi.setRGB(x, y, newArgb);
			}
		}

		return bi;
	}
}
