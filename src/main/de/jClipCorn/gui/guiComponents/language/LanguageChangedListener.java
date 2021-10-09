package de.jClipCorn.gui.guiComponents.language;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface LanguageChangedListener extends EventListener {
	void languageChanged(ActionEvent e);
}
