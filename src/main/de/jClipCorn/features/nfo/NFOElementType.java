package de.jClipCorn.features.nfo;

import de.jClipCorn.gui.localization.LocaleBundle;

public enum NFOElementType {
	MOVIE,
	SERIES,
	EPISODE;

	public String getLocalized() {
		return LocaleBundle.getString("CreateNFOFrame.type." + name());
	}
}
