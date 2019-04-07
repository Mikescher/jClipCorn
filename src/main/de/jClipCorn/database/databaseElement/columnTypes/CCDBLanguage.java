package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCDBLanguage implements ContinoousEnum<CCDBLanguage> {
	GERMAN(0),
	ENGLISH(1),
	MUTED(2),
	FRENCH(3),
	JAPANESE(4);

	private static EnumWrapper<CCDBLanguage> wrapper = new EnumWrapper<>(GERMAN);

	private final static String[] NAMES = {
		LocaleBundle.getString("CCMovieLanguage.German"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.English"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.Muted"),    //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.French"),   //$NON-NLS-1$
		LocaleBundle.getString("CCMovieLanguage.Japanese")  //$NON-NLS-1$
	};

	private final static String[] SHORTNAMES = {
			"GER",   //$NON-NLS-1$
			"ENG",   //$NON-NLS-1$
			"MUT",   //$NON-NLS-1$
			"FR",    //$NON-NLS-1$
			"JAP"    //$NON-NLS-1$
	};

	private final static String[] LONGNAMES = {
			"German",    //$NON-NLS-1$
			"English",   //$NON-NLS-1$
			"Muted",     //$NON-NLS-1$
			"French",    //$NON-NLS-1$
			"Japanese"   //$NON-NLS-1$
	};

	private final int id;

	CCDBLanguage(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCDBLanguage> getWrapper() {
		return wrapper;
	}

	public static CCDBLanguage findByLongString(String s) {
		for (int i = 0; i < LONGNAMES.length; i++) {
			if (LONGNAMES[i].equalsIgnoreCase(s)) return values()[i];
		}
		return null;
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

	public static int compare(CCDBLanguage o1, CCDBLanguage o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}

	public ImageIcon getIcon() {
		switch (this) {
		case GERMAN:
			return Resources.ICN_TABLE_LANGUAGE_0_0.get();
		case ENGLISH:
			return Resources.ICN_TABLE_LANGUAGE_1_1.get();
		case MUTED:
			return Resources.ICN_TABLE_LANGUAGE_2_2.get();
		case FRENCH:
			return Resources.ICN_TABLE_LANGUAGE_3_3.get();
		case JAPANESE:
			return Resources.ICN_TABLE_LANGUAGE_4_4.get();
		default:
			return null;
		}
	}

	public String getShortString() {
		return SHORTNAMES[asInt()];
	}

	public String getLongString() {
		return LONGNAMES[asInt()];
	}

	@Override
	public CCDBLanguage[] evalues() {
		return CCDBLanguage.values();
	}

	public long asBitMask() {
		return 1<<id;
	}

	public CCDBLanguage nextLanguage() {
		return wrapper.find((id+1)%NAMES.length);
	}
}
