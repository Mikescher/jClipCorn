package de.jClipCorn.gui.guiComponents.jCoverChooser;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.ImageUtilities;

public class TransformRectangle2D extends TransformRectangle{
	
	
	public TransformRectangle2D(Point tl, Point br) {
		super(tl, br);
	}

	private BufferedImage transformImg(BufferedImage oi, boolean focused) {
		BufferedImage i = new BufferedImage(oi.getWidth(), oi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		i.getGraphics().drawImage(oi, 0, 0, null);
		
		if (CCProperties.getInstance().PROP_PREVSERIES_COVERBORDER.getValue()) {
			if (focused) {
				ImageUtilities.drawBorder(i, FOCUSBORDERCOLOR, FOCUSBORDERWIDTH);
			} else {
				ImageUtilities.drawBorder(i, BORDERCOLOR, BORDERWIDTH);
			}
		}
		
		return i;
	}

	@Override
	public void draw(Graphics g, BufferedImage i, boolean focused) {
		g.drawImage(transformImg(i, focused), (int)topLeft.getX(), (int)topLeft.getY(), null);
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
