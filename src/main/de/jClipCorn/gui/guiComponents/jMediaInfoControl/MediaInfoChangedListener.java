package de.jClipCorn.gui.guiComponents.jMediaInfoControl;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface MediaInfoChangedListener extends EventListener {
	void mediaInfoChanged(ActionEvent e);
}
