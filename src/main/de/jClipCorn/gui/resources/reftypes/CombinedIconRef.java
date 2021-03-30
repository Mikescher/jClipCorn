package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.ResourceRefType;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class CombinedIconRef extends IconRef {
	public final CombinedImageRef inner_img;

	public CombinedIconRef(CombinedImageRef inner, boolean preload) {
		super("combinedicon://{" + inner.ident + "}", ResourceRefType.ICON_OTHER_COMBINED, preload); //$NON-NLS-1$ //$NON-NLS-2$
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
