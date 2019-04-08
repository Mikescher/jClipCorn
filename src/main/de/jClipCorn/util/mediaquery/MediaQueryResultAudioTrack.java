package de.jClipCorn.util.mediaquery;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.util.exceptions.InnerMediaQueryException;

import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

public class MediaQueryResultAudioTrack {
	public final String Format;   // NULL if not set
	public final String Language; // NULL if not set
	public final String CodecID;  // NULL if not set
	public final boolean Default;

	private MediaQueryResultAudioTrack(String format, String language, String codecID, boolean aDefault) {
		Format = format;
		Language = language;
		CodecID = codecID;
		Default = aDefault;
	}

	@SuppressWarnings("nls")
	public static MediaQueryResultAudioTrack parse(CCXMLElement xml) throws InnerMediaQueryException, CCXMLException {
		String format;
		String language;
		String codecID;
		boolean adefault;

		format = xml.getFirstChildValueOrDefault("Format", null);
		language = xml.getFirstChildValueOrDefault("Language", null);
		codecID = xml.getFirstChildValueOrDefault("CodecID", null);
		adefault = MediaQueryResult.parseBool(xml.getFirstChildValueOrDefault("Default", "No"));

		return new MediaQueryResultAudioTrack(format, language, codecID, adefault);
	}

	@SuppressWarnings("nls")
	public CCDBLanguage getLanguage() throws InnerMediaQueryException {
		return MediaQueryResult.getLanguage(Language);
	}
}
