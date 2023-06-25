package de.jClipCorn.gui.resources.reftypes;

import de.jClipCorn.gui.resources.ResourceRefType;
import de.jClipCorn.util.Str;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ResourceRef {

	public static ConcurrentHashMap<UUID, ResourceRef> allImageResources = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<UUID, ResourceRef> allIconResources = new ConcurrentHashMap<>();

	public final String ident;
	public final UUID id;
	public final ResourceRefType type;

	public boolean doPreload; // preload resource (if the global preload setting is `on` or `async`)

	protected ResourceRef(String _ident, ResourceRefType t, boolean preload) {
		ident = _ident;
		id = UUID.randomUUID();
		type = t;
		doPreload = preload;

		if (t.isIcon()) allIconResources.put(id, this);
		if (t.isImage()) allImageResources.put(id, this);
	}

	public abstract void preload();

	public abstract List<ResourceRef> getDirectDependencies();

	public abstract BufferedImage getImage();

	public void preloadRootElement()
	{
		if (doPreload) preload();
	}
}
