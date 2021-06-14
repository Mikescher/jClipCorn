package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;

import de.jClipCorn.database.util.CCOnlineRefTypeHelper;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.exceptions.OnlineRefFormatException;
import de.jClipCorn.util.stream.CCStreams;

@SuppressWarnings("nls")
public enum CCOnlineRefType implements ContinoousEnum<CCOnlineRefType> {
	NONE       (0,  "",     "CCOnlineRefType.NONE",        CCOnlineRefTypeHelper.REGEX_NONE, null,                                   Resources.ICN_REF_00, Resources.ICN_REF_00_BUTTON),
	IMDB       (1,  "imdb", "CCOnlineRefType.IMDB",        CCOnlineRefTypeHelper.REGEX_IMDB, CCOnlineRefTypeHelper.REGEX_PASTE_IMDB, Resources.ICN_REF_01, Resources.ICN_REF_01_BUTTON),
	AMAZON     (2,  "amzn", "CCOnlineRefType.AMAZON",      CCOnlineRefTypeHelper.REGEX_AMZN, null,                                   Resources.ICN_REF_02, Resources.ICN_REF_02_BUTTON),
	MOVIEPILOT (3,  "mvpt", "CCOnlineRefType.MOVIEPILOT",  CCOnlineRefTypeHelper.REGEX_MVPT, CCOnlineRefTypeHelper.REGEX_PASTE_MVPT, Resources.ICN_REF_03, Resources.ICN_REF_03_BUTTON),
	THEMOVIEDB (4,  "tmdb", "CCOnlineRefType.THEMOVIEDB",  CCOnlineRefTypeHelper.REGEX_TMDB, CCOnlineRefTypeHelper.REGEX_PASTE_TMDB, Resources.ICN_REF_04, Resources.ICN_REF_04_BUTTON),
	PROXERME   (5,  "prox", "CCOnlineRefType.PROXERME",    CCOnlineRefTypeHelper.REGEX_PROX, CCOnlineRefTypeHelper.REGEX_PASTE_PROX, Resources.ICN_REF_05, Resources.ICN_REF_05_BUTTON),
	MYANIMELIST(6,  "myal", "CCOnlineRefType.MYANIMELIST", CCOnlineRefTypeHelper.REGEX_MYAL, CCOnlineRefTypeHelper.REGEX_PASTE_MYAL, Resources.ICN_REF_06, Resources.ICN_REF_06_BUTTON),
	ANILIST    (7,  "anil", "CCOnlineRefType.ANILIST",     CCOnlineRefTypeHelper.REGEX_ANIL, CCOnlineRefTypeHelper.REGEX_PASTE_ANIL, Resources.ICN_REF_07, Resources.ICN_REF_07_BUTTON),
	ANIMEPLANET(8,  "anpl", "CCOnlineRefType.ANIMEPLANET", CCOnlineRefTypeHelper.REGEX_ANPL, CCOnlineRefTypeHelper.REGEX_PASTE_ANPL, Resources.ICN_REF_08, Resources.ICN_REF_08_BUTTON),
	KITSU      (9,  "kisu", "CCOnlineRefType.KITSU",       CCOnlineRefTypeHelper.REGEX_KISU, CCOnlineRefTypeHelper.REGEX_PASTE_KISU, Resources.ICN_REF_09, Resources.ICN_REF_09_BUTTON),
	ANIDB      (10, "andb", "CCOnlineRefType.ANIDB",       CCOnlineRefTypeHelper.REGEX_ANDB, CCOnlineRefTypeHelper.REGEX_PASTE_ANDB, Resources.ICN_REF_10, Resources.ICN_REF_10_BUTTON),
	THETVDB    (11, "tvdb", "CCOnlineRefType.THETVDB",     CCOnlineRefTypeHelper.REGEX_TVDB, CCOnlineRefTypeHelper.REGEX_PASTE_TVDB, Resources.ICN_REF_11, Resources.ICN_REF_11_BUTTON),
	TVMAZE     (12, "maze", "CCOnlineRefType.TVMAZE",      CCOnlineRefTypeHelper.REGEX_MAZE, CCOnlineRefTypeHelper.REGEX_PASTE_MAZE, Resources.ICN_REF_12, Resources.ICN_REF_12_BUTTON),
	WIKIDE     (13, "wkde", "CCOnlineRefType.WIKIDE",      CCOnlineRefTypeHelper.REGEX_WKDE, CCOnlineRefTypeHelper.REGEX_PASTE_WKDE, Resources.ICN_REF_13, Resources.ICN_REF_13_BUTTON),
	WIKIEN     (14, "wken", "CCOnlineRefType.WIKIEN",      CCOnlineRefTypeHelper.REGEX_WKEN, CCOnlineRefTypeHelper.REGEX_PASTE_WKEN, Resources.ICN_REF_14, Resources.ICN_REF_14_BUTTON),
	OFDB       (15, "ofdb", "CCOnlineRefType.OFDB",        CCOnlineRefTypeHelper.REGEX_OFDB, CCOnlineRefTypeHelper.REGEX_PASTE_OFDB, Resources.ICN_REF_15, Resources.ICN_REF_15_BUTTON);

	private final int id;
	private final String identifier;
	private final String name;
	private final Pattern regexVerification;
	private final Pattern regexPaste;
	private final MultiSizeIconRef icon;
	private final IconRef iconButton;

	private static final EnumWrapper<CCOnlineRefType> wrapper = new EnumWrapper<>(NONE);

	private CCOnlineRefType(int refid, String strid, String bundleid, Pattern regex_value, Pattern regex_paste, MultiSizeIconRef icnSquare, IconRef icnButton) {
		id                = refid;
		identifier        = strid;
		name              = LocaleBundle.getString(bundleid);
		regexVerification = regex_value;
		regexPaste        = regex_paste;
		icon              = icnSquare;
		iconButton        = icnButton;
	}
	
	public static EnumWrapper<CCOnlineRefType> getWrapper() {
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

	public static int compare(CCUserScore s1, CCUserScore s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	@Override
	public String[] getList() {
		return CCStreams.iterate(wrapper.allValues()).map(p -> p.name).toArray(new String[0]);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	@Override
	public String asString() {
		return name;
	}

	public static CCOnlineRefType parse(String strtype) throws OnlineRefFormatException {
		for (CCOnlineRefType val : wrapper.allValues()) {
			if (val.identifier.equalsIgnoreCase(strtype)) return val;
		}
		
		throw new OnlineRefFormatException(strtype);
	}

	public MultiSizeIconRef getIconRef() {
		return icon;
	}

	public Icon getIcon() {
		return icon.get32x32();
	}

	public Icon getIcon16x16() {
		return icon.get16x16();
	}

	public Icon getIconButton() {
		return iconButton.get();
	}
	
	public boolean isValid(String id) {
		return regexVerification.matcher(id).matches();
	}

	public static CCOnlineRefType guessType(String id) {
		CCOnlineRefType result = null;
		
		for (CCOnlineRefType reftype : values()) {
			if (reftype.isValid(id)) {
				if (result != null) return null;
				result = reftype;
			}
		}
		return result;
	}
	
	public static Tuple<CCOnlineRefType, String> extractType(String input) {
		for (CCOnlineRefType val : CCStreams.iterate(wrapper.allValues()).filter(v -> v.regexPaste != null)) {	
			Matcher matcher = val.regexPaste.matcher(input);
			if (matcher.find()) return Tuple.Create(val, matcher.group("id"));
		}
		
		return null;
	}

	@Override
	public CCOnlineRefType[] evalues() {
		return CCOnlineRefType.values();
	}
}