package de.jClipCorn.gui.guiComponents.dateTimeListEditor;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface DateTimeChangedListener extends EventListener {
	void dateTimeChanged(ActionEvent e);
}
