package de.jClipCorn.gui.resources.reftypes;

import java.awt.image.BufferedImage;

public abstract class ImageRef extends ResourceRef {
	protected ImageRef(String id, boolean preload) {
		super(id, ResourceCategory.IMAGE, preload);
	}

	public abstract BufferedImage get();

	public abstract BufferedImage createImage();

	public BufferedImage getImage() { return get(); }

}
