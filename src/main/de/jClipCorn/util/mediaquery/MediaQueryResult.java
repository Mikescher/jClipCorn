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

	private static Pattern REX_LANGUAGE_REPEAT = Pattern.compile("^\\s*(\\w+)(\\s*/\\s*\\1)+\\s*$");

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

		for (CCXMLElement track : xml.getAllChildren("track"))
		{
			String strtype = track.getAttributeValueOrThrow("type");
			switch (strtype) {
				case "General":
					foundGeneral = true;

					format = track.getFirstChildValueOrDefault("Format", null);
					format_Version = track.getFirstChildValueOrDefault("Format_Version", null);
					fileSize = track.getFirstChildLongValueOrThrow("FileSize");
					duration = track.getFirstChildDoubleValueOrDefault("Duration", -1);
					overallBitRate = track.getFirstChildIntValueOrDefault("OverallBitRate", -1);
					frameRate = track.getFirstChildDoubleValueOrDefault("FrameRate", -1);

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
		if (!foundVideo) throw new InnerMediaQueryException("No track 'Video' found");

		CCDBLanguageList alng = getLang(atracks);
		
		return new MediaQueryResult(format, format_Version, fileSize, duration, overallBitRate, frameRate, vtracks.get(0), vtracks, atracks, stracks, alng);
	}

	private static CCDBLanguageList getLang(List<MediaQueryResultAudioTrack> tcks) throws InnerMediaQueryException {

		if (tcks.size() == 1 && tcks.get(0).Language == null) return null;

		if (CCStreams.iterate(tcks).any(t -> t.Language == null))
		{
			String info = CCStreams.iterate(tcks).stringjoin(t -> t.Language==null ? "NULL": t.Language, ", "); //$NON-NLS-1$
			throw new InnerMediaQueryException("No audio language set in tracks ("+info+")");
		}

		HashSet<CCDBLanguage> lng = new HashSet<>();
		for (MediaQueryResultAudioTrack t : tcks) lng.add(t.getLanguage());
		return CCDBLanguageList.createDirect(lng);
	}

	@SuppressWarnings("nls")
	public static boolean parseBool(String value) throws InnerMediaQueryException {
		if (value.equals("Yes")) return true;
		if (value.equals("No")) return false;
		throw new InnerMediaQueryException("Unknown boolean := '" + value + "'");
	}

	@SuppressWarnings("nls")
	public static CCDBLanguage getLanguage(String langval) throws InnerMediaQueryException {
		if (langval == null) throw new InnerMediaQueryException("Audio language not set (null)");

		// https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes

		langval = langval.trim();

		Matcher m1 = REX_LANGUAGE_REPEAT.matcher(langval);
		if (m1.matches()) langval = m1.group(1).trim();

		if (langval.equalsIgnoreCase("de"))                        return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("deu"))                       return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("ger"))                       return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("German"))                    return CCDBLanguage.GERMAN;
		if (langval.equalsIgnoreCase("Deutsch"))                   return CCDBLanguage.GERMAN;

		if (langval.equalsIgnoreCase("en"))                        return CCDBLanguage.ENGLISH;
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

		throw new InnerMediaQueryException("Unknown audio language '" + langval + "'");
	}
}
