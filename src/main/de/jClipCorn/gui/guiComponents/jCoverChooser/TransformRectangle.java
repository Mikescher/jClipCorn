package de.jClipCorn.gui.guiComponents.jCoverChooser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

abstract class TransformRectangle { // Default visibility
	protected final static int BORDERWIDTH = 1;
	protected final static Color BORDERCOLOR = Color.BLACK;
	protected final static int FOCUSBORDERWIDTH = 2;
	protected final static Color FOCUSBORDERCOLOR = Color.BLUE;
	
	protected Point topLeft = new Point();
	protected Point topRight = new Point();
	protected Point bottomLeft = new Point();
	protected Point bottomRight = new Point();

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
	
	public abstract void transform(final int circRad);

	public abstract void draw(Graphics g, BufferedImage bufferedImage, boolean focused);

	public abstract boolean includesPoint(int x, int y);
}
