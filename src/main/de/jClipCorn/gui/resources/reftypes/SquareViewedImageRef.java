package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class SquareViewedImageRef extends ImageRef {
	public final ImageRef base;
	public final int targetHeight;

	public SquareViewedImageRef(ImageRef base, int targetHeight, boolean preload) {
		super("squareviewedimage://{" + base.ident+"|"+targetHeight+"}", preload); //$NON-NLS-1$ //$NON-NLS-2$
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
		int baseWidth = orig.getWidth();
		int baseHeight = orig.getHeight();

		// Take 27/34 (= 79.41%) of the width (from left)
		// then scale to target height
		// then put it, centered, on a square canvas of target height + target-width

		int croppedWidth = baseWidth * 27 / 34;
		int croppedHeight = baseHeight;
		BufferedImage cropped = orig.getSubimage(0, 0, croppedWidth, croppedHeight);

		int targetSize = this.targetHeight;
		double scaleX = (double) targetSize / croppedWidth;
		double scaleY = (double) targetSize / croppedHeight;
		double scale = Math.min(scaleX, scaleY);
		int scaledWidth = (int) Math.round(croppedWidth * scale);
		int scaledHeight = (int) Math.round(croppedHeight * scale);

		Image scaled = cropped.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
		BufferedImage scaledBI = new BufferedImage(scaledWidth, targetSize, BufferedImage.TYPE_INT_ARGB);
		var g2dScaled = scaledBI.createGraphics();
		g2dScaled.drawImage(scaled, 0, 0, null);
		g2dScaled.dispose();

		var squared = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
		var g2dSquared = squared.createGraphics();
		int sqx = (targetSize - scaledWidth) / 2;
		int sqy = (targetSize - scaledHeight) / 2;
		g2dSquared.drawImage(scaledBI, sqx, sqy, null);
		g2dSquared.dispose();

		return squared;
	}
}
