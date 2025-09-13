package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class IconRef extends ResourceRef {
	public final ImageRef img;

	public IconRef(ImageRef iref, boolean preload) {
		super("icon://{" + iref.ident + "}", ResourceCategory.ICON, preload); //$NON-NLS-1$
		img = iref;
	}

	@Override
	public void preload() {
		img.preload();
		CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public List<ResourceRef> getDirectDependencies() {
		return Collections.singletonList(img);
	}

	public ImageIcon get() {
		return CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public BufferedImage getImage() {
		return img.get();
	}
}
