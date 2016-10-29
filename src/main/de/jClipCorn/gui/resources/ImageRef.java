package de.jClipCorn.gui.resources;

import java.util.UUID;

public final class ImageRef {
	public final UUID id;
	public final String path;
	
	public ImageRef(String _path) {
		id = UUID.randomUUID();
		path = _path;
	}
}
