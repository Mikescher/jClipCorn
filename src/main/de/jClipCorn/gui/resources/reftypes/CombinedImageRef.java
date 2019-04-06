package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.ResourceRefType;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class CombinedImageRef extends ImageRef {
	public final SingleImageRef[] layers;

	public CombinedImageRef(SingleImageRef[] _layers) {
		super("combinedimage://" + CCStreams.iterate(_layers).stringjoin(l -> l.path, "|"), ResourceRefType.IMAGE_COMBINED); //$NON-NLS-1$ //$NON-NLS-2$
		layers = _layers;

		if (layers.length == 0) throw new Error("CombinedIconRef invariant failed"); //$NON-NLS-1$
	}

	@Override
	public void preload() {
		CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public List<ResourceRef> getDirectDependencies() {
		return Arrays.asList(layers);
	}

	@Override
	public BufferedImage get() {
		return CachedResourceLoader.getOrLoad(this);
	}
}
