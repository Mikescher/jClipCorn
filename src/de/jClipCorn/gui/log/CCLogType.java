package de.jClipCorn.gui.log;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;

public enum CCLogType {
	LOG_ELEM_UNDEFINED, 
	LOG_ELEM_INFORMATION, 
	LOG_ELEM_WARNING, 
	LOG_ELEM_ERROR,
	LOG_ELEM_FATALERROR;

	public ImageIcon getIcon() {
		switch (this) {
		case LOG_ELEM_UNDEFINED:
			return CachedResourceLoader.getImageIcon(Resources.ICN_LOG_UNDEFINIED);
		case LOG_ELEM_INFORMATION:
			return CachedResourceLoader.getImageIcon(Resources.ICN_LOG_OK);
		case LOG_ELEM_WARNING:
			return CachedResourceLoader.getImageIcon(Resources.ICN_LOG_WARNING);
		case LOG_ELEM_ERROR:
			return CachedResourceLoader.getImageIcon(Resources.ICN_LOG_ERROR);
		case LOG_ELEM_FATALERROR:
			return CachedResourceLoader.getImageIcon(Resources.ICN_LOG_ERROR);
		default:
			return null;
		}
	}
	
	public ImageIcon getSmallIcon() {
		switch (this) {
		case LOG_ELEM_UNDEFINED:
			return CachedResourceLoader.getSmallImageIcon(Resources.ICN_LOG_UNDEFINIED);
		case LOG_ELEM_INFORMATION:
			return CachedResourceLoader.getSmallImageIcon(Resources.ICN_LOG_OK);
		case LOG_ELEM_WARNING:
			return CachedResourceLoader.getSmallImageIcon(Resources.ICN_LOG_WARNING);
		case LOG_ELEM_ERROR:
			return CachedResourceLoader.getSmallImageIcon(Resources.ICN_LOG_ERROR);
		case LOG_ELEM_FATALERROR:
			return CachedResourceLoader.getSmallImageIcon(Resources.ICN_LOG_ERROR);
		default:
			return null;
		}
	}
};
