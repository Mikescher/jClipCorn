package de.jClipCorn.gui.guiComponents.referenceChooser;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface ReferenceChangedListener extends EventListener {
	void referenceChanged(ActionEvent e);
}
