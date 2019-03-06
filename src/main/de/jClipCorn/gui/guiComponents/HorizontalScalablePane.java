package de.jClipCorn.gui.guiComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/*
 * https://stackoverflow.com/a/14553003/1761622
 */
public class HorizontalScalablePane extends JPanel {
	private static final long serialVersionUID = 9098817564122730236L;

	private BufferedImage master;

	public HorizontalScalablePane(BufferedImage master) {
		this.master = master;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1, calcHeight());
	}

	public void setImage(BufferedImage master) {
		this.master = master;

		invalidate();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		//g.setColor(Color.MAGENTA);
		//g.fillRect(0, 0, getWidth(), getHeight());
		//g.setColor(Color.BLUE);
		//g.fillRect(5, 5, getWidth()-10, getHeight()-10);
		//g.setColor(Color.WHITE);

		g.drawImage(master, 0, 0, getWidth(), calcHeight(), null);

		if (getHeight() != calcHeight()) invalidate();
	}

	private int calcHeight() {
		return (int)(getWidth() * ((master.getHeight() * 1d) / master.getWidth()));
	}

	@Override
	public void invalidate() {
		super.invalidate();
	}
}
