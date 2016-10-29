package de.jClipCorn.gui.log;

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
			return CachedResourceLoader.getIcon(Resources.ICN_LOG_UNDEFINIED.icon16x16);
		case LOG_ELEM_INFORMATION:
			return CachedResourceLoader.getIcon(Resources.ICN_LOG_OK.icon16x16);
		case LOG_ELEM_WARNING:
			return CachedResourceLoader.getIcon(Resources.ICN_LOG_WARNING.icon16x16);
		case LOG_ELEM_ERROR:
			return CachedResourceLoader.getIcon(Resources.ICN_LOG_ERROR.icon16x16);
		case LOG_ELEM_FATALERROR:
			return CachedResourceLoader.getIcon(Resources.ICN_LOG_ERROR.icon16x16);
		default:
			return null;
		}
	}
}
