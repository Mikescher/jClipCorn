package de.jClipCorn.gui.guiComponents.jCanvasLabel;

import java.util.EventListener;

public interface PaintComponentListener extends EventListener {
	void paintComponentCalled(PaintComponentEvent e);
}