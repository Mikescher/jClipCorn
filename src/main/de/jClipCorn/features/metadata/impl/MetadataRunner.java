package de.jClipCorn.features.metadata.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.*;
import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryException;
import de.jClipCorn.features.metadata.exceptions.MetadataQueryException;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.ErrOpt;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MetadataRunner {

	private static final Pattern REX_LANGUAGE_REPEAT = Pattern.compile("^\\s*(\\w+)(\\s*/\\s*\\1)+\\s*$");  //$NON-NLS-1$
	private static final Pattern REX_LANGUAGE_STR1   = Pattern.compile("^([^\\s]+)\\s*\\(?<r>[^\\s]+\\)$"); //$NON-NLS-1$
	private static final Pattern REX_LANGUAGE_STR2   = Pattern.compile("^(?<r>[^\\s]+)\\s*\\([^\\s]+\\)$"); //$NON-NLS-1$
	private static final Pattern REX_LANGUAGE_STR3   = Pattern.compile("^(?<r1>[^\\-]+)-(?<r2>[^\\-]+)$");  //$NON-NLS-1$

	protected List<ErrOpt<CCDBLanguage, MetadataError>> extractAudioLangs(List<AudioTrackMetadata> atracks)
	{
		return CCStreams.iterate(atracks).map(AudioTrackMetadata::calcCCDBLanguage).toList();
	}

	protected List<ErrOpt<CCDBLanguage, MetadataError>> extractSubLangs(List<SubtitleTrackMetadata> stracks)
	{
		return CCStreams.iterate(stracks).map(SubtitleTrackMetadata::calcCCDBLanguage).toList();
	}

	@SuppressWarnings("nls")
	public static CCDBLanguage getLanguage(String _langval, String _title) throws InnerMediaQueryException {

		if (!Str.isNullOrWhitespace(_langval))
		{
			{ var r = getLanguageOrNullFromStr(_langval); if (r != null) return r; }

			Matcher m2 = REX_LANGUAGE_STR3.matcher(_langval);
			if (m2.matches()) { var r = getLanguageOrNullFromStr(m2.group("r1").trim()); if (r != null) return r; }
			if (m2.matches()) { var r = getLanguageOrNullFromStr(m2.group("r2").trim()); if (r != null) return r; }
		}

		if (!Str.isNullOrWhitespace(_title))
		{
			Matcher m0 = REX_LANGUAGE_STR1.matcher(_title);
			if (m0.matches()) { var r = getLanguageOrNullFromStr(m0.group("r").trim()); if (r != null) return r; }

			Matcher m1 = REX_LANGUAGE_STR2.matcher(_title);
			if (m1.matches()) { var r = getLanguageOrNullFromStr(m1.group("r").trim()); if (r != null) return r; }

			{ var r = getLanguageOrNullFromStr(_title); if (r != null) return r; }
		}

		throw new InnerMediaQueryException("Unknown audio language: ['" + _langval + "' | '" + _title + "']");
	}

	@SuppressWarnings("nls")
	public static CCDBLanguage getLanguageOrNullFromIdent(String langval)
	{
		// https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
		// https://www.loc.gov/standards/iso639-2/php/code_list.php

		if (Str.isNullOrWhitespace(langval)) return null;

		langval = langval.trim();

		Matcher m1 = REX_LANGUAGE_REPEAT.matcher(langval);
		if (m1.matches()) langval = m1.group(1).trim();

		if (langval.equalsIgnoreCase("de"))                              return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("deu"))                             return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("ger"))                             return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("German"))                          return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("Deutsch"))                         return CCDBLanguage.GERMAN;

		if (langval.equalsIgnoreCase("en"))                              return CCDBLanguage.ENGLISH;
		if (langval.equalsIgnoreCase("uk"))                              return CCDBLanguage.ENGLISH;
		if (langval.equalsIgnoreCase("eng"))                             return CCDBLanguage.ENGLISH;
		if (langval.equalsIgnoreCase("English"))                         return CCDBLanguage.ENGLISH;

		if (langval.equalsIgnoreCase("it"))                              return CCDBLanguage.ITALIAN;
		if (langval.equalsIgnoreCase("ita"))                             return CCDBLanguage.ITALIAN;
		if (langval.equalsIgnoreCase("Italiano"))                        return CCDBLanguage.ITALIAN;
		if (langval.equalsIgnoreCase("Italian"))                         return CCDBLanguage.ITALIAN;

		if (langval.equalsIgnoreCase("ja"))                              return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("jpn"))                             return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("jap"))                             return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("\u65e5\u672c\u8a9e"))              return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("\u306b\u307b\u3093\u3054"))        return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("Japanese"))                        return CCDBLanguage.JAPANESE;

		if (langval.equalsIgnoreCase("fr"))                              return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("fre"))                             return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("fra"))                             return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Français"))                        return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Francais"))                        return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Française"))                       return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Francaise"))                       return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Langue Française"))                return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Langue Francaise"))                return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("French"))                          return CCDBLanguage.FRENCH;

		if (langval.equalsIgnoreCase("es"))                              return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("spa"))                             return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("Castilian"))                       return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("Español"))                         return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("Espanol"))                         return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("Spanish"))                         return CCDBLanguage.SPANISH;

		if (langval.equalsIgnoreCase("pt"))                              return CCDBLanguage.PORTUGUESE;
		if (langval.equalsIgnoreCase("por"))                             return CCDBLanguage.PORTUGUESE;
		if (langval.equalsIgnoreCase("Português"))                       return CCDBLanguage.PORTUGUESE;
		if (langval.equalsIgnoreCase("Portugues"))                       return CCDBLanguage.PORTUGUESE;
		if (langval.equalsIgnoreCase("Portuguese"))                      return CCDBLanguage.PORTUGUESE;

		if (langval.equalsIgnoreCase("da"))                              return CCDBLanguage.DANISH;
		if (langval.equalsIgnoreCase("dan"))                             return CCDBLanguage.DANISH;
		if (langval.equalsIgnoreCase("Dansk"))                           return CCDBLanguage.DANISH;
		if (langval.equalsIgnoreCase("Danish"))                          return CCDBLanguage.DANISH;

		if (langval.equalsIgnoreCase("fi"))                              return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("fin"))                             return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("Suomi"))                           return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("Suomen kieli"))                    return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("Finnish"))                         return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("fi / fi"))                         return CCDBLanguage.FINNISH;

		if (langval.equalsIgnoreCase("sv"))                              return CCDBLanguage.SWEDISH;
		if (langval.equalsIgnoreCase("swe"))                             return CCDBLanguage.SWEDISH;
		if (langval.equalsIgnoreCase("Svenska"))                         return CCDBLanguage.SWEDISH;
		if (langval.equalsIgnoreCase("Swedish"))                         return CCDBLanguage.SWEDISH;

		if (langval.equalsIgnoreCase("no"))                              return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("nor"))                             return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("nob"))                             return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("nno"))                             return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("Norsk"))                           return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("Norwegian"))                       return CCDBLanguage.NORWEGIAN;

		if (langval.equalsIgnoreCase("nl"))                              return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("nld"))                             return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("dut"))                             return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("Nederlands"))                      return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("Vlaams"))                          return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("Dutch"))                           return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("Flemish"))                         return CCDBLanguage.DUTCH;

		if (langval.equalsIgnoreCase("cs"))                              return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cz"))                              return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("ces"))                             return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cze"))                             return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("čeština"))                         return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cestina"))                         return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("český jazyk"))                     return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cesky jazyk"))                     return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("Czech"))                           return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cs / cz"))                         return CCDBLanguage.CZECH;

		if (langval.equalsIgnoreCase("pl"))                              return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("pol"))                             return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("język polski"))                    return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("jezyk polski"))                    return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("polszczyzna"))                     return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("Polish"))                          return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("pl / pl"))                         return CCDBLanguage.POLISH;

		if (langval.equalsIgnoreCase("tr"))                              return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("tur"))                             return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Türkçe"))                          return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Türkce"))                          return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Turkce"))                          return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Tuerkce"))                         return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Turkish"))                         return CCDBLanguage.TURKISH;

		if (langval.equalsIgnoreCase("hu"))                              return CCDBLanguage.HUNGARIAN;
		if (langval.equalsIgnoreCase("hun"))                             return CCDBLanguage.HUNGARIAN;
		if (langval.equalsIgnoreCase("Magyar"))                          return CCDBLanguage.HUNGARIAN;
		if (langval.equalsIgnoreCase("Hungarian"))                       return CCDBLanguage.HUNGARIAN;

		if (langval.equalsIgnoreCase("bg"))                              return CCDBLanguage.BULGARIAN;
		if (langval.equalsIgnoreCase("bul"))                             return CCDBLanguage.BULGARIAN;
		if (langval.equalsIgnoreCase("български език"))                  return CCDBLanguage.BULGARIAN;
		if (langval.equalsIgnoreCase("Bulgarian"))                       return CCDBLanguage.BULGARIAN;

		if (langval.equalsIgnoreCase("ru"))                              return CCDBLanguage.RUSSIAN;
		if (langval.equalsIgnoreCase("rus"))                             return CCDBLanguage.RUSSIAN;
		if (langval.equalsIgnoreCase("русский"))                         return CCDBLanguage.RUSSIAN;
		if (langval.equalsIgnoreCase("Russian"))                         return CCDBLanguage.RUSSIAN;

		if (langval.equalsIgnoreCase("zh"))                              return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("zho"))                             return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("chi"))                             return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("\u4e2d\u6587"))                    return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("Zhōngwén"))                        return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("Zhongwen"))                        return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("\u6c49\u8bed"))                    return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("\u6f22\u8a9e"))                    return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("Chinese"))                         return CCDBLanguage.CHINESE;

		if (langval.equalsIgnoreCase("ko"))                              return CCDBLanguage.KOREAN;
		if (langval.equalsIgnoreCase("kor"))                             return CCDBLanguage.KOREAN;
		if (langval.equalsIgnoreCase("korean"))                          return CCDBLanguage.KOREAN;

		if (langval.equalsIgnoreCase("ms"))                              return CCDBLanguage.MALAY;
		if (langval.equalsIgnoreCase("may"))                             return CCDBLanguage.MALAY;
		if (langval.equalsIgnoreCase("msa"))                             return CCDBLanguage.MALAY;
		if (langval.equalsIgnoreCase("malay"))                           return CCDBLanguage.MALAY;

		if (langval.equalsIgnoreCase("fil"))                             return CCDBLanguage.FILIPINO;
		if (langval.equalsIgnoreCase("filipino"))                        return CCDBLanguage.FILIPINO;
		if (langval.equalsIgnoreCase("pilipino"))                        return CCDBLanguage.FILIPINO;

		if (langval.equalsIgnoreCase("id"))                              return CCDBLanguage.INDONESIAN;
		if (langval.equalsIgnoreCase("ind"))                             return CCDBLanguage.INDONESIAN;
		if (langval.equalsIgnoreCase("indonesian"))                      return CCDBLanguage.INDONESIAN;

		if (langval.equalsIgnoreCase("ro"))                              return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("rum"))                             return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("ron"))                             return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("romanian"))                        return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("moldavian"))                       return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("moldovan"))                        return CCDBLanguage.ROMANIAN;

		if (langval.equalsIgnoreCase("el"))                              return CCDBLanguage.GREEK;
		if (langval.equalsIgnoreCase("gre"))                             return CCDBLanguage.GREEK;
		if (langval.equalsIgnoreCase("ell"))                             return CCDBLanguage.GREEK;
		if (langval.equalsIgnoreCase("greek"))                           return CCDBLanguage.GREEK;

		if (langval.equalsIgnoreCase("he"))                              return CCDBLanguage.HEBREW;
		if (langval.equalsIgnoreCase("heb"))                             return CCDBLanguage.HEBREW;
		if (langval.equalsIgnoreCase("hebrew"))                          return CCDBLanguage.HEBREW;

		if (langval.equalsIgnoreCase("ar"))                              return CCDBLanguage.ARABIC;
		if (langval.equalsIgnoreCase("ara"))                             return CCDBLanguage.ARABIC;
		if (langval.equalsIgnoreCase("arabic"))                          return CCDBLanguage.ARABIC;

		if (langval.equalsIgnoreCase("hi"))                              return CCDBLanguage.HINDI;
		if (langval.equalsIgnoreCase("hin"))                             return CCDBLanguage.HINDI;
		if (langval.equalsIgnoreCase("hindi"))                           return CCDBLanguage.HINDI;

		if (langval.equalsIgnoreCase("ta"))                              return CCDBLanguage.TAMIL;
		if (langval.equalsIgnoreCase("tam"))                             return CCDBLanguage.TAMIL;
		if (langval.equalsIgnoreCase("tamil"))                           return CCDBLanguage.TAMIL;

		if (langval.equalsIgnoreCase("te"))                              return CCDBLanguage.TELUGU;
		if (langval.equalsIgnoreCase("tel"))                             return CCDBLanguage.TELUGU;
		if (langval.equalsIgnoreCase("telugu"))                          return CCDBLanguage.TELUGU;

		if (langval.equalsIgnoreCase("th"))                              return CCDBLanguage.THAI;
		if (langval.equalsIgnoreCase("tha"))                             return CCDBLanguage.THAI;
		if (langval.equalsIgnoreCase("thai"))                            return CCDBLanguage.THAI;

		if (langval.equalsIgnoreCase("hr"))                              return CCDBLanguage.CROATIAN;
		if (langval.equalsIgnoreCase("hrv"))                             return CCDBLanguage.CROATIAN;
		if (langval.equalsIgnoreCase("croatian"))                        return CCDBLanguage.CROATIAN;

		if (langval.equalsIgnoreCase("ml"))                              return CCDBLanguage.MALAYALAM;
		if (langval.equalsIgnoreCase("mal"))                             return CCDBLanguage.MALAYALAM;
		if (langval.equalsIgnoreCase("malayalam"))                       return CCDBLanguage.MALAYALAM;

		if (langval.equalsIgnoreCase("nb"))                              return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("nob"))                             return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("bokmål"))                          return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("bokmal"))                          return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("norwegian"))                       return CCDBLanguage.NORWEGIAN;

		if (langval.equalsIgnoreCase("vi"))                              return CCDBLanguage.VIETNAMESE;
		if (langval.equalsIgnoreCase("vie"))                             return CCDBLanguage.VIETNAMESE;
		if (langval.equalsIgnoreCase("vietnamese"))                      return CCDBLanguage.VIETNAMESE;

		if (langval.equalsIgnoreCase("is"))                              return CCDBLanguage.ICELANDIC;
		if (langval.equalsIgnoreCase("ice"))                             return CCDBLanguage.ICELANDIC;
		if (langval.equalsIgnoreCase("isl"))                             return CCDBLanguage.ICELANDIC;
		if (langval.equalsIgnoreCase("icelandic"))                       return CCDBLanguage.ICELANDIC;

		if (langval.equalsIgnoreCase("rom"))                             return CCDBLanguage.ROMANY;
		if (langval.equalsIgnoreCase("romany"))                          return CCDBLanguage.ROMANY;

		if (langval.equalsIgnoreCase("gaa"))                             return CCDBLanguage.GA;
		if (langval.equalsIgnoreCase("ga"))                              return CCDBLanguage.GA;

		if (langval.equalsIgnoreCase("lt"))                              return CCDBLanguage.LITHUANIAN;
		if (langval.equalsIgnoreCase("lit"))                             return CCDBLanguage.LITHUANIAN;
		if (langval.equalsIgnoreCase("iithuanian"))                      return CCDBLanguage.LITHUANIAN;

		if (langval.equalsIgnoreCase("lv"))                              return CCDBLanguage.LATVIAN;
		if (langval.equalsIgnoreCase("lav"))                             return CCDBLanguage.LATVIAN;
		if (langval.equalsIgnoreCase("latvian"))                         return CCDBLanguage.LATVIAN;

		if (langval.equalsIgnoreCase("sk"))                              return CCDBLanguage.SLOVAK;
		if (langval.equalsIgnoreCase("slo"))                             return CCDBLanguage.SLOVAK;
		if (langval.equalsIgnoreCase("slk"))                             return CCDBLanguage.SLOVAK;
		if (langval.equalsIgnoreCase("slovak"))                          return CCDBLanguage.SLOVAK;

		if (langval.equalsIgnoreCase("sl"))                              return CCDBLanguage.SLOVENIAN;
		if (langval.equalsIgnoreCase("slv"))                             return CCDBLanguage.SLOVENIAN;

		if (langval.equalsIgnoreCase("et"))                              return CCDBLanguage.ESTONIAN;
		if (langval.equalsIgnoreCase("est"))                             return CCDBLanguage.ESTONIAN;
		if (langval.equalsIgnoreCase("estonian"))                        return CCDBLanguage.ESTONIAN;

		if (langval.equalsIgnoreCase("cy"))                              return CCDBLanguage.WELSH;
		if (langval.equalsIgnoreCase("wel"))                             return CCDBLanguage.WELSH;
		if (langval.equalsIgnoreCase("cym"))                             return CCDBLanguage.WELSH;
		if (langval.equalsIgnoreCase("welsh"))                           return CCDBLanguage.WELSH;

		if (langval.equalsIgnoreCase("ca"))                              return CCDBLanguage.CATALAN;
		if (langval.equalsIgnoreCase("cat"))                             return CCDBLanguage.CATALAN;
		if (langval.equalsIgnoreCase("catalan"))                         return CCDBLanguage.CATALAN;

		if (langval.equalsIgnoreCase("gl"))                              return CCDBLanguage.GALICIAN;
		if (langval.equalsIgnoreCase("glg"))                             return CCDBLanguage.GALICIAN;
		if (langval.equalsIgnoreCase("galician"))                        return CCDBLanguage.GALICIAN;

		if (langval.equalsIgnoreCase("ukr"))                             return CCDBLanguage.UKRAINIAN;
		if (langval.equalsIgnoreCase("ukrainian"))                       return CCDBLanguage.UKRAINIAN;

		if (langval.equalsIgnoreCase("eu"))                              return CCDBLanguage.BASQUE;
		if (langval.equalsIgnoreCase("baq"))                             return CCDBLanguage.BASQUE;
		if (langval.equalsIgnoreCase("eus"))                             return CCDBLanguage.BASQUE;
		if (langval.equalsIgnoreCase("basque"))                          return CCDBLanguage.BASQUE;

		if (langval.equalsIgnoreCase("kn"))                              return CCDBLanguage.KANNADA;
		if (langval.equalsIgnoreCase("kan"))                             return CCDBLanguage.KANNADA;
		if (langval.equalsIgnoreCase("\u0C95\u0CA8\u0CCD\u0CA8\u0CA1"))  return CCDBLanguage.KANNADA;
		if (langval.equalsIgnoreCase("Kanaresisch"))                     return CCDBLanguage.KANNADA;
		if (langval.equalsIgnoreCase("Kannada"))                         return CCDBLanguage.KANNADA;

		return null;
	}

	public static boolean isNullLanguage(String langval) {
		if (Str.isNullOrWhitespace(langval))          return true;
		if (langval.equalsIgnoreCase("Undefined"))    return true; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Keine Angabe")) return true; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("unk"))          return true; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Unknown"))      return true; //$NON-NLS-1$

		return false;
	}

	private static CCDBLanguage getLanguageOrNullFromStr(String langval)
	{
		var r1 = getLanguageOrNullFromIdent(langval);
		if (r1 != null) return r1;

		var langvalFix = Str.tryFixEncodingErrors(langval);
		for (var lv2 : langvalFix)
		{
			var r2 = getLanguageOrNullFromIdent(lv2);
			if (r2 != null) return r2;
		}

		return null;
	}


	public abstract VideoMetadata run(FSPath filename) throws IOException, MetadataQueryException;

	public abstract MetadataSourceType getSourceType();
	public abstract boolean isConfiguredAndRunnable();
}
