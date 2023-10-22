package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.EnumValueNotFoundException;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;

@SuppressWarnings("HardCodedStringLiteral")
public enum CCDBLanguage implements ContinoousEnum<CCDBLanguage> {

	GERMAN    ( 0, "GER", "German",     "CCMovieLanguage.German",     Resources.ICN_TABLE_LANGUAGE_00),
	ENGLISH   ( 1, "ENG", "English",    "CCMovieLanguage.English",    Resources.ICN_TABLE_LANGUAGE_01),
	MUTED     ( 2, "MUT", "Muted",      "CCMovieLanguage.Muted",      Resources.ICN_TABLE_LANGUAGE_02),
	FRENCH    ( 3, "FR",  "French",     "CCMovieLanguage.French",     Resources.ICN_TABLE_LANGUAGE_03),
	JAPANESE  ( 4, "JAP", "Japanese",   "CCMovieLanguage.Japanese",   Resources.ICN_TABLE_LANGUAGE_04),
	ITALIAN   ( 5, "IT",  "Italian",    "CCMovieLanguage.Italian",    Resources.ICN_TABLE_LANGUAGE_05),
	SPANISH   ( 6, "SPA", "Spanish",    "CCMovieLanguage.Spanish",    Resources.ICN_TABLE_LANGUAGE_06),
	PORTUGUESE( 7, "POR", "Portuguese", "CCMovieLanguage.Portuguese", Resources.ICN_TABLE_LANGUAGE_07),
	DANISH    ( 8, "DAN", "Danish",     "CCMovieLanguage.Danish",     Resources.ICN_TABLE_LANGUAGE_08),
	FINNISH   ( 9, "FIN", "Finnish",    "CCMovieLanguage.Finnish",    Resources.ICN_TABLE_LANGUAGE_09),
	SWEDISH   (10, "SWE", "Swedisch",   "CCMovieLanguage.Swedisch",   Resources.ICN_TABLE_LANGUAGE_10),
	NORWEGIAN (11, "NOR", "Norwegian",  "CCMovieLanguage.Norwegian",  Resources.ICN_TABLE_LANGUAGE_11),
	DUTCH     (12, "NL",  "Dutch",      "CCMovieLanguage.Dutch",      Resources.ICN_TABLE_LANGUAGE_12),
	CZECH     (13, "CS",  "Czech",      "CCMovieLanguage.Czech",      Resources.ICN_TABLE_LANGUAGE_13),
	POLISH    (14, "POL", "Polish",     "CCMovieLanguage.Polish",     Resources.ICN_TABLE_LANGUAGE_14),
	TURKISH   (15, "TR",  "Turkish",    "CCMovieLanguage.Turkish",    Resources.ICN_TABLE_LANGUAGE_15),
	HUNGARIAN (16, "HU",  "Hungarian",  "CCMovieLanguage.Hungarian",  Resources.ICN_TABLE_LANGUAGE_16),
	BULGARIAN (17, "BUL", "Bulgarian",  "CCMovieLanguage.Bulgarian",  Resources.ICN_TABLE_LANGUAGE_17),
	RUSSIAN   (18, "RUS", "Russian",    "CCMovieLanguage.Russian",    Resources.ICN_TABLE_LANGUAGE_18),
	CHINESE   (19, "CHI", "Chinese",    "CCMovieLanguage.Chinese",    Resources.ICN_TABLE_LANGUAGE_19),
	KOREAN    (20, "KOR", "Korean",     "CCMovieLanguage.Korean",     Resources.ICN_TABLE_LANGUAGE_20),
	MALAY     (21, "MAY", "Malay",      "CCMovieLanguage.Malay",      Resources.ICN_TABLE_LANGUAGE_21),
	FILIPINO  (22, "FIL", "Filipino",   "CCMovieLanguage.Filipino",   Resources.ICN_TABLE_LANGUAGE_22),
	INDONESIAN(23, "IND", "Indonesian", "CCMovieLanguage.Indonesian", Resources.ICN_TABLE_LANGUAGE_23),
	ROMANIAN  (24, "RUM", "Romanian",   "CCMovieLanguage.Romanian",   Resources.ICN_TABLE_LANGUAGE_24),
	GREEK     (25, "GRE", "Greek",      "CCMovieLanguage.Greek",      Resources.ICN_TABLE_LANGUAGE_25),
	HEBREW    (26, "HEB", "Hebrew",     "CCMovieLanguage.Hebrew",     Resources.ICN_TABLE_LANGUAGE_26),
	ARABIC    (27, "ARA", "Arabic",     "CCMovieLanguage.Arabic",     Resources.ICN_TABLE_LANGUAGE_27),
	HINDI     (28, "HIN", "Hindi",      "CCMovieLanguage.Hindi",      Resources.ICN_TABLE_LANGUAGE_28),
	TAMIL     (29, "TAM", "Tamil",      "CCMovieLanguage.Tamil",      Resources.ICN_TABLE_LANGUAGE_29),
	TELUGU    (30, "TEL", "Telugu",     "CCMovieLanguage.Telugu",     Resources.ICN_TABLE_LANGUAGE_30),
	THAI      (31, "THA", "Thai",       "CCMovieLanguage.Thai",       Resources.ICN_TABLE_LANGUAGE_31),
	CROATIAN  (32, "HRV", "Croatian",   "CCMovieLanguage.Croatian",   Resources.ICN_TABLE_LANGUAGE_32),
	MALAYALAM (33, "MAL", "Malayalam",  "CCMovieLanguage.Malayalam",  Resources.ICN_TABLE_LANGUAGE_33),
	VIETNAMESE(34, "VIE", "Vietnamese", "CCMovieLanguage.Vietnamese", Resources.ICN_TABLE_LANGUAGE_34),
	ICELANDIC (35, "ICE", "Icelandic",  "CCMovieLanguage.Icelandic",  Resources.ICN_TABLE_LANGUAGE_35),
	ROMANY    (36, "ROM", "Romany",     "CCMovieLanguage.Romany",     Resources.ICN_TABLE_LANGUAGE_36),
	GA        (37, "GAA", "Ga",         "CCMovieLanguage.Ga",         Resources.ICN_TABLE_LANGUAGE_37),
	LITHUANIAN(38, "LIT", "Lithuanian", "CCMovieLanguage.Lithuanian", Resources.ICN_TABLE_LANGUAGE_38),
	LATVIAN   (39, "LAV", "Latvian",    "CCMovieLanguage.Latvian",    Resources.ICN_TABLE_LANGUAGE_39),
	SLOVAK    (40, "SLO", "Slovak",     "CCMovieLanguage.Slovak",     Resources.ICN_TABLE_LANGUAGE_40),
	SLOVENIAN (41, "SLV", "Slovenian",  "CCMovieLanguage.Slovenian",  Resources.ICN_TABLE_LANGUAGE_41),
	ESTONIAN  (42, "EST", "Estonian",   "CCMovieLanguage.Estonian",   Resources.ICN_TABLE_LANGUAGE_42),
	WELSH     (43, "CYM", "Welsh",      "CCMovieLanguage.Welsh",      Resources.ICN_TABLE_LANGUAGE_43),
	CATALAN   (44, "CAT", "Catalan",    "CCMovieLanguage.Catalan",    Resources.ICN_TABLE_LANGUAGE_44),
	GALICIAN  (45, "GLG", "Galician",   "CCMovieLanguage.Galician",   Resources.ICN_TABLE_LANGUAGE_45),
	UKRAINIAN (46, "UKR", "Ukrainian",  "CCMovieLanguage.Ukrainian",  Resources.ICN_TABLE_LANGUAGE_46),
	BASQUE    (47, "BAQ", "Basque",     "CCMovieLanguage.Basque",     Resources.ICN_TABLE_LANGUAGE_47),
	KANNADA   (48, "KAN", "Basque",     "CCMovieLanguage.Kannada",    Resources.ICN_TABLE_LANGUAGE_48);


