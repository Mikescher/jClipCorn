package de.jClipCorn.gui.resources;

import de.jClipCorn.gui.resources.reftypes.SingleIconRef;

import javax.swing.*;
import java.awt.image.BufferedImage;

public final class MultiSizeIconRef {
	private final SingleIconRef icon16x16;
	private final SingleIconRef icon32x32;
	
	public MultiSizeIconRef(SingleIconRef i16, SingleIconRef i32) {
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
		return icon16x16.img.get();
	}

	public BufferedImage getImage32x32() {
		return icon32x32.img.get();
	}
}
