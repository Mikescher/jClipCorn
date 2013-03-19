package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;

public enum CCMovieStatus {
	STATUS_OK(0),
	STATUS_LOWQUALITY(1),
	STATUS_MISSINGVIDEOTIME(2); // Höherer Wert  =>  Höhere Priorität
	
	private final static String names[] = {
		LocaleBundle.getString("CCMovieStatus.Status0"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieStatus.Status1"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieStatus.Status2")   //$NON-NLS-1$
	};
	
	private int id;

	CCMovieStatus(int val) {
		id = val;
	}
	
	public int asInt() {
		return id;
	}
	
	public String asString() {
		return names[asInt()];
	}
	
	public static String[] getList() {
		return names;
	}

	public static CCMovieStatus find(int val) {
		if (val >= 0 && val < CCMovieStatus.values().length) { 
			return CCMovieStatus.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}

	public ImageIcon getIcon() {
		switch (this) {
		case STATUS_LOWQUALITY:
		case STATUS_MISSINGVIDEOTIME:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_QUALITY_STATUS);
		case STATUS_OK:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_STATUS_OK);
		default:
			return null;
		}
	}

	public String getIconName() {
		switch (this) {
		case STATUS_LOWQUALITY:
		case STATUS_MISSINGVIDEOTIME:
			return Resources.ICN_TABLE_QUALITY_STATUS;
		case STATUS_OK:
			return Resources.ICN_TABLE_STATUS_OK;
		default:
			return ""; //$NON-NLS-1$
		}
	}
}
