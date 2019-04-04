package de.jClipCorn.features.log;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;

public enum CCLogType {
	LOG_ELEM_UNDEFINED, 
	LOG_ELEM_INFORMATION, 
	LOG_ELEM_WARNING, 
	LOG_ELEM_ERROR,
	LOG_ELEM_FATALERROR;

	public ImageIcon getSmallIcon() {
		switch (this) {
		case LOG_ELEM_UNDEFINED:
			return Resources.ICN_LOG_UNDEFINIED.get16x16();
		case LOG_ELEM_INFORMATION:
			return Resources.ICN_LOG_OK.get16x16();
		case LOG_ELEM_WARNING:
			return Resources.ICN_LOG_WARNING.get16x16();
		case LOG_ELEM_ERROR:
			case LOG_ELEM_FATALERROR:
			return Resources.ICN_LOG_ERROR.get16x16();
		default:
			return null;
		}
	}
}
