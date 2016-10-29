package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.localization.util.LocalizedVector;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCMovieLanguage implements ContinoousEnum<CCMovieLanguage> {
	GERMAN(0),
	ENGLISH(1),
	MUTED(2),
	FRENCH(3),
	JAPANESE(4);

	private final static String NAMES[] = {
		LocaleBundle.getString("CCMovieLanguage.German"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.English"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.Muted"),    //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.French"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.Japanese")  //$NON-NLS-1$
	};

	private final static String NAME_IDS[] = {
		"CCMovieLanguage.German",   //$NON-NLS-1$
		"CCMovieLanguage.English",  //$NON-NLS-1$
		"CCMovieLanguage.Muted",    //$NON-NLS-1$
		"CCMovieLanguage.French",   //$NON-NLS-1$
		"CCMovieLanguage.Japanese"  //$NON-NLS-1$
	};
	
	private final static String SHORTNAMES[] = {
		"GER",   //$NON-NLS-1$
		"ENG",   //$NON-NLS-1$
		"MUT",   //$NON-NLS-1$
		"FR",    //$NON-NLS-1$
		"JAP"    //$NON-NLS-1$
	};
	
	private int id;
	private static EnumWrapper<CCMovieLanguage> wrapper = new EnumWrapper<>(GERMAN);

	private CCMovieLanguage(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCMovieLanguage> getWrapper() {
		return wrapper;
	}

	public static LocalizedVector valueNames() {
		LocalizedVector lv = new LocalizedVector();
		for (String id : NAME_IDS) {
			lv.add(id);
		}
		return lv;
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

	public static int compare(CCMovieLanguage o1, CCMovieLanguage o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}
	
	public ImageIcon getIcon() {
		switch (this) {
		case GERMAN:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_LANGUAGE_0);
		case ENGLISH:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_LANGUAGE_1);
		case MUTED:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_LANGUAGE_2);
		case FRENCH:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_LANGUAGE_3);
		case JAPANESE:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_LANGUAGE_4);
		default:
			return null;
		}
	}

	public String getShortString() {
		return SHORTNAMES[asInt()];
	}

	@Override
	public CCMovieLanguage[] evalues() {
		return CCMovieLanguage.values();
	}
}
