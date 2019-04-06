package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.ResourceRefType;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public final class SingleImageRef extends ImageRef {
	public final String path;

	public SingleImageRef(String _path) {
		super("image://" + _path, ResourceRefType.IMAGE); //$NON-NLS-1$
		path = _path;
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
