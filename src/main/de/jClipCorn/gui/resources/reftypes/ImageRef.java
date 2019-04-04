package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.ResourceRefType;

import java.awt.image.BufferedImage;

public abstract class ImageRef extends ResourceRef {
	public ImageRef(String id, ResourceRefType _type) {
		super(id, _type);
	}

	public abstract BufferedImage get();
}
