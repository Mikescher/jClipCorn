package de.jClipCorn.gui.resources;

import de.jClipCorn.gui.resources.reftypes.IconRef;

import javax.swing.*;
import java.awt.image.BufferedImage;

public final class MultiSizeIconRef {
	private final IconRef icon16x16;
	private final IconRef icon32x32;
	
	public MultiSizeIconRef(IconRef i16, IconRef i32) {
		icon16x16 = i16;
		icon32x32 = i32;
	}

	public ImageIcon get32x32() {
		return icon32x32.get();
	}

	public ImageIcon get16x16() {
		return icon16x16.get();
	}

	public BufferedImage getImage16x16() {
		return icon16x16.getImage();
	}

	public BufferedImage getImage32x32() {
		return icon32x32.getImage();
	}
}
