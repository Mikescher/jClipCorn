package de.jClipCorn.features.metadata.mediaquery;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryException;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaQueryResult {

	private static final Pattern REX_LANGUAGE_REPEAT = Pattern.compile("^\\s*(\\w+)(\\s*/\\s*\\1)+\\s*$"); //$NON-NLS-1$
	private static final Pattern REX_LANGUAGE_STR1   = Pattern.compile("^([^\\s]+)\\s*\\(?<r>[^\\s]+\\)$"); //$NON-NLS-1$
	private static final Pattern REX_LANGUAGE_STR2   = Pattern.compile("^(?<r>[^\\s]+)\\s*\\([^\\s]+\\)$"); //$NON-NLS-1$
	private static final Pattern REX_LANGUAGE_STR3   = Pattern.compile("^(?<r1>[^\\-]+)-(?<r2>[^\\-]+)$"); //$NON-NLS-1$

	public final String Raw;
	
	public final long CDate;             // from file attributes
	public final long MDate;             // from file attributes

	public final String Checksum;

	public final String Format;          // NULL if not set
	public final String Format_Version;  // NULL if not set
	public final long FileSize;
	public final double Duration;        // -1 if not set
	public final int OverallBitRate;     // -1 if not set
	public final double FrameRate;       // -1 if not set

	public final MediaQueryResultVideoTrack Video;

	public final List<MediaQueryResultVideoTrack> VideoTracks;
	public final List<MediaQueryResultAudioTrack> AudioTracks;
	public final List<MediaQueryResultSubtitleTrack> SubtitleTracks;

	public final CCDBLanguageSet AudioLanguages;   // NULL if only 1 Language without a specifier
	public final CCDBLanguageList SubtitleLanguages;

	private MediaQueryResult(String raw,
							 String hash, long cdate, long mdate, String format, String format_Version, long fileSize,
							 double duration, int overallBitRate, double frameRate,
							 MediaQueryResultVideoTrack video, List<MediaQueryResultVideoTrack> videoTracks,
							 List<MediaQueryResultAudioTrack> audioTracks,
							 List<MediaQueryResultSubtitleTrack> subtitleTracks,
							 CCDBLanguageSet language, CCDBLanguageList subtitles) {

		Raw               = raw;
		Checksum          = hash;
		CDate             = cdate;
		MDate             = mdate;
		Format            = format;
		Format_Version    = format_Version;
		FileSize          = fileSize;
		Duration          = duration;
		OverallBitRate    = overallBitRate;
		FrameRate         = frameRate;
		Video             = video;
		VideoTracks       = Collections.unmodifiableList(videoTracks);
		AudioTracks       = Collections.unmodifiableList(audioTracks);
		SubtitleTracks    = Collections.unmodifiableList(subtitleTracks);
		AudioLanguages    = language;
		SubtitleLanguages = subtitles;

	}

	@SuppressWarnings("nls")
	public static MediaQueryResult parse(String raw, long cdate, long mdate, String hash, CCXMLElement xml, boolean doNotValidateLangs) throws InnerMediaQueryException, CCXMLException {
		boolean foundGeneral = false;
		boolean foundVideo = false;

		String format         = Str.Empty;
		String format_Version = Str.Empty;
		long   fileSize       = 0;
		double duration       = 0.0;
		int    overallBitRate = 0;
		double frameRate      = 0.0;

		List<MediaQueryResultVideoTrack>    vtracks = new ArrayList<>();
		List<MediaQueryResultAudioTrack>    atracks = new ArrayList<>();
		List<MediaQueryResultSubtitleTrack> stracks = new ArrayList<>();

		for (CCXMLElement track : xml.getAllChildren("track"))
		{
			String strtype = track.getAttributeValueOrThrow("type");
			switch (strtype) {
				case "General":
					foundGeneral = true;

					format         = track.getFirstChildValueOrDefault("Format", null);
					format_Version = track.getFirstChildValueOrDefault("Format_Version", null);
					fileSize       = track.getFirstChildLongValueOrThrow("FileSize");
					duration       = track.getFirstChildDoubleValueOrDefault("Duration", -1);
					overallBitRate = track.getFirstChildIntValueOrDefault("OverallBitRate", -1);
					frameRate      = track.getFirstChildDoubleValueOrDefault("FrameRate", -1);

					break;
				case "Video":
					foundVideo = true;
					vtracks.add(MediaQueryResultVideoTrack.parse(track));
					break;
				case "Audio":
					atracks.add(MediaQueryResultAudioTrack.parse(track));
					break;
				case "Text":
					stracks.add(MediaQueryResultSubtitleTrack.parse(track));
					break;
				case "Menu":
				case "Other":
					// Ignored
					break;
				default:
					throw new InnerMediaQueryException("Unknown Track Type: " + strtype);
			}
		}

		if (!foundGeneral) throw new InnerMediaQueryException("No track 'General' found");
		if (!foundVideo)   throw new InnerMediaQueryException("No track 'Video' found");

		if (vtracks.size() == 1 && vtracks.get(0).Duration != -1) duration = vtracks.get(0).Duration;

		CCDBLanguageSet alng = getAudioLang(atracks, doNotValidateLangs);
		CCDBLanguageList slng = getSubLang(stracks, doNotValidateLangs);

		return new MediaQueryResult(
				raw, hash,
				cdate, mdate,
				format, format_Version, fileSize, duration, overallBitRate, frameRate,
				vtracks.get(0), vtracks,
				atracks, stracks,
				alng, slng);
	}

	private static CCDBLanguageSet getAudioLang(List<MediaQueryResultAudioTrack> tcks, boolean doNotValidateLangs) throws InnerMediaQueryException {

		if (tcks.size() == 1 && tcks.get(0).Language == null) return null;

		if (CCStreams.iterate(tcks).any(t -> t.Language == null))
		{
			if (doNotValidateLangs) return null;
			String info = CCStreams.iterate(tcks).stringjoin(t -> t.Language==null ? "NULL": t.Language, ", "); //$NON-NLS-1$ //$NON-NLS-2$
			throw new InnerMediaQueryException("No audio language set in tracks ("+info+")"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		HashSet<CCDBLanguage> lng = new HashSet<>();
		for (var t : tcks) lng.add(t.getLanguage());
		return CCDBLanguageSet.createDirect(lng);
	}

	private static CCDBLanguageList getSubLang(List<MediaQueryResultSubtitleTrack> tcks, boolean doNotValidateLangs) throws InnerMediaQueryException
	{
		try
		{
			List<CCDBLanguage> lng = new ArrayList<>();
			for (var t : tcks)
			{
				var l = doNotValidateLangs ? t.getLanguageOrNull() : t.getLanguage();
				if (l != null) lng.add(l);
			}
			return CCDBLanguageList.createDirect(lng);
		}
		catch (InnerMediaQueryException e)
		{
			String info = CCStreams.iterate(tcks).stringjoin(t -> "["+(t.Language==null ? "NULL": t.Language)+"|"+(t.Title==null ? "NULL": t.Title)+"] --> " + Opt.ofNullable(t.getLanguageOrNull()).mapOrElse(CCDBLanguage::getLongString, "/NULL/"), "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			throw new InnerMediaQueryException("No subtitle language set in tracks\n\n"+info, e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@SuppressWarnings("nls")
	public static boolean parseBool(String value) throws InnerMediaQueryException {
		if (value.equals("Yes")) return true;
		if (value.equals("No")) return false;
		throw new InnerMediaQueryException("Unknown boolean := '" + value + "'");
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

	@SuppressWarnings("nls")
	public static CCDBLanguage getLanguageOrNullFromIdent(String langval)
	{
		// https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
		// https://www.loc.gov/standards/iso639-2/php/code_list.php

		if (Str.isNullOrWhitespace(langval)) return null;

		langval = langval.trim();

		Matcher m1 = REX_LANGUAGE_REPEAT.matcher(langval);
		if (m1.matches()) langval = m1.group(1).trim();

		if (langval.equalsIgnoreCase("de"))                        return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("deu"))                       return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("ger"))                       return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("German"))                    return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("Deutsch"))                   return CCDBLanguage.GERMAN;

		if (langval.equalsIgnoreCase("en"))                        return CCDBLanguage.ENGLISH;
		if (langval.equalsIgnoreCase("uk"))                        return CCDBLanguage.ENGLISH;
		if (langval.equalsIgnoreCase("eng"))                       return CCDBLanguage.ENGLISH;
		if (langval.equalsIgnoreCase("English"))                   return CCDBLanguage.ENGLISH;

		if (langval.equalsIgnoreCase("it"))                        return CCDBLanguage.ITALIAN;
		if (langval.equalsIgnoreCase("ita"))                       return CCDBLanguage.ITALIAN;
		if (langval.equalsIgnoreCase("Italiano"))                  return CCDBLanguage.ITALIAN;
		if (langval.equalsIgnoreCase("Italian"))                   return CCDBLanguage.ITALIAN;

		if (langval.equalsIgnoreCase("ja"))                        return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("jpn"))                       return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("jap"))                       return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("\u65e5\u672c\u8a9e"))        return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("\u306b\u307b\u3093\u3054"))  return CCDBLanguage.JAPANESE;
		if (langval.equalsIgnoreCase("Japanese"))                  return CCDBLanguage.JAPANESE;

		if (langval.equalsIgnoreCase("fr"))                        return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("fre"))                       return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("fra"))                       return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Français"))                  return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Francais"))                  return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Française"))                 return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Francaise"))                 return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Langue Française"))          return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("Langue Francaise"))          return CCDBLanguage.FRENCH;
		if (langval.equalsIgnoreCase("French"))                    return CCDBLanguage.FRENCH;

		if (langval.equalsIgnoreCase("es"))                        return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("spa"))                       return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("Castilian"))                 return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("Español"))                   return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("Espanol"))                   return CCDBLanguage.SPANISH;
		if (langval.equalsIgnoreCase("Spanish"))                   return CCDBLanguage.SPANISH;

		if (langval.equalsIgnoreCase("pt"))                        return CCDBLanguage.PORTUGUESE;
		if (langval.equalsIgnoreCase("por"))                       return CCDBLanguage.PORTUGUESE;
		if (langval.equalsIgnoreCase("Português"))                 return CCDBLanguage.PORTUGUESE;
		if (langval.equalsIgnoreCase("Portugues"))                 return CCDBLanguage.PORTUGUESE;
		if (langval.equalsIgnoreCase("Portuguese"))                return CCDBLanguage.PORTUGUESE;

		if (langval.equalsIgnoreCase("da"))                        return CCDBLanguage.DANISH;
		if (langval.equalsIgnoreCase("dan"))                       return CCDBLanguage.DANISH;
		if (langval.equalsIgnoreCase("Dansk"))                     return CCDBLanguage.DANISH;
		if (langval.equalsIgnoreCase("Danish"))                    return CCDBLanguage.DANISH;

		if (langval.equalsIgnoreCase("fi"))                        return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("fin"))                       return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("Suomi"))                     return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("Suomen kieli"))              return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("Finnish"))                   return CCDBLanguage.FINNISH;
		if (langval.equalsIgnoreCase("fi / fi"))                   return CCDBLanguage.FINNISH;

		if (langval.equalsIgnoreCase("sv"))                        return CCDBLanguage.SWEDISH;
		if (langval.equalsIgnoreCase("swe"))                       return CCDBLanguage.SWEDISH;
		if (langval.equalsIgnoreCase("Svenska"))                   return CCDBLanguage.SWEDISH;
		if (langval.equalsIgnoreCase("Swedish"))                   return CCDBLanguage.SWEDISH;

		if (langval.equalsIgnoreCase("no"))                        return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("nor"))                       return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("nob"))                       return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("nno"))                       return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("Norsk"))                     return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("Norwegian"))                 return CCDBLanguage.NORWEGIAN;

		if (langval.equalsIgnoreCase("nl"))                        return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("nld"))                       return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("dut"))                       return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("Nederlands"))                return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("Vlaams"))                    return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("Dutch"))                     return CCDBLanguage.DUTCH;
		if (langval.equalsIgnoreCase("Flemish"))                   return CCDBLanguage.DUTCH;

		if (langval.equalsIgnoreCase("cs"))                        return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cz"))                        return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("ces"))                       return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cze"))                       return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("čeština"))                   return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cestina"))                   return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("český jazyk"))               return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cesky jazyk"))               return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("Czech"))                     return CCDBLanguage.CZECH;
		if (langval.equalsIgnoreCase("cs / cz"))                   return CCDBLanguage.CZECH;

		if (langval.equalsIgnoreCase("pl"))                        return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("pol"))                       return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("język polski"))              return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("jezyk polski"))              return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("polszczyzna"))               return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("Polish"))                    return CCDBLanguage.POLISH;
		if (langval.equalsIgnoreCase("pl / pl"))                   return CCDBLanguage.POLISH;

		if (langval.equalsIgnoreCase("tr"))                        return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("tur"))                       return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Türkçe"))                    return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Türkce"))                    return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Turkce"))                    return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Tuerkce"))                   return CCDBLanguage.TURKISH;
		if (langval.equalsIgnoreCase("Turkish"))                   return CCDBLanguage.TURKISH;

		if (langval.equalsIgnoreCase("hu"))                        return CCDBLanguage.HUNGARIAN;
		if (langval.equalsIgnoreCase("hun"))                       return CCDBLanguage.HUNGARIAN;
		if (langval.equalsIgnoreCase("Magyar"))                    return CCDBLanguage.HUNGARIAN;
		if (langval.equalsIgnoreCase("Hungarian"))                 return CCDBLanguage.HUNGARIAN;

		if (langval.equalsIgnoreCase("bg"))                        return CCDBLanguage.BULGARIAN;
		if (langval.equalsIgnoreCase("bul"))                       return CCDBLanguage.BULGARIAN;
		if (langval.equalsIgnoreCase("български език"))            return CCDBLanguage.BULGARIAN;
		if (langval.equalsIgnoreCase("Bulgarian"))                 return CCDBLanguage.BULGARIAN;

		if (langval.equalsIgnoreCase("ru"))                        return CCDBLanguage.RUSSIAN;
		if (langval.equalsIgnoreCase("rus"))                       return CCDBLanguage.RUSSIAN;
		if (langval.equalsIgnoreCase("русский"))                   return CCDBLanguage.RUSSIAN;
		if (langval.equalsIgnoreCase("Russian"))                   return CCDBLanguage.RUSSIAN;

		if (langval.equalsIgnoreCase("zh"))                        return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("zho"))                       return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("chi"))                       return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("\u4e2d\u6587"))              return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("Zhōngwén"))                  return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("Zhongwen"))                  return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("\u6c49\u8bed"))              return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("\u6f22\u8a9e"))              return CCDBLanguage.CHINESE;
		if (langval.equalsIgnoreCase("Chinese"))                   return CCDBLanguage.CHINESE;

		if (langval.equalsIgnoreCase("ko"))                        return CCDBLanguage.KOREAN;
		if (langval.equalsIgnoreCase("kor"))                       return CCDBLanguage.KOREAN;
		if (langval.equalsIgnoreCase("korean"))                    return CCDBLanguage.KOREAN;

		if (langval.equalsIgnoreCase("ms"))                        return CCDBLanguage.MALAY;
		if (langval.equalsIgnoreCase("may"))                       return CCDBLanguage.MALAY;
		if (langval.equalsIgnoreCase("msa"))                       return CCDBLanguage.MALAY;
		if (langval.equalsIgnoreCase("malay"))                     return CCDBLanguage.MALAY;

		if (langval.equalsIgnoreCase("fil"))                       return CCDBLanguage.FILIPINO;
		if (langval.equalsIgnoreCase("filipino"))                  return CCDBLanguage.FILIPINO;
		if (langval.equalsIgnoreCase("pilipino"))                  return CCDBLanguage.FILIPINO;

		if (langval.equalsIgnoreCase("id"))                        return CCDBLanguage.INDONESIAN;
		if (langval.equalsIgnoreCase("ind"))                       return CCDBLanguage.INDONESIAN;
		if (langval.equalsIgnoreCase("indonesian"))                return CCDBLanguage.INDONESIAN;

		if (langval.equalsIgnoreCase("ro"))                        return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("rum"))                       return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("ron"))                       return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("romanian"))                  return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("moldavian"))                 return CCDBLanguage.ROMANIAN;
		if (langval.equalsIgnoreCase("moldovan"))                  return CCDBLanguage.ROMANIAN;

		if (langval.equalsIgnoreCase("el"))                        return CCDBLanguage.GREEK;
		if (langval.equalsIgnoreCase("gre"))                       return CCDBLanguage.GREEK;
		if (langval.equalsIgnoreCase("ell"))                       return CCDBLanguage.GREEK;
		if (langval.equalsIgnoreCase("greek"))                     return CCDBLanguage.GREEK;

		if (langval.equalsIgnoreCase("he"))                        return CCDBLanguage.HEBREW;
		if (langval.equalsIgnoreCase("heb"))                       return CCDBLanguage.HEBREW;
		if (langval.equalsIgnoreCase("hebrew"))                    return CCDBLanguage.HEBREW;

		if (langval.equalsIgnoreCase("ar"))                        return CCDBLanguage.ARABIC;
		if (langval.equalsIgnoreCase("ara"))                       return CCDBLanguage.ARABIC;
		if (langval.equalsIgnoreCase("arabic"))                    return CCDBLanguage.ARABIC;

		if (langval.equalsIgnoreCase("hi"))                        return CCDBLanguage.HINDI;
		if (langval.equalsIgnoreCase("hin"))                       return CCDBLanguage.HINDI;
		if (langval.equalsIgnoreCase("hindi"))                     return CCDBLanguage.HINDI;

		if (langval.equalsIgnoreCase("ta"))                        return CCDBLanguage.TAMIL;
		if (langval.equalsIgnoreCase("tam"))                       return CCDBLanguage.TAMIL;
		if (langval.equalsIgnoreCase("tamil"))                     return CCDBLanguage.TAMIL;

		if (langval.equalsIgnoreCase("te"))                        return CCDBLanguage.TELUGU;
		if (langval.equalsIgnoreCase("tel"))                       return CCDBLanguage.TELUGU;
		if (langval.equalsIgnoreCase("telugu"))                    return CCDBLanguage.TELUGU;

		if (langval.equalsIgnoreCase("th"))                        return CCDBLanguage.THAI;
		if (langval.equalsIgnoreCase("tha"))                       return CCDBLanguage.THAI;
		if (langval.equalsIgnoreCase("thai"))                      return CCDBLanguage.THAI;

		if (langval.equalsIgnoreCase("hr"))                        return CCDBLanguage.CROATIAN;
		if (langval.equalsIgnoreCase("hrv"))                       return CCDBLanguage.CROATIAN;
		if (langval.equalsIgnoreCase("croatian"))                  return CCDBLanguage.CROATIAN;

		if (langval.equalsIgnoreCase("ml"))                        return CCDBLanguage.MALAYALAM;
		if (langval.equalsIgnoreCase("mal"))                       return CCDBLanguage.MALAYALAM;
		if (langval.equalsIgnoreCase("malayalam"))                 return CCDBLanguage.MALAYALAM;

		if (langval.equalsIgnoreCase("nb"))                        return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("nob"))                       return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("bokmål"))                    return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("bokmal"))                    return CCDBLanguage.NORWEGIAN;
		if (langval.equalsIgnoreCase("norwegian"))                 return CCDBLanguage.NORWEGIAN;

		if (langval.equalsIgnoreCase("vi"))                        return CCDBLanguage.VIETNAMESE;
		if (langval.equalsIgnoreCase("vie"))                       return CCDBLanguage.VIETNAMESE;
		if (langval.equalsIgnoreCase("vietnamese"))                return CCDBLanguage.VIETNAMESE;

		if (langval.equalsIgnoreCase("is"))                        return CCDBLanguage.ICELANDIC;
		if (langval.equalsIgnoreCase("ice"))                       return CCDBLanguage.ICELANDIC;
		if (langval.equalsIgnoreCase("isl"))                       return CCDBLanguage.ICELANDIC;
		if (langval.equalsIgnoreCase("icelandic"))                 return CCDBLanguage.ICELANDIC;

		if (langval.equalsIgnoreCase("rom"))                       return CCDBLanguage.ROMANY;
		if (langval.equalsIgnoreCase("romany"))                    return CCDBLanguage.ROMANY;

		if (langval.equalsIgnoreCase("gaa"))                       return CCDBLanguage.GA;
		if (langval.equalsIgnoreCase("ga"))                        return CCDBLanguage.GA;

		if (langval.equalsIgnoreCase("lt"))                        return CCDBLanguage.LITHUANIAN;
		if (langval.equalsIgnoreCase("lit"))                       return CCDBLanguage.LITHUANIAN;
		if (langval.equalsIgnoreCase("iithuanian"))                return CCDBLanguage.LITHUANIAN;

		if (langval.equalsIgnoreCase("lv"))                        return CCDBLanguage.LATVIAN;
		if (langval.equalsIgnoreCase("lav"))                       return CCDBLanguage.LATVIAN;
		if (langval.equalsIgnoreCase("latvian"))                   return CCDBLanguage.LATVIAN;

		if (langval.equalsIgnoreCase("sk"))                        return CCDBLanguage.SLOVAK;
		if (langval.equalsIgnoreCase("slo"))                       return CCDBLanguage.SLOVAK;
		if (langval.equalsIgnoreCase("slk"))                       return CCDBLanguage.SLOVAK;
		if (langval.equalsIgnoreCase("slovak"))                    return CCDBLanguage.SLOVAK;

		if (langval.equalsIgnoreCase("sl"))                        return CCDBLanguage.SLOVENIAN;
		if (langval.equalsIgnoreCase("slv"))                       return CCDBLanguage.SLOVENIAN;

		if (langval.equalsIgnoreCase("et"))                        return CCDBLanguage.ESTONIAN;
		if (langval.equalsIgnoreCase("est"))                       return CCDBLanguage.ESTONIAN;
		if (langval.equalsIgnoreCase("estonian"))                  return CCDBLanguage.ESTONIAN;

		if (langval.equalsIgnoreCase("cy"))                        return CCDBLanguage.WELSH;
		if (langval.equalsIgnoreCase("wel"))                       return CCDBLanguage.WELSH;
		if (langval.equalsIgnoreCase("cym"))                       return CCDBLanguage.WELSH;
		if (langval.equalsIgnoreCase("welsh"))                     return CCDBLanguage.WELSH;

		if (langval.equalsIgnoreCase("ca"))                        return CCDBLanguage.CATALAN;
		if (langval.equalsIgnoreCase("cat"))                       return CCDBLanguage.CATALAN;
		if (langval.equalsIgnoreCase("catalan"))                   return CCDBLanguage.CATALAN;

		if (langval.equalsIgnoreCase("gl"))                        return CCDBLanguage.GALICIAN;
		if (langval.equalsIgnoreCase("glg"))                       return CCDBLanguage.GALICIAN;
		if (langval.equalsIgnoreCase("galician"))                  return CCDBLanguage.GALICIAN;

		if (langval.equalsIgnoreCase("ukr"))                       return CCDBLanguage.UKRAINIAN;
		if (langval.equalsIgnoreCase("ukrainian"))                 return CCDBLanguage.UKRAINIAN;

		if (langval.equalsIgnoreCase("eu"))                        return CCDBLanguage.BASQUE;
		if (langval.equalsIgnoreCase("baq"))                       return CCDBLanguage.BASQUE;
		if (langval.equalsIgnoreCase("eus"))                       return CCDBLanguage.BASQUE;
		if (langval.equalsIgnoreCase("basque"))                    return CCDBLanguage.BASQUE;

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

	public static int parsePseudoPercInt(CCXMLParser owner, String val, int def) throws CCXMLException {
		if (val == null) return def;
		if (!val.contains("/"))
		{
			try {
				return Integer.parseInt(val);
			} catch (NumberFormatException e) {
				throw new CCXMLException(Str.format("The value \"{0}\" is not an pseudo-integer", val), owner.getXMLString()); //$NON-NLS-1$
			}
		}
		else
		{
			var split = val.split("/");
			try {
				var sa = Integer.parseInt(split[0].trim());
				var sb = Integer.parseInt(split[1].trim());

				return (int)Math.round((sa*1.0) / (sb*1.0));

			} catch (NumberFormatException e) {
				throw new CCXMLException(Str.format("The value \"{0}\" is not an pseudo-integer", val), owner.getXMLString()); //$NON-NLS-1$
			}
		}
	}

	public CCMediaInfo toMediaInfo() {
		MediaQueryResultVideoTrack video = getDefaultVideoTrack();
		if (video == null) return CCMediaInfo.EMPTY;

		MediaQueryResultAudioTrack audio = getDefaultAudioTrack();
		if (audio == null) return CCMediaInfo.EMPTY;

		int tbr = getTotalBitrate();

		if (Duration         == -1)   return CCMediaInfo.EMPTY;
		if (OverallBitRate   == -1)   return CCMediaInfo.EMPTY;

		if (video.Format     == null) return CCMediaInfo.EMPTY;
		if (video.FrameRate  == -1)   return CCMediaInfo.EMPTY;
		if (video.BitDepth   == -1)   return CCMediaInfo.EMPTY;
		if (video.FrameCount == -1)   return CCMediaInfo.EMPTY;
		//if (video.CodecID    == null) return CCMediaInfo.EMPTY;

		if (audio.Format     == null) return CCMediaInfo.EMPTY;
		if (audio.Channels   == -1)   return CCMediaInfo.EMPTY;
		//if (audio.CodecID    == null) return CCMediaInfo.EMPTY;

		if (tbr              == -1)   return CCMediaInfo.EMPTY;

		if (FileSize   <= 0)          return CCMediaInfo.EMPTY;

		return CCMediaInfo.create
		(
			CDate, MDate, new CCFileSize(FileSize), Checksum,
			Duration, tbr,
			video.Format, video.Width, video.Height, video.FrameRate, video.BitDepth, video.FrameCount, Str.coalesce(video.CodecID),
			audio.Format, audio.Channels, Str.coalesce(audio.CodecID), audio.Samplingrate
		);
	}

	public int getTotalBitrate() {
		MediaQueryResultVideoTrack video = getDefaultVideoTrack();
		if (video == null) return -1;

		MediaQueryResultAudioTrack audio = getDefaultAudioTrack();
		if (audio == null) return -1;

		int br_vid = (video.BitRateNominal != -1) ? video.BitRateNominal : video.BitRate;
		int br_aud = (audio.BitRateNominal != -1) ? audio.BitRateNominal : audio.BitRate;

		if (br_vid == -1 || br_aud == -1) return OverallBitRate; // can also be -1

		if (OverallBitRate != -1 && OverallBitRate < (br_vid + br_aud)) return OverallBitRate;

		return br_vid + br_aud;
	}

	public MediaQueryResultVideoTrack getDefaultVideoTrack() {
		MediaQueryResultVideoTrack video = CCStreams.iterate(VideoTracks).firstOrNull(t -> t.Default);
		if (video == null && !VideoTracks.isEmpty()) video = VideoTracks.get(0);
		return video;
	}

	public MediaQueryResultAudioTrack getDefaultAudioTrack() {
		MediaQueryResultAudioTrack audio = CCStreams.iterate(AudioTracks).firstOrNull(t -> t.Default);
		if (audio == null && !AudioTracks.isEmpty()) audio = AudioTracks.get(0);
		return audio;
	}

	public PartialMediaInfo toPartial() {
		MediaQueryResultVideoTrack video = getDefaultVideoTrack();
		MediaQueryResultAudioTrack audio = getDefaultAudioTrack();

		int tbr = getTotalBitrate();

		return PartialMediaInfo.create
		(
			Opt.of(Raw),
			Opt.of(CDate),
			Opt.of(MDate),
			Opt.of(new CCFileSize(FileSize)),
			Str.isNullOrWhitespace(Checksum) ? Opt.empty() : Opt.of(Checksum),
			(Duration==-1) ? Opt.empty() : Opt.of(Duration),
			(tbr==-1) ? Opt.empty() : Opt.of(tbr),
			(video == null || Str.isNullOrWhitespace(video.Format)) ? Opt.empty() : Opt.of(video.Format),
			(video == null || video.Width <= 0) ? Opt.empty() : Opt.of(video.Width),
			(video == null || video.Height <= 0) ? Opt.empty() : Opt.of(video.Height),
			(video == null || video.FrameRate == -1) ? Opt.empty() : Opt.of(video.FrameRate),
			(video == null || video.BitDepth == -1) ? Opt.empty() : Opt.of(video.BitDepth),
			(video == null || video.FrameCount == -1) ? Opt.empty() : Opt.of(video.FrameCount),
			(video == null || Str.isNullOrWhitespace(video.CodecID)) ? Opt.empty() : Opt.of(video.CodecID),
			(audio == null || Str.isNullOrWhitespace(audio.Format)) ? Opt.empty() : Opt.of(audio.Format),
			(audio == null || audio.Channels == -1) ? Opt.empty() : Opt.of(audio.Channels),
			(audio == null || Str.isNullOrWhitespace(audio.CodecID)) ? Opt.empty() : Opt.of(audio.CodecID),
			(audio == null || audio.Samplingrate == -1) ? Opt.empty() : Opt.of(audio.Samplingrate)
		);
	}
}
