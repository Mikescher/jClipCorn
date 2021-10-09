package de.jClipCorn.gui.guiComponents.onlinescore;

import java.awt.event.ActionEvent;
import java.util.EventListener;

public interface OnlineScoreChangedListener extends EventListener {
	void onlineScoreChanged(ActionEvent e);
}