	private static final EnumWrapper<CCDBLanguage> wrapper = new EnumWrapper<>(GERMAN);

	public static final CCDBLanguage[] PRIMARY_LANGUAGES = {CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH, CCDBLanguage.JAPANESE};

	private final int id;

	private final String shortName;
	private final String longName;
	private final String localName;
	private final String localNameRef;

	private final IconRef icnref;

	CCDBLanguage(int val, String shrt, String lng, String lcl, IconRef ref) {
		id = val;
		shortName    = shrt;
		longName     = lng;
		localName    = LocaleBundle.getString(lcl);
		localNameRef = lcl;
		icnref       = ref;
	}
	
	public static EnumWrapper<CCDBLanguage> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}

	public static CCDBLanguage findByLongString(String s) throws CCFormatException {
		for (var v : values()) if (v.longName.equalsIgnoreCase(s)) return v;
		throw new EnumValueNotFoundException(s, CCDBLanguage.class);
	}

	public static CCDBLanguage findByShortString(String s) throws CCFormatException {
		for (var v : values()) if (v.shortName.equalsIgnoreCase(s)) return v;
		throw new EnumValueNotFoundException(s, CCDBLanguage.class);
	}

	@Override
	public int asInt() {
		return id;
	}
	
	@Override
	public String asString() {
		return localName;
	}

	public String getLocalNameRef() {
		return localNameRef;
	}

	@Override
	public String[] getList() {
		return CCStreams.iterate(wrapper.allValues()).map(p -> p.localName).toArray(new String[0]);
	}

	public static int compare(CCDBLanguage o1, CCDBLanguage o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}

	public IconRef getIconRef() {
		return icnref;
	}

	public ImageIcon getIcon() {
		return getIconRef().get();
	}

	public String getShortString() {
		return shortName;
	}

	public String getLongString() {
		return longName;
	}

	@Override
	public CCDBLanguage[] evalues() {
		return CCDBLanguage.values();
	}

	public long asBitMask() {
		return 1L<<id;
	}

	public CCDBLanguage nextLanguage() {
		return wrapper.findOrFatalError((id+1)%values().length);
	}
}
