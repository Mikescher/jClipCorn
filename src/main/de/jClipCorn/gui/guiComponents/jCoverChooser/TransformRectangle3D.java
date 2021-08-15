package de.jClipCorn.gui.guiComponents.jCoverChooser;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.ImageUtilities;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TransformRectangle3D extends TransformRectangle{
	private final static double FLEEDEPTH = 2.0;

	private final CCProperties ccprops;

	public TransformRectangle3D(CCProperties ccprops, Point tl, Point br) {
		super(tl, br);
		this.ccprops = ccprops;
	}
	
	private int getTopAt(double d) {
		double dw = bottomRight.getX() - topLeft.getX();
		return (int) (topLeft.getY() + (topRight.getY() - topLeft.getY() * 1d) * d / (dw * 1d));
	}
	
	private int getBottomAt(double d) {
		double dw = Math.abs(bottomRight.getX() - topLeft.getX());
		return (int) (bottomLeft.getY() + (bottomRight.getY() - bottomLeft.getY() * 1d) * d / (dw * 1d));
	}
	
	private int getHeightAt(int x) {
		double dw =Math.abs(topRight.getX() - topLeft.getX());
		
		double ytop = topLeft.getY() + (topRight.getY() - topLeft.getY() * 1d) * x / (dw * 1d);
		
		double ybot = bottomLeft.getY() + (bottomRight.getY() - bottomLeft.getY() * 1d) * x / (dw * 1d);
		
		return (int) (ybot - ytop);
	}
	
	private BufferedImage transformImg(BufferedImage oi, boolean focused) {
		BufferedImage i = new BufferedImage(oi.getWidth(), oi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		i.getGraphics().drawImage(oi, 0, 0, null);
		
		if (ccprops.PROP_PREVSERIES_COVERBORDER.getValue()) {
			if (focused) {
				ImageUtilities.drawActualBorder(i, FOCUSBORDERCOLOR, FOCUSBORDERWIDTH);
			} else {
				ImageUtilities.drawActualBorder(i, BORDERCOLOR, BORDERWIDTH);
			}
		}
		
		double dw = Math.abs(topRight.getX() - topLeft.getX());
		double dh = Math.max(bottomLeft.getY() - topLeft.getY(), bottomRight.getY() - topRight.getY());
		
		int iw = (int)dw;
		int ih = (int)dh;
		
		BufferedImage transHorz = new BufferedImage(iw, i.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		transHorz.getGraphics().drawImage(i, 0, 0, iw, i.getHeight(), 0, 0, i.getWidth(), i.getHeight(), null);
		
		BufferedImage rimg = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
		
		Graphics graphics = rimg.getGraphics();
//		g.fillRect(0, 0, w, h);
		for (int j = 0; j < iw; j++) {
			int sh = getHeightAt(j);
			int y1 = (ih - sh) / 2;
			int y2 = y1 + sh;
			graphics.drawImage(transHorz, j, y1, j + 1, y2, j, 0, j + 1, i.getHeight(), null);
		}
		
		return rimg;
	}
	
	private int getTop() {
		return (int) Math.min(topLeft.getY(), topRight.getY());
	}

	@Override
	public void draw(Graphics g, BufferedImage i, boolean focused) {
		if (g != null && i != null) {
			g.drawImage(transformImg(i, focused), (int)topLeft.getX(), getTop(), null);
		}
	}
	
	@Override
	public void transform(final int circRad) {
		transformPoint(circRad, topLeft);
		transformPoint(circRad, topRight);
		transformPoint(circRad, bottomLeft);
		transformPoint(circRad, bottomRight);
	}
	
	private void transformPoint(final int circRad, Point p) {
		double alpha = p.getX() / (circRad);
		double newx = Math.sin(alpha) * circRad;
		
		double depth = circRad - Math.cos(alpha) * circRad;
		
		double newy = p.getY() - p.getY() * ((depth) / (FLEEDEPTH * circRad));
		
		p.setLocation(newx, newy);
	}

	@Override
	public boolean includesPoint(int x, int y) {
		if (x >= topLeft.getX() && x <= topRight.getX()) {
			int y1 = getTopAt(x - topLeft.getX());
			int y2 = getBottomAt(x - bottomLeft.getX());
			
			if (y >= y1 && y <= y2) {
				return true;
			}
		}
		return false;
	}
}
