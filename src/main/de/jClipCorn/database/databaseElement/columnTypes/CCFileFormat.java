package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.filesystem.FSPath;

import javax.swing.*;

public enum CCFileFormat implements ContinoousEnum<CCFileFormat> {
	MKV(0),
	AVI(1),
	MPEG(2),
	IMG(3),
	IFO(4),
	WMV(5),
	MP4(6),
	DIVX(7),
	FLV(8),
	WEBM(9);
	
	// Names sind gleichzeitig die extensions
	private final static String[] NAMES    = {"mkv", "avi", "mpeg", "img", "ifo", "wmv", "mp4", "divx", "flv", "webm"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
	private final static String[] ALTNAMES = {"mkv", "avi", "mpg",  "img", "ifo", "wmv", "mp4", "divx", "flv", "webm"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
	private int id;
	
	private static final EnumWrapper<CCFileFormat> wrapper = new EnumWrapper<>(MKV);
	
	CCFileFormat(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCFileFormat> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
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
	public CCFileFormat[] evalues() {
		return CCFileFormat.values();
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

	public static boolean isValidMovieFormat(FSPath f) {
		return isValidMovieFormat(f.getExtension());
	}
	
	public static CCFileFormat getMovieFormat(String ext) {
		for (int i = 0; i < NAMES.length; i++) {
			if (NAMES[i].equalsIgnoreCase(ext)) {
				return wrapper.findOrFatalError(i);
			}
		}
		
		for (int i = 0; i < ALTNAMES.length; i++) {
			if (ALTNAMES[i].equalsIgnoreCase(ext)) {
				return wrapper.findOrFatalError(i);
			}
		}
		
		return null;
	}
	
	public static CCFileFormat getMovieFormatFromPath(FSPath path) {
		return getMovieFormat(path.getExtension());
	}
	
	public static CCFileFormat getMovieFormatFromPathOrDefault(FSPath path) {
		CCFileFormat mf = getMovieFormat(path.getExtension());
		
		if (mf == null) 
			mf = AVI;
		
		return mf;
	}
	
	public static CCFileFormat getMovieFormatOrDefault(String ext) {
		CCFileFormat mf = getMovieFormat(ext);
		
		if (mf == null) 
			mf = AVI;
		
		return mf;
	}

	public static int compare(CCFileFormat o1, CCFileFormat o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case MKV:
			return Resources.ICN_TABLE_FORMAT_0.get();
		case AVI:
			return Resources.ICN_TABLE_FORMAT_1.get();
		case MPEG:
			return Resources.ICN_TABLE_FORMAT_2.get();
		case IMG:
			return Resources.ICN_TABLE_FORMAT_3.get();
		case IFO:
			return Resources.ICN_TABLE_FORMAT_4.get();
		case WMV:
			return Resources.ICN_TABLE_FORMAT_5.get();
		case MP4:
			return Resources.ICN_TABLE_FORMAT_6.get();
		case DIVX:
			return Resources.ICN_TABLE_FORMAT_7.get();
		case FLV:
			return Resources.ICN_TABLE_FORMAT_8.get();
		case WEBM:
			return Resources.ICN_TABLE_FORMAT_9.get();
		default:
			return null;
		}
	}

	public static CCFileFormat getMovieFormatFromPaths(FSPath path0, FSPath... paths) {
		CCFileFormat fmt = null;
		
		if (path0 != null && !path0.isEmpty()) {
			fmt = getMovieFormatFromPath(path0);
		}
		
		for (var path : paths) {
			if (path != null && !path.isEmpty()) {
				CCFileFormat result = getMovieFormatFromPath(path);
				
				if (result != null && result != fmt) return null;
			}
		}
		
		return fmt;
	}
}
