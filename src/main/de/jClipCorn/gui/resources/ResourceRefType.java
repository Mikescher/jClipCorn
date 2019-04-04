package de.jClipCorn.gui.resources;

public enum ResourceRefType {
	IMAGE,

	IMAGE_COMBINED,

	ICON_16,
	ICON_32,
	ICON_64,
	ICON_OTHER,

	ICON_OTHER_COMBINED;

	public boolean isImage() {
		return this == ResourceRefType.IMAGE || this == ResourceRefType.IMAGE_COMBINED;
	}

	public boolean isIcon() {
		switch (this)
		{
			case ICON_16:
			case ICON_32:
			case ICON_64:
			case ICON_OTHER:
			case ICON_OTHER_COMBINED:
				return true;
			default:
				return false;
		}
	}
}
