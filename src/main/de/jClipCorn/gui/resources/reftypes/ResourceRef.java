package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.ResourceRefType;

import java.util.UUID;

public abstract class ResourceRef {
	public final String ident;
	public final UUID id;
	public final ResourceRefType type;

	protected ResourceRef(String _ident, ResourceRefType t) {
		ident = _ident;
		id = UUID.randomUUID();
		type = t;
	}

	public abstract void preload();
}
