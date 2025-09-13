package de.jClipCorn.gui.resources.reftypes;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class ResourceRef {

	protected enum ResourceCategory {ICON, IMAGE}

	public static ConcurrentMap<UUID, ResourceRef> allImageResources = new ConcurrentHashMap<>();
	public static ConcurrentMap<UUID, ResourceRef> allIconResources  = new ConcurrentHashMap<>();

	public final String ident;
	public final UUID id;
	private final ResourceCategory category;

	public boolean doPreload; // preload resource (if the global preload setting is `on` or `async`)

	protected ResourceRef(String _ident, ResourceCategory cat, boolean preload) {
		ident = _ident;
		id = UUID.randomUUID();
		category = cat;
		doPreload = preload;

		if (cat == ResourceCategory.ICON)  allIconResources.put(id, this);
		if (cat == ResourceCategory.IMAGE) allImageResources.put(id, this);
	}

	public abstract void preload();

	public abstract List<ResourceRef> getDirectDependencies();

	public abstract BufferedImage getImage();

	public void preloadRootElement()
	{
		if (doPreload) preload();
	}

	public boolean isIcon() {
		return category == ResourceCategory.ICON;
	}

	public boolean isImage() {
		return category == ResourceCategory.IMAGE;
	}

}
