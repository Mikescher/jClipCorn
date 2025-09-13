package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class CombinedImageRef extends ImageRef {
	public final ImageRef[] layers;

	public CombinedImageRef(ImageRef[] innerlayers, boolean preload) {
		super("combinedimage://" + CCStreams.iterate(innerlayers).stringjoin(l -> "{"+l.ident+"}", "|"), preload); //$NON-NLS-1$ //$NON-NLS-2$
		layers = innerlayers;

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

	@Override
	public BufferedImage createImage() {
		BufferedImage img = ImageUtilities.deepCopyImage(this.layers[0].get());

		Graphics g = img.getGraphics();
		for (int i = 1; i < this.layers.length; i++)
		{
			g.drawImage(this.layers[i].get(), 0, 0, null);
		}

		return img;
	}
}
