package de.jClipCorn.gui.guiComponents.tags;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface TagsChangedListener extends EventListener {
	void tagsChanged(ActionEvent e);
}
