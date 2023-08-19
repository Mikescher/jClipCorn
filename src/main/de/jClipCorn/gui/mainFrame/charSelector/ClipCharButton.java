package de.jClipCorn.gui.mainFrame.charSelector;

import de.jClipCorn.util.helper.ColorHelper;

import javax.swing.*;
import java.awt.*;

public class ClipCharButton extends JButton {

	private boolean inactive = false;

	private Color c1;
	private Color c2;

	public ClipCharButton(String s) {
		super(s);

		c1 = this.getBackground();
		c2 = this.getForeground();
	}

	public void setInactive(boolean b) {
		if (inactive == b) return;

		inactive = b;

		if (this.inactive) {
			this.setForeground(ColorHelper.mix(c2, c1, 0.25));
		} else {
			this.setForeground(c2);
		}

		this.invalidate();
		this.repaint();
	}

	public boolean getInactive() {
		return inactive;
	}
}
