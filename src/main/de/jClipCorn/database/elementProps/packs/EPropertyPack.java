package de.jClipCorn.database.elementProps.packs;

import de.jClipCorn.database.elementProps.IEProperty;

public abstract class EPropertyPack {
	public boolean isDirty() {
		for (var p: getProperties()) if (p.isDirty()) return true;
		return false;
	}

	public void resetDirty() {
		for (var p: getProperties()) if (p.isDirty()) p.resetDirty();
	}

	public abstract IEProperty[] getProperties();
}
