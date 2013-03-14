package de.jClipCorn.gui.guiComponents.jCoverChooser;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

class TransformRectangle { //Default visibility
	private final static double FLEEDEPTH = 2.0;
	
	private Point topLeft = new Point();
	private Point topRight = new Point();
	private Point bottomLeft = new Point();
	private Point bottomRight = new Point();
	
	public TransformRectangle(Point tl, Point br) {
		topLeft.setLocation(tl.getX(), tl.getY());
		bottomRight.setLocation(br.getX(), br.getY());
		topRight.setLocation(br.getX(), tl.getY());
		bottomLeft.setLocation(tl.getX(), br.getY());
	}

	public Point getTopLeft() {
		return topLeft;
	}

	public void setTopLeft(Point topLeft) {
		this.topLeft = topLeft;
	}

	public Point getTopRight() {
		return topRight;
	}

	public void setTopRight(Point topRight) {
		this.topRight = topRight;
	}

	public Point getBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(Point bottomLeft) {
		this.bottomLeft = bottomLeft;
	}

	public Point getBottomRight() {
		return bottomRight;
	}

	public void setBottomRight(Point bottomRight) {
		this.bottomRight = bottomRight;
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
	
	private BufferedImage transformImg(BufferedImage i) {
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

	public void draw(Graphics g, BufferedImage i) {
		g.drawImage(transformImg(i), (int)topLeft.getX(), getTop(), null);
		
//		g.drawLine((int)topLeft.getX(), (int)topLeft.getY(), (int)topRight.getX(), (int)topRight.getY());
//		g.drawLine((int)bottomRight.getX(), (int)bottomRight.getY(), (int)topRight.getX(), (int)topRight.getY());
//		g.drawLine((int)topLeft.getX(), (int)topLeft.getY(), (int)bottomLeft.getX(), (int)bottomLeft.getY());
//		g.drawLine((int)bottomRight.getX(), (int)bottomRight.getY(), (int)bottomLeft.getX(), (int)bottomLeft.getY());
	}
	
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
