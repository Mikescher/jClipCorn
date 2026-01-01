package de.jClipCorn.features.nfo;

import de.jClipCorn.gui.localization.LocaleBundle;

public enum NFOStatus {
	ERROR,  // something went wrong
	UNCHANGED,  // NFO exists and matches current metadata
	CHANGED,    // NFO exists but needs updating
	CREATE,     // NFO doesn't exist, needs creation
	DELETE;     // NFO exists but shouldn't (settings disabled)

	public String getLocalized() {
		return LocaleBundle.getString("CreateNFOFrame.status." + name());
	}
}
