package de.jClipCorn.gui.guiComponents.editCoverControl;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface CoverChangedListener extends EventListener {
	void coverChanged(ActionEvent e);
}
