package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCQuality implements ContinoousEnum<CCQuality> {
	STREAM(0), 
	ONE_CD(1), 
	MULTIPLE_CD(2), 
	DVD(3),
	BLURAY(4);

	private static long FSIZE_MAX_STREAM = 	5 * 1024 * 1024;	// 5 MB/min
	private static long FSIZE_MAX_CD = 		20 * 1024 * 1024;	// 20 MB/min
	private static long FSIZE_MAX_DVD = 	50 * 1024 * 1024; 	// 50 MB/min
	
	private final static String[] NAMES = {
		LocaleBundle.getString("CCMovieQuality.Quality0"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieQuality.Quality1"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieQuality.Quality2"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieQuality.Quality3"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieQuality.Quality4")   //$NON-NLS-1$
	};
	
	private int id;
	
	private static EnumWrapper<CCQuality> wrapper = new EnumWrapper<>(STREAM);

	private CCQuality(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCQuality> getWrapper() {
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
	
	@Override
	public String[] getList() {
		return NAMES;
	}

	public static int compare(CCQuality o1, CCQuality o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case STREAM:
			return Resources.ICN_TABLE_QUALITY_0.get();
		case ONE_CD:
			return Resources.ICN_TABLE_QUALITY_1.get();
		case MULTIPLE_CD:
			return Resources.ICN_TABLE_QUALITY_2.get();
		case DVD:
			return Resources.ICN_TABLE_QUALITY_3.get();
		case BLURAY:
			return Resources.ICN_TABLE_QUALITY_4.get();
		default:
			return null;
		}
	}
	
	public static CCQuality calculateQuality(CCFileSize size, int length, int partCount) {
		return calculateQuality(size.getBytes(), length, partCount);
	}

	public static CCQuality calculateQuality(long size, int length, int partCount) {
		if (length == 0) {
			return STREAM;
		}
		
		long qualityIdent = size / length;
		
		if (qualityIdent <= FSIZE_MAX_STREAM) {
			return CCQuality.STREAM;
		} else if (qualityIdent <= FSIZE_MAX_CD) {
			if (partCount == 1) {
				return CCQuality.ONE_CD;
			} else {
				return CCQuality.MULTIPLE_CD;
			}
		} else if (qualityIdent <= FSIZE_MAX_DVD) {
			return CCQuality.DVD;
		} else {
			return CCQuality.BLURAY;
		}
	}

	@Override
	public CCQuality[] evalues() {
		return CCQuality.values();
	}
}