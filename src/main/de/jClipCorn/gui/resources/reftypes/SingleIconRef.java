package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.ResourceRefType;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class SingleIconRef extends IconRef {
	public final SingleImageRef img;

	public SingleIconRef(SingleImageRef iref, ResourceRefType type, boolean preload) {
		super("icon://" + iref.path, type, preload); //$NON-NLS-1$
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

	@Override
	public ImageIcon get() {
		return CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public BufferedImage getImage() {
		return img.get();
	}
}
