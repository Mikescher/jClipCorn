package de.jClipCorn.gui.guiComponents;

import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class SimpleDoubleBufferStrategy {
	private JComponent component;
	private int w;
	private int h;
	private boolean state = true;
	
	private BufferedImage img_0;
	private BufferedImage img_1;
	
	public SimpleDoubleBufferStrategy(JComponent comp) {
		component = comp;
		
		update();
	}
	
	private void update() {
		w = component.getWidth();
		h = component.getHeight();
		
		img_0 = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		img_1 = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		
		state = false;
	}
	
	public BufferedImage getCurrent() {
		if (w != component.getWidth() || h != component.getHeight()) {
			update();
		}
		
		return state ? img_0 : img_1;
	}
	
	public void doSwitch() {
		state = ! state;
	}
}
