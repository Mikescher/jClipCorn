package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.CachedResourceLoader;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

public class DualMaskedImageRef extends ImageRef {
	public final ResourceRef mask1;
	public final ResourceRef mask2;
	public final ResourceRef image1;
	public final ResourceRef image2;

	public DualMaskedImageRef(ResourceRef m1, ResourceRef m2, ResourceRef i1, ResourceRef i2, boolean preload) {
		super("dualmaskedimage://"+m1.ident+"|"+m2.ident+"|"+i1.ident+"|"+i2.ident+"|", preload); //$NON-NLS-1$ //$NON-NLS-2$
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

	@Override
	public BufferedImage createImage() {
		var i1 = this.image1.getImage();
		var i2 = this.image2.getImage();
		var m1 = this.mask1.getImage();
		var m2 = this.mask2.getImage();

		var result = new BufferedImage(i1.getWidth(), i1.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < i1.getWidth(); x++)
		{
			for (int y = 0; y < i1.getHeight(); y++)
			{
				var a1 = (m1.getRGB(x, y) >> 24) & 0xFF;
				var a2 = (m2.getRGB(x, y) >> 24) & 0xFF;

				var c1 = i1.getRGB(x, y);
				var c2 = i2.getRGB(x, y);

				var r1 = ((c1 >> 16) & 0xFF)/255.0;
				var g1 = ((c1 >> 8)  & 0xFF)/255.0;
				var b1 = (c1         & 0xFF)/255.0;

				var r2 = ((c2 >> 16) & 0xFF)/255.0;
				var g2 = ((c2 >> 8)  & 0xFF)/255.0;
				var b2 = (c2         & 0xFF)/255.0;

				var ca1 = (a1 * ((c1 >> 24) & 0xFF))/(255.0*255.0);
				var ca2 = (a2 * ((c2 >> 24) & 0xFF))/(255.0*255.0);
				var ca3 = 1 - (1 - ca1) * (1 - ca2);

				if (ca3 < 1.0e-6)
				{
					result.setRGB(x, y, 0);
				}
				else
				{
					var r3 = r1 * ca1 / ca3 + r2 * ca2 * (1 - ca1) / ca3;
					var g3 = g1 * ca1 / ca3 + g2 * ca2 * (1 - ca1) / ca3;
					var b3 = b1 * ca1 / ca3 + b2 * ca2 * (1 - ca1) / ca3;

					var c3 = ((int)(ca3*255) << 24) | ((int)(r3*255) << 16) | ((int)(g3*255) << 8) | ((int)(b3*255));

					result.setRGB(x, y, c3);
				}
			}
		}

		return result;
	}
}
