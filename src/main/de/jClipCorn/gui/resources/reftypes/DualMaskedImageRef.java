package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.ResourceRefType;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class DualMaskedImageRef extends ImageRef {
	public final ResourceRef mask1;
	public final ResourceRef mask2;
	public final ResourceRef image1;
	public final ResourceRef image2;

	public DualMaskedImageRef(ResourceRef m1, ResourceRef m2, ResourceRef i1, ResourceRef i2, boolean preload) {
		super("dualmaskedimage://"+m1.ident+"|"+m2.ident+"|"+i1.ident+"|"+i2.ident+"|", ResourceRefType.IMAGE_DUALMASKED, preload); //$NON-NLS-1$ //$NON-NLS-2$
		mask1 = m1;
		mask2 = m2;
		image1 = i1;
		image2 = i2;
	}

	@Override
	public void preload() {
		CachedResourceLoader.getOrLoad(this);
	}

	@Override
	public List<ResourceRef> getDirectDependencies() {
		return Arrays.asList(mask1, mask2, image1, image2);
	}

	@Override
	public BufferedImage get() {
		return CachedResourceLoader.getOrLoad(this);
	}
}
