package de.jClipCorn.gui.guiComponents.jCanvasLabel;

import java.awt.*;
import java.awt.event.ComponentEvent;

public class PaintComponentEvent extends ComponentEvent {

	public final JCanvasLabel Component;
	public final Graphics2D Graphics;

	public PaintComponentEvent(JCanvasLabel lbl, Graphics g) {
		super(lbl, 0);
		Component = lbl;
		Graphics  = (Graphics2D)g;
	}
}