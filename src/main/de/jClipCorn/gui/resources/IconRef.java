package de.jClipCorn.gui.resources;

import java.util.UUID;

public final class IconRef {
	public final UUID id;
	public final String path;
	public final CachedIconType type;
	
	public IconRef(String _path, CachedIconType _type) {
		id = UUID.randomUUID();
		path = _path;
		type = _type;
	}
}
