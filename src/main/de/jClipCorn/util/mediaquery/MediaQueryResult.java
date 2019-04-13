package de.jClipCorn.util.mediaquery;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.InnerMediaQueryException;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaQueryResult {

	private static Pattern REX_LANGUAGE_REPEAT = Pattern.compile("^\\s*(\\w+)(\\s*/\\s*\\1)+\\s*$"); //$NON-NLS-1$

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

	public final CCDBLanguageList AudioLanguages;   // NULL if only 1 Language without a specifier
	
	private MediaQueryResult(String format, String format_Version, long fileSize, double duration, int overallBitRate, double frameRate, MediaQueryResultVideoTrack video, List<MediaQueryResultVideoTrack> videoTracks, List<MediaQueryResultAudioTrack> audioTracks, List<MediaQueryResultSubtitleTrack> subtitleTracks, CCDBLanguageList language) {
		Format = format;
		Format_Version = format_Version;
		FileSize = fileSize;
		Duration = duration;
		OverallBitRate = overallBitRate;
		FrameRate = frameRate;
		Video = video;
		VideoTracks = Collections.unmodifiableList(videoTracks);
		AudioTracks = Collections.unmodifiableList(audioTracks);
		SubtitleTracks = Collections.unmodifiableList(subtitleTracks);
		AudioLanguages = language;
	}

	@SuppressWarnings("nls")
	public static MediaQueryResult parse(CCXMLElement xml) throws InnerMediaQueryException, CCXMLException {
		boolean foundGeneral = false;
		boolean foundVideo = false;

		String format = Str.Empty;
		String format_Version = Str.Empty;
		long fileSize = 0;
		double duration = 0.0;
		int overallBitRate = 0;
		double frameRate = 0.0;

		List<MediaQueryResultVideoTrack> vtracks = new ArrayList<>();
		List<MediaQueryResultAudioTrack> atracks = new ArrayList<>();
		List<MediaQueryResultSubtitleTrack> stracks = new ArrayList<>();

		for (CCXMLElement track : xml.getAllChildren("track")) //$NON-NLS-1$
		{
			String strtype = track.getAttributeValueOrThrow("type"); //$NON-NLS-1$
			switch (strtype) {
				case "General": //$NON-NLS-1$
					foundGeneral = true;

					format = track.getFirstChildValueOrDefault("Format", null); //$NON-NLS-1$
					format_Version = track.getFirstChildValueOrDefault("Format_Version", null); //$NON-NLS-1$
					fileSize = track.getFirstChildLongValueOrThrow("FileSize"); //$NON-NLS-1$
					duration = track.getFirstChildDoubleValueOrDefault("Duration", -1); //$NON-NLS-1$
					overallBitRate = track.getFirstChildIntValueOrDefault("OverallBitRate", -1); //$NON-NLS-1$
					frameRate = track.getFirstChildDoubleValueOrDefault("FrameRate", -1); //$NON-NLS-1$

					break;
				case "Video": //$NON-NLS-1$
					foundVideo = true;
					vtracks.add(MediaQueryResultVideoTrack.parse(track));
					break;
				case "Audio": //$NON-NLS-1$
					atracks.add(MediaQueryResultAudioTrack.parse(track));
					break;
				case "Text": //$NON-NLS-1$
					stracks.add(MediaQueryResultSubtitleTrack.parse(track));
					break;
				case "Menu": //$NON-NLS-1$
				case "Other": //$NON-NLS-1$
					// Ignored
					break;
				default:
					throw new InnerMediaQueryException("Unknown Track Type: " + strtype); //$NON-NLS-1$
			}
		}

		if (!foundGeneral) throw new InnerMediaQueryException("No track 'General' found"); //$NON-NLS-1$
		if (!foundVideo) throw new InnerMediaQueryException("No track 'Video' found"); //$NON-NLS-1$

		CCDBLanguageList alng = getLang(atracks);
		
		return new MediaQueryResult(format, format_Version, fileSize, duration, overallBitRate, frameRate, vtracks.get(0), vtracks, atracks, stracks, alng);
	}

	private static CCDBLanguageList getLang(List<MediaQueryResultAudioTrack> tcks) throws InnerMediaQueryException {

		if (tcks.size() == 1 && tcks.get(0).Language == null) return null;

		if (CCStreams.iterate(tcks).any(t -> t.Language == null))
		{
			String info = CCStreams.iterate(tcks).stringjoin(t -> t.Language==null ? "NULL": t.Language, ", "); //$NON-NLS-1$ //$NON-NLS-2$
			throw new InnerMediaQueryException("No audio language set in tracks ("+info+")"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		HashSet<CCDBLanguage> lng = new HashSet<>();
		for (MediaQueryResultAudioTrack t : tcks) lng.add(t.getLanguage());
		return CCDBLanguageList.createDirect(lng);
	}

	@SuppressWarnings("nls")
	public static boolean parseBool(String value) throws InnerMediaQueryException {
		if (value.equals("Yes")) return true; //$NON-NLS-1$
		if (value.equals("No")) return false; //$NON-NLS-1$
		throw new InnerMediaQueryException("Unknown boolean := '" + value + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@SuppressWarnings("nls")
	public static CCDBLanguage getLanguage(String langval) throws InnerMediaQueryException {
		if (langval == null) throw new InnerMediaQueryException("Audio language not set (null)"); //$NON-NLS-1$

		// https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes

		langval = langval.trim();

		Matcher m1 = REX_LANGUAGE_REPEAT.matcher(langval);
		if (m1.matches()) langval = m1.group(1).trim();

		if (langval.equalsIgnoreCase("de"))                        return CCDBLanguage.GERMAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("deu"))                       return CCDBLanguage.GERMAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("ger"))                       return CCDBLanguage.GERMAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("German"))                    return CCDBLanguage.GERMAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Deutsch"))                   return CCDBLanguage.GERMAN; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("en"))                        return CCDBLanguage.ENGLISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("eng"))                       return CCDBLanguage.ENGLISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("English"))                   return CCDBLanguage.ENGLISH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("it"))                        return CCDBLanguage.ITALIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("ita"))                       return CCDBLanguage.ITALIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Italiano"))                  return CCDBLanguage.ITALIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Italian"))                   return CCDBLanguage.ITALIAN; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("ja"))                        return CCDBLanguage.JAPANESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("jpn"))                       return CCDBLanguage.JAPANESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("jap"))                       return CCDBLanguage.JAPANESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("\u65e5\u672c\u8a9e"))        return CCDBLanguage.JAPANESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("\u306b\u307b\u3093\u3054"))  return CCDBLanguage.JAPANESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Japanese"))                  return CCDBLanguage.JAPANESE; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("fr"))                        return CCDBLanguage.FRENCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("fre"))                       return CCDBLanguage.FRENCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("fra"))                       return CCDBLanguage.FRENCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Français"))                  return CCDBLanguage.FRENCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Francais"))                  return CCDBLanguage.FRENCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Française"))                 return CCDBLanguage.FRENCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Francaise"))                 return CCDBLanguage.FRENCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Langue Française"))          return CCDBLanguage.FRENCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Langue Francaise"))          return CCDBLanguage.FRENCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("French"))                    return CCDBLanguage.FRENCH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("es"))                        return CCDBLanguage.SPANISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("spa"))                       return CCDBLanguage.SPANISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Castilian"))                 return CCDBLanguage.SPANISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Español"))                   return CCDBLanguage.SPANISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Espanol"))                   return CCDBLanguage.SPANISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Spanish"))                   return CCDBLanguage.SPANISH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("pt"))                        return CCDBLanguage.PORTUGUESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("por"))                       return CCDBLanguage.PORTUGUESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Português"))                 return CCDBLanguage.PORTUGUESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Portugues"))                 return CCDBLanguage.PORTUGUESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Portuguese"))                return CCDBLanguage.PORTUGUESE; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("da"))                        return CCDBLanguage.DANISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("dan"))                       return CCDBLanguage.DANISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Dansk"))                     return CCDBLanguage.DANISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Danish"))                    return CCDBLanguage.DANISH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("fi"))                        return CCDBLanguage.FINNISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("fin"))                       return CCDBLanguage.FINNISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Suomi"))                     return CCDBLanguage.FINNISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Suomen kieli"))              return CCDBLanguage.FINNISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Finnish"))                   return CCDBLanguage.FINNISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("fi / fi"))                   return CCDBLanguage.FINNISH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("sv"))                        return CCDBLanguage.SWEDISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("swe"))                       return CCDBLanguage.SWEDISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Svenska"))                   return CCDBLanguage.SWEDISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Swedish"))                   return CCDBLanguage.SWEDISH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("no"))                        return CCDBLanguage.NORWEGIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("nor"))                       return CCDBLanguage.NORWEGIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("nob"))                       return CCDBLanguage.NORWEGIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("nno"))                       return CCDBLanguage.NORWEGIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Norsk"))                     return CCDBLanguage.NORWEGIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Norwegian"))                 return CCDBLanguage.NORWEGIAN; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("nl"))                        return CCDBLanguage.DUTCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("nld"))                       return CCDBLanguage.DUTCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("dut"))                       return CCDBLanguage.DUTCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Nederlands"))                return CCDBLanguage.DUTCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Vlaams"))                    return CCDBLanguage.DUTCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Dutch"))                     return CCDBLanguage.DUTCH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Flemish"))                   return CCDBLanguage.DUTCH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("cs"))                        return CCDBLanguage.CZECH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("cz"))                        return CCDBLanguage.CZECH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("ces"))                       return CCDBLanguage.CZECH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("cze"))                       return CCDBLanguage.CZECH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("čeština"))                   return CCDBLanguage.CZECH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("cestina"))                   return CCDBLanguage.CZECH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("český jazyk"))               return CCDBLanguage.CZECH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("cesky jazyk"))               return CCDBLanguage.CZECH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Czech"))                     return CCDBLanguage.CZECH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("cs / cz"))                   return CCDBLanguage.CZECH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("pl"))                        return CCDBLanguage.POLISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("pol"))                       return CCDBLanguage.POLISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("język polski"))              return CCDBLanguage.POLISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("jezyk polski"))              return CCDBLanguage.POLISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("polszczyzna"))               return CCDBLanguage.POLISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Polish"))                    return CCDBLanguage.POLISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("pl / pl"))                   return CCDBLanguage.POLISH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("tr"))                        return CCDBLanguage.TURKISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("tur"))                       return CCDBLanguage.TURKISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Türkçe"))                    return CCDBLanguage.TURKISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Türkce"))                    return CCDBLanguage.TURKISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Turkce"))                    return CCDBLanguage.TURKISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Tuerkce"))                   return CCDBLanguage.TURKISH; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Turkish"))                   return CCDBLanguage.TURKISH; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("hu"))                        return CCDBLanguage.HUNGARIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("hun"))                       return CCDBLanguage.HUNGARIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Magyar"))                    return CCDBLanguage.HUNGARIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Hungarian"))                 return CCDBLanguage.HUNGARIAN; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("bg"))                        return CCDBLanguage.BULGARIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("bul"))                       return CCDBLanguage.BULGARIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("български език"))            return CCDBLanguage.BULGARIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Bulgarian"))                 return CCDBLanguage.BULGARIAN; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("ru"))                        return CCDBLanguage.RUSSIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("rus"))                       return CCDBLanguage.RUSSIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("русский"))                   return CCDBLanguage.RUSSIAN; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Russian"))                   return CCDBLanguage.RUSSIAN; //$NON-NLS-1$

		if (langval.equalsIgnoreCase("zh"))                        return CCDBLanguage.CHINESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("zho"))                       return CCDBLanguage.CHINESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("chi"))                       return CCDBLanguage.CHINESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("\u4e2d\u6587"))              return CCDBLanguage.CHINESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Zhōngwén"))                  return CCDBLanguage.CHINESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Zhongwen"))                  return CCDBLanguage.CHINESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("\u6c49\u8bed"))              return CCDBLanguage.CHINESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("\u6f22\u8a9e"))              return CCDBLanguage.CHINESE; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Chinese"))                   return CCDBLanguage.CHINESE; //$NON-NLS-1$

		throw new InnerMediaQueryException("Unknown audio language '" + langval + "'"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static boolean isNullLanguage(String langval) {
		if (Str.isNullOrWhitespace(langval)) return true;
		if (langval.equalsIgnoreCase("Undefined")) return true; //$NON-NLS-1$
		if (langval.equalsIgnoreCase("Keine Angabe")) return true; //$NON-NLS-1$

		return false;
	}
}
