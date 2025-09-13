package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class HeightScaledImageRef extends ImageRef {
	public final ImageRef base;
	public final int targetHeight;

	public HeightScaledImageRef(ImageRef base, int targetHeight, boolean preload) {
		super("heightscaledimage://{" + base.ident+"|"+targetHeight+"}", preload); //$NON-NLS-1$ //$NON-NLS-2$
		this.base = base;
		this.targetHeight = targetHeight;
	}

	@Override
	public void preload() {
		CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public List<ResourceRef> getDirectDependencies() {
		return Collections.singletonList(base);
	}

	@Override
	public BufferedImage get() {
		return CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public BufferedImage createImage() {
		BufferedImage orig = this.base.getImage();

		int w = (orig.getWidth() * this.targetHeight) / orig.getHeight();

		Image scaled = orig.getScaledInstance(w, this.targetHeight, Image.SCALE_SMOOTH);
		BufferedImage scaledBI = new BufferedImage(w, this.targetHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = scaledBI.createGraphics();
		g2d.drawImage(scaled, 0, 0, null);
		g2d.dispose();

		return scaledBI;
	}
}
