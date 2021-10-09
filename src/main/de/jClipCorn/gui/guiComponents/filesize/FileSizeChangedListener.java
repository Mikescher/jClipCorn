package de.jClipCorn.gui.guiComponents.filesize;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface FileSizeChangedListener extends EventListener {
	void fileSizeChanged(ActionEvent e);
}
