package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.ResourceRefType;

import java.awt.image.BufferedImage;

public abstract class ImageRef extends ResourceRef {
	public ImageRef(String id, ResourceRefType type, boolean preload) {
		super(id, type, preload);
	}

	public abstract BufferedImage get();

	public BufferedImage getImage() { return get(); }
}
