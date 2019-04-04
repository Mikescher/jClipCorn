package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.ResourceRefType;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class CombinedIconRef extends IconRef {
	public final CombinedImageRef inner_img;

	public CombinedIconRef(CombinedImageRef _inner) {
		super("combinedicon://" + CCStreams.iterate(_inner.layers).stringjoin(l -> l.path, "|"), ResourceRefType.ICON_OTHER_COMBINED); //$NON-NLS-1$
		inner_img = _inner;
	}

	@Override
	public void preload() {
		inner_img.preload();
		CachedResourceLoader.getOrLoad(this);
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
