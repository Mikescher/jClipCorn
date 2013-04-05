package de.jClipCorn.gui.guiComponents.jCoverChooser;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.ImageUtilities;

public class TransformRectangle3D extends TransformRectangle{
	private final static double FLEEDEPTH = 2.0;
		
	public TransformRectangle3D(Point tl, Point br) {
		super(tl, br);
	}
	
	private int getTopAt(double d) {
		double dw = bottomRight.getX() - topLeft.getX();
		return (int) (topLeft.getY() + (topRight.getY() - topLeft.getY() * 1d) * d/(dw*1d));
	}
	
	private int getBottomAt(double d) {
		double dw = Math.abs(bottomRight.getX() - topLeft.getX());
		return (int) (bottomLeft.getY() + (bottomRight.getY() - bottomLeft.getY() * 1d) * d/(dw*1d));
	}
	
	private int getHeightAt(int x) {
		double dw =Math.abs(topRight.getX() - topLeft.getX());
		
		double ytop = topLeft.getY() + (topRight.getY() - topLeft.getY() * 1d) * x/(dw*1d);
		
		double ybot = bottomLeft.getY() + (bottomRight.getY() - bottomLeft.getY() * 1d) * x/(dw*1d);
		
		return (int) (ybot - ytop);
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
		
		double dw = Math.abs(topRight.getX() - topLeft.getX());
		double dh = Math.max(bottomLeft.getY() - topLeft.getY(), bottomRight.getY() - topRight.getY());
		
		int w = (int)dw;
		int h = (int)dh;
		
		BufferedImage transHorz = new BufferedImage(w, i.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		transHorz.getGraphics().drawImage(i, 0, 0, w, i.getHeight(), 0, 0, i.getWidth(), i.getHeight(), null);
		
		BufferedImage r = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = r.getGraphics();
//		g.fillRect(0, 0, w, h);
		for (int j = 0; j < w; j++) {
			int sh = getHeightAt(j);
			int y1 = (h - sh)/2;
			int y2 = y1 + sh;
			g.drawImage(transHorz, j, y1, j+1, y2, j, 0, j+1, i.getHeight(), null);
		}
		
		return r;
	}
	
	private int getTop() {
		return (int) Math.min(topLeft.getY(), topRight.getY());
	}

	@Override
	public void draw(Graphics g, BufferedImage i, boolean focused) {
		g.drawImage(transformImg(i, focused), (int)topLeft.getX(), getTop(), null);
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
			int y1 = getTopAt(x-topLeft.getX());
			int y2 = getBottomAt(x-bottomLeft.getX());
			
			if (y >= y1 && y <= y2) {
				return true;
			}
		}
		return false;
	}
}
