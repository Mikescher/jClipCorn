package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.ResourceRefType;

import java.util.List;
import java.util.UUID;

public abstract class ResourceRef {
	public final String ident;
	public final UUID id;
	public final ResourceRefType type;

	public boolean doPreload; // preload resource (if the global preload setting is `on` or `async`)

	protected ResourceRef(String _ident, ResourceRefType t, boolean preload) {
		ident = _ident;
		id = UUID.randomUUID();
		type = t;
		doPreload = preload;
	}

	public abstract void preload();

	public abstract List<ResourceRef> getDirectDependencies();

	public void preloadRootElement()
	{
		if (doPreload) preload();
	}
}
