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
	JAPANESE(4),
	ITALIAN(5),
	SPANISH(6),
	PORTUGUESE(7),
	DANISH(8),
	FINNISH(9),
	SWEDISH(10),
	NORWEGIAN(11),
	DUTCH(12),
	CZECH(13),
	POLISH(14),
	TURKISH(15),
	HUNGARIAN(16),
	BULGARIAN(17),
	RUSSIAN(18),
	CHINESE(19);


	private static EnumWrapper<CCDBLanguage> wrapper = new EnumWrapper<>(GERMAN);

	private final static String[] NAMES = {
			LocaleBundle.getString("CCMovieLanguage.German"),     //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.English"),    //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Muted"),      //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.French"),     //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Japanese"),   //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Italian"),    //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Spanish"),    //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Portuguese"), //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Danish"),     //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Finnish"),    //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Swedisch"),   //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Norwegian"),  //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Dutch"),      //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Czech"),      //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Polish"),     //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Turkish"),    //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Hungarian"),  //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Bulgarian"),  //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Russian"),    //$NON-NLS-1$
			LocaleBundle.getString("CCMovieLanguage.Chinese"),    //$NON-NLS-1$
	};

	private final static String[] SHORTNAMES = {
			"GER",   //$NON-NLS-1$
			"ENG",   //$NON-NLS-1$
			"MUT",   //$NON-NLS-1$
			"FR",    //$NON-NLS-1$
			"JAP",   //$NON-NLS-1$
			"IT",    //$NON-NLS-1$
			"SPA",   //$NON-NLS-1$
			"POR",   //$NON-NLS-1$
			"DAN",   //$NON-NLS-1$
			"FIN",   //$NON-NLS-1$
			"SWE",   //$NON-NLS-1$
			"NOR",   //$NON-NLS-1$
			"NL",    //$NON-NLS-1$
			"CS",    //$NON-NLS-1$
			"POL",   //$NON-NLS-1$
			"TR",    //$NON-NLS-1$
			"HU",    //$NON-NLS-1$
			"BUL",   //$NON-NLS-1$
			"RUS",   //$NON-NLS-1$
			"CHI",   //$NON-NLS-1$
	};

	private final static String[] LONGNAMES = {
			"German",     //$NON-NLS-1$
			"English",    //$NON-NLS-1$
			"Muted",      //$NON-NLS-1$
			"French",     //$NON-NLS-1$
			"Japanese",   //$NON-NLS-1$
			"Italian",    //$NON-NLS-1$
			"Spanish",    //$NON-NLS-1$
			"Portuguese", //$NON-NLS-1$
			"Danish",     //$NON-NLS-1$
			"Finnish",    //$NON-NLS-1$
			"Swedisch",   //$NON-NLS-1$
			"Norwegian",  //$NON-NLS-1$
			"Dutch",      //$NON-NLS-1$
			"Czech",      //$NON-NLS-1$
			"Polish",     //$NON-NLS-1$
			"Turkish",    //$NON-NLS-1$
			"Hungarian",  //$NON-NLS-1$
			"Bulgarian",  //$NON-NLS-1$
			"Russian",    //$NON-NLS-1$
			"Chinese",    //$NON-NLS-1$
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
		return Resources.ICN_TABLE_LANGUAGE[this.asInt()].get();
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
		return wrapper.findOrFatalError((id+1)%NAMES.length);
	}
}
