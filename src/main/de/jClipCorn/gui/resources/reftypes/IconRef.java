package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.ResourceRefType;

import javax.swing.*;
import java.awt.image.BufferedImage;

public abstract class IconRef extends ResourceRef {
	public IconRef(String id, ResourceRefType _type) {
		super(id, _type);
	}

	public abstract ImageIcon get();

	public abstract BufferedImage getImage();
}
