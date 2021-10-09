package de.jClipCorn.gui.guiComponents.groupListEditor;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface GroupListChangedListener extends EventListener {
	void groupListChanged(ActionEvent e);
}
