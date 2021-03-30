package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.ResourceRefType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public final class RecoloredSingleImageRef extends ImageRef {
	public final ImageRef img;
	public final Color color;

	public RecoloredSingleImageRef(ImageRef inner, boolean preload, long recol) {
		super("image_recolored://[recolor=0x"+Long.toHexString(recol)+"]/{" + inner.ident + "}", ResourceRefType.IMAGE_RECOLORED, preload); //$NON-NLS-1$
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
}
