package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.ResourceRefType;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class DualMaskedIconRef extends IconRef {
	public final DualMaskedImageRef inner_img;

	public DualMaskedIconRef(DualMaskedImageRef inner, boolean preload) {
		super("dualmaskedicon://{" + inner.ident + "}", ResourceRefType.ICON_OTHER_DUALMASKED, preload); //$NON-NLS-1$ //$NON-NLS-2$
		inner_img = inner;
	}

	@Override
	public void preload() {
		inner_img.preload();
		CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public List<ResourceRef> getDirectDependencies() {
		return Collections.singletonList(inner_img);
	}

	@Override
	public ImageIcon get() {
		return CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public BufferedImage getImage() {
		return inner_img.get();
	}
}
