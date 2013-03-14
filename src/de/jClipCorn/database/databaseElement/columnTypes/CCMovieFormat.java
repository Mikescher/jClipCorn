package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;

public enum CCMovieFormat {
	MKV(0),
	AVI(1),
	MPEG(2),
	IMG(3),
	IFO(4),
	WMV(5),
	MP4(6),
	DIVX(7),
	FLV(8);
	
	// Names sind gleichzeitig die extensions
	private final static String names[] = {"mkv", "avi", "mpeg", "img", "ifo", "wmv", "mp4", "divx", "flv"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
	private final static String altnames[] = {"mkv", "avi", "mpg", "img", "ifo", "wmv", "mp4", "divx", "flv"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
	private int id;
	
	CCMovieFormat(int val) {
		id = val;
	}
	
	public int asInt() {
		return id;
	}
	
	public static CCMovieFormat find(int val) {
		if (val >= 0 && val < CCMovieFormat.values().length) {
			return CCMovieFormat.values()[val]; // Geht nur wenn alle Zahlen nach der Reihe da sind
		}
		return null;
	}

	public String asString() {
		return names[asInt()];
	}
	
	public String asString_Alt() {
		return altnames[asInt()];
	}
	
	public static String[] getList() {
		return names;
	}
	
	public static boolean isValidMovieFormat(String ext) {
		for (String se : names) {
			if (se.equalsIgnoreCase(ext)) {
				return true;
			}
		}
		for (String se : altnames) {
			if (se.equalsIgnoreCase(ext)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static CCMovieFormat getMovieFormat(String ext) {
		for (int i = 0; i < names.length; i++) {
			if (names[i].equalsIgnoreCase(ext)) {
				return CCMovieFormat.find(i);
			}
		}
		
		for (int i = 0; i < altnames.length; i++) {
			if (altnames[i].equalsIgnoreCase(ext)) {
				return CCMovieFormat.find(i);
			}
		}
		
		return null;
	}

	public static int compare(CCMovieFormat o1, CCMovieFormat o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case MKV:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FORMAT_0);
		case AVI:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FORMAT_1);
		case MPEG:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FORMAT_2);
		case IMG:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FORMAT_3);
		case IFO:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FORMAT_4);
		case WMV:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FORMAT_5);
		case MP4:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FORMAT_6);
		case DIVX:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FORMAT_7);
		case FLV:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_FORMAT_8);
		default:
			return null;
		}
	}
}
