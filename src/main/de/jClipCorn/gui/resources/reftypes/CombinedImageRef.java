package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.ResourceRefType;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class CombinedImageRef extends ImageRef {
	public final SingleImageRef[] layers;

	public CombinedImageRef(SingleImageRef[] _layers) {
		super("combinedimage://" + CCStreams.iterate(_layers).stringjoin(l -> l.path, "|"), ResourceRefType.IMAGE_COMBINED); //$NON-NLS-1$
		layers = _layers;

		if (layers.length == 0) throw new Error("CombinedIconRef invariant failed");
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
