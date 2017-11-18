package de.jClipCorn.gui.guiComponents;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/*
 * https://stackoverflow.com/a/14553003/1761622
 */
public class ScalablePane extends JPanel {
	private static final long serialVersionUID = 9098817564122720236L;

	private Image master;
	private boolean toFit;
	private Image scaled;

	public ScalablePane(Image master) {
		this(master, true);
	}

	public ScalablePane(Image master, boolean toFit) {
		this.master = master;
		setToFit(toFit);
	}

	@Override
	public Dimension getPreferredSize() {
		return master == null ? super.getPreferredSize() : new Dimension(master.getWidth(this), master.getHeight(this));
	}

	public void setImage(Image master) {
		this.master = master;
		this.scaled = null;
		
		invalidate();
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image toDraw = null;
		if (scaled != null) {
			toDraw = scaled;
		} else if (master != null) {
			toDraw = master;
		}

		if (toDraw != null) {
			int x = (getWidth() - toDraw.getWidth(this)) / 2;
			int y = (getHeight() - toDraw.getHeight(this)) / 2;
			g.drawImage(toDraw, x, y, this);
		}
	}

	@Override
	public void invalidate() {
		generateScaledInstance();
		super.invalidate();
	}

	public boolean isToFit() {
		return toFit;
	}

	public void setToFit(boolean value) {
		if (value != toFit) {
			toFit = value;
			invalidate();
		}
	}

	protected void generateScaledInstance() {
		scaled = null;
		if (isToFit()) {
			scaled = getScaledInstanceToFit(master, getSize());
		} else {
			scaled = getScaledInstanceToFill(master, getSize());
		}
	}

	protected BufferedImage toBufferedImage(Image master) {
		Dimension masterSize = new Dimension(master.getWidth(this), master.getHeight(this));
		BufferedImage image = createCompatibleImage(masterSize);
		Graphics2D g2d = image.createGraphics();
		g2d.drawImage(master, 0, 0, this);
		g2d.dispose();
		return image;
	}

	public Image getScaledInstanceToFit(Image master, Dimension size) {
		Dimension masterSize = new Dimension(master.getWidth(this), master.getHeight(this));
		return getScaledInstance(toBufferedImage(master), getScaleFactorToFit(masterSize, size), RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
	}

	public Image getScaledInstanceToFill(Image master, Dimension size) {
		Dimension masterSize = new Dimension(master.getWidth(this), master.getHeight(this));
		return getScaledInstance(toBufferedImage(master), getScaleFactorToFill(masterSize, size), RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
	}

	public Dimension getSizeToFit(Dimension original, Dimension toFit) {
		double factor = getScaleFactorToFit(original, toFit);
		Dimension size = new Dimension(original);
		size.width *= factor;
		size.height *= factor;
		return size;
	}

	public Dimension getSizeToFill(Dimension original, Dimension toFit) {
		double factor = getScaleFactorToFill(original, toFit);
		Dimension size = new Dimension(original);
		size.width *= factor;
		size.height *= factor;
		return size;
	}

	public double getScaleFactor(int iMasterSize, int iTargetSize) {
		return (double) iTargetSize / (double) iMasterSize;
	}

	public double getScaleFactorToFit(Dimension original, Dimension toFit) {
		double dScale = 1d;
		if (original != null && toFit != null) {
			double dScaleWidth = getScaleFactor(original.width, toFit.width);
			double dScaleHeight = getScaleFactor(original.height, toFit.height);
			dScale = Math.min(dScaleHeight, dScaleWidth);
		}
		return dScale;
	}

	public double getScaleFactorToFill(Dimension masterSize, Dimension targetSize) {
		double dScaleWidth = getScaleFactor(masterSize.width, targetSize.width);
		double dScaleHeight = getScaleFactor(masterSize.height, targetSize.height);

		return Math.max(dScaleHeight, dScaleWidth);
	}

	public BufferedImage createCompatibleImage(Dimension size) {
		return createCompatibleImage(size.width, size.height);
	}

	public BufferedImage createCompatibleImage(int width, int height) {
		GraphicsConfiguration gc = getGraphicsConfiguration();
		if (gc == null) {
			gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		}

		BufferedImage image = gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		image.coerceData(true);
		return image;
	}

	protected BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor, Object hint, boolean bHighQuality) {
		BufferedImage imgScale = img;
		int iImageWidth = (int) Math.round(img.getWidth() * dScaleFactor);
		int iImageHeight = (int) Math.round(img.getHeight() * dScaleFactor);

		if (dScaleFactor <= 1.0d) {
			imgScale = getScaledDownInstance(img, iImageWidth, iImageHeight, hint, bHighQuality);
		} else {
			imgScale = getScaledUpInstance(img, iImageWidth, iImageHeight, hint, bHighQuality);
		}

		return imgScale;
	}

	protected BufferedImage getScaledDownInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {

		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

		BufferedImage ret = img;

		if (targetHeight > 0 || targetWidth > 0) {
			int w, h;
			if (higherQuality) {
				// Use multi-step technique: start with original size, then
				// scale down in multiple passes with drawImage()
				// until the target size is reached
				w = img.getWidth();
				h = img.getHeight();
			} else {
				// Use one-step technique: scale directly from original
				// size to target size with a single drawImage() call
				w = targetWidth;
				h = targetHeight;
			}

			do {
				if (higherQuality && w > targetWidth) {
					w /= 2;
					if (w < targetWidth) {
						w = targetWidth;
					}
				}
				if (higherQuality && h > targetHeight) {
					h /= 2;
					if (h < targetHeight) {
						h = targetHeight;
					}
				}

				BufferedImage tmp = new BufferedImage(Math.max(w, 1), Math.max(h, 1), type);
				Graphics2D g2 = tmp.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
				g2.drawImage(ret, 0, 0, w, h, null);
				g2.dispose();

				ret = tmp;
			} while (w != targetWidth || h != targetHeight);
		} else {
			ret = new BufferedImage(1, 1, type);
		}

		return ret;
	}

	protected BufferedImage getScaledUpInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {

		int type = BufferedImage.TYPE_INT_ARGB;

		BufferedImage ret = img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w < targetWidth) {
				w *= 2;
				if (w > targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h < targetHeight) {
				h *= 2;
				if (h > targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
			tmp = null;
		} while (w != targetWidth || h != targetHeight);
		return ret;
	}
}
