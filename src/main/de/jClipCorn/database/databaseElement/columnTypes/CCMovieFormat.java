package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.formatter.PathFormatter;

public enum CCMovieFormat implements ContinoousEnum<CCMovieFormat> {
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
	private final static String NAMES[] = {"mkv", "avi", "mpeg", "img", "ifo", "wmv", "mp4", "divx", "flv"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
	private final static String ALTNAMES[] = {"mkv", "avi", "mpg", "img", "ifo", "wmv", "mp4", "divx", "flv"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
	private int id;
	
	private static EnumWrapper<CCMovieFormat> wrapper = new EnumWrapper<>(MKV);
	
	private CCMovieFormat(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCMovieFormat> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return NAMES[asInt()];
	}
	
	public String asStringAlt() {
		return ALTNAMES[asInt()];
	}
	
	@Override
	public String[] getList() {
		return NAMES;
	}

	@Override
	public CCMovieFormat[] evalues() {
		return CCMovieFormat.values();
	}
	
	public static boolean isValidMovieFormat(String ext) {
		for (String se : NAMES) {
			if (se.equalsIgnoreCase(ext)) {
				return true;
			}
		}
		for (String se : ALTNAMES) {
			if (se.equalsIgnoreCase(ext)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static CCMovieFormat getMovieFormat(String ext) {
		for (int i = 0; i < NAMES.length; i++) {
			if (NAMES[i].equalsIgnoreCase(ext)) {
				return wrapper.find(i);
			}
		}
		
		for (int i = 0; i < ALTNAMES.length; i++) {
			if (ALTNAMES[i].equalsIgnoreCase(ext)) {
				return wrapper.find(i);
			}
		}
		
		return null;
	}
	
	public static CCMovieFormat getMovieFormatFromPath(String path) {
		return getMovieFormat(PathFormatter.getExtension(path));
	}
	
	public static CCMovieFormat getMovieFormatFromPathOrDefault(String path) {
		CCMovieFormat mf = getMovieFormat(PathFormatter.getExtension(path));
		
		if (mf == null) 
			mf = AVI;
		
		return mf;
	}
	
	public static CCMovieFormat getMovieFormatOrDefault(String ext) {
		CCMovieFormat mf = getMovieFormat(ext);
		
		if (mf == null) 
			mf = AVI;
		
		return mf;
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

	public static CCMovieFormat getMovieFormatFromPaths(String path0, String... paths) {
		CCMovieFormat fmt = null;
		
		if (path0 != null && !path0.isEmpty()) {
			fmt = getMovieFormatFromPath(path0);
		}
		
		for (String path : paths) {
			if (path != null && !path.isEmpty()) {
				CCMovieFormat result = getMovieFormatFromPath(path);
				
				if (result != null && result != fmt) return null;
			}
		}
		
		return fmt;
	}
}
