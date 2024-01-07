package de.jClipCorn.gui.guiComponents.jCoverChooser;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.ImageUtilities;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TransformRectangle2D extends TransformRectangle{

	private final CCProperties ccprops;

	public TransformRectangle2D(CCProperties ccprops, Point tl, Point br) {
		super(tl, br);
		this.ccprops = ccprops;
	}

	private BufferedImage transformImg(BufferedImage oi, boolean focused) {

		double dw = topRight.getX() - topLeft.getX();
		double dh = bottomLeft.getY() - topLeft.getY();

		int iw = (int)dw;
		int ih = (int)dh;

		BufferedImage rimg = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);

		rimg.getGraphics().drawImage(oi, 0, 0, iw, ih, 0, 0, oi.getWidth(), oi.getHeight(), null);

		if (ccprops.PROP_PREVSERIES_COVERBORDER.getValue()) {
			if (focused) {
				ImageUtilities.drawActualBorder(rimg, FOCUSBORDERCOLOR, FOCUSBORDERWIDTH);
			} else {
				ImageUtilities.drawActualBorder(rimg, BORDERCOLOR, BORDERWIDTH);
			}
		}

		return rimg;
	}

	@Override
	public void draw(Graphics g, BufferedImage i, boolean focused) {
		if (g != null && i != null) {
			g.drawImage(transformImg(i, focused), (int)topLeft.getX(), (int)topLeft.getY(), null);
		}
	}
	
	@Override
	public void transform(final int circRad) {
		//Nothing to do here
	}

	@Override
	public boolean includesPoint(int x, int y) {
		if (x >= topLeft.getX() && x <= bottomRight.getX()) {
			if (y >= topLeft.getY() && y <= bottomRight.getY()) {
				return true;
			}
		}
		return false;
	}
}
