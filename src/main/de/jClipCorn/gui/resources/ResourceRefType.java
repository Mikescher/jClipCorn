package de.jClipCorn.gui.resources;

public enum ResourceRefType {
	IMAGE,

	IMAGE_COMBINED,
	IMAGE_RECOLORED,
	IMAGE_DUALMASKED,

	ICON_16,
	ICON_32,
	ICON_64,
	ICON_OTHER,

	ICON_OTHER_COMBINED,
	ICON_OTHER_DUALMASKED;

	public boolean isImage() {
		switch (this)
		{
			case IMAGE:
			case IMAGE_COMBINED:
			case IMAGE_RECOLORED:
			case IMAGE_DUALMASKED:
				return true;
			default:
				return false;
		}
	}

	public boolean isIcon() {
		switch (this)
		{
			case ICON_16:
			case ICON_32:
			case ICON_64:
			case ICON_OTHER:
			case ICON_OTHER_COMBINED:
			case ICON_OTHER_DUALMASKED:
				return true;
			default:
				return false;
		}
	}
}
