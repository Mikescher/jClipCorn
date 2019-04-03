package de.jClipCorn.gui.mainFrame.clipStatusbar;

import java.awt.Component;
import java.awt.GridBagConstraints;

public class ClipStatusbarColumn {
	private final int width;
	private final GridBagConstraints constraints;
	private final Component component;
	
	public ClipStatusbarColumn(Component c, GridBagConstraints gbc, int w) {
		super();
		this.width = w;
		this.component = c;
		this.constraints = gbc;
	}

	public GridBagConstraints getConstraints(int position) {
		constraints.gridx = position;
		return constraints;
	}

	public Component getComponent() {
		return component;
	}

	public int getWidth() {
		return width;
	}
}
