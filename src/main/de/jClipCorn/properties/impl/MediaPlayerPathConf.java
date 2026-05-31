package de.jClipCorn.properties.impl;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.property.CCExecutableProperty;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;

public class MediaPlayerPathConf implements CCExecutableProperty.ExecPathValidator {

	public static MediaPlayerPathConf INST = new MediaPlayerPathConf();

	@Override
	@SuppressWarnings("nls")
	public boolean Validate(FSPath p) {
		if (!p.exists()) {
			CCLog.addWarning(Str.format("Failed to verify mediaplayer binary '"+p+"' - file does not exist"));
			return false;
		}
		if (!p.isFile()) {
			CCLog.addWarning(Str.format("Failed to verify mediaplayer binary '"+p+"' - path is not a file"));
			return false;
		}
		if (!p.canExecute()) {
			CCLog.addWarning(Str.format("Failed to verify mediaplayer binary '"+p+"' - file is not executable"));
			return false;
		}

		return true;
	}
}
