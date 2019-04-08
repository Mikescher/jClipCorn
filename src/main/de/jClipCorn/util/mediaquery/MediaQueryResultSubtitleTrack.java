package de.jClipCorn.util.mediaquery;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.util.exceptions.InnerMediaQueryException;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

public class MediaQueryResultSubtitleTrack {
	public final String Format;    // NULL if not set
	public final String Title;     // NULL if not set
	public final String Language;  // NULL if not set
	public final String CodecID;   // NULL if not set
	public final boolean Default;

	private MediaQueryResultSubtitleTrack(String format, String title, String codecID, String language, boolean aDefault) {
		Format = format;
		Title = title;
		Language = language;
		CodecID = codecID;
		Default = aDefault;
	}

	@SuppressWarnings("nls")
	public static MediaQueryResultSubtitleTrack parse(CCXMLElement xml) throws InnerMediaQueryException, CCXMLException {
		String format;
		String title;
		String codecID;
		String language;
		boolean adefault;

		format   = xml.getFirstChildValueOrDefault("Format", null);
		title    = xml.getFirstChildValueOrDefault("Title", null);
		codecID  = xml.getFirstChildValueOrDefault("CodecID", null);
		language = xml.getFirstChildValueOrDefault("Language", null);
		adefault = MediaQueryResult.parseBool(xml.getFirstChildValueOrDefault("Default", "No"));

		return new MediaQueryResultSubtitleTrack(format, title, codecID, language, adefault);
	}

	@SuppressWarnings("nls")
	public CCDBLanguage getLanguage() throws InnerMediaQueryException {
		return MediaQueryResult.getLanguage(Language);
	}
}
