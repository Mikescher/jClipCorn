package de.jClipCorn.gui.frames.previewMovieFrame;

import de.jClipCorn.database.history.CCCombinedHistoryEntry;

import java.util.EventListener;
import java.util.EventObject;

public interface PMHSelectionListener extends EventListener {

	class PMHSelectionEvent extends EventObject {
		public final CCCombinedHistoryEntry Entry;

		public PMHSelectionEvent(Object source, CCCombinedHistoryEntry entry) {
			super(source);
			Entry = entry;
		}
	}

	void actionPerformed(PMHSelectionEvent e);
}