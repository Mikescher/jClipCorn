package de.jClipCorn.gui.guiComponents.jCoverChooser;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import de.jClipCorn.properties.CCProperties;

public class TransformRectangle2D extends TransformRectangle{
	
	
	public TransformRectangle2D(Point tl, Point br) {
		super(tl, br);
	}

	private BufferedImage transformImg(BufferedImage oi, boolean focused) {
		BufferedImage i = new BufferedImage(oi.getWidth(), oi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		i.getGraphics().drawImage(oi, 0, 0, null);
		
		if (CCProperties.getInstance().PROP_PREVSERIES_COVERBORDER.getValue()) {
			if (focused) {
				Graphics ig = i.getGraphics();
				ig.setColor(FOCUSBORDERCOLOR);
				ig.fillRect(0, 0, FOCUSBORDERWIDTH, i.getHeight());
				ig.fillRect(0, 0, i.getWidth(), FOCUSBORDERWIDTH);
				ig.fillRect(i.getWidth() - FOCUSBORDERWIDTH, 0, FOCUSBORDERWIDTH, i.getHeight());
				ig.fillRect(0, i.getHeight() - FOCUSBORDERWIDTH, i.getWidth(), FOCUSBORDERWIDTH);
			} else {
				Graphics ig = i.getGraphics();
				ig.setColor(BORDERCOLOR);
				ig.fillRect(0, 0, BORDERWIDTH, i.getHeight());
				ig.fillRect(0, 0, i.getWidth(), BORDERWIDTH);
				ig.fillRect(i.getWidth() - BORDERWIDTH, 0, BORDERWIDTH, i.getHeight());
				ig.fillRect(0, i.getHeight() - BORDERWIDTH, i.getWidth(), BORDERWIDTH);
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
