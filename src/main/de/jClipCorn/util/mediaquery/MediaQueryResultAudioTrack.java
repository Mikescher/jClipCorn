package de.jClipCorn.util.mediaquery;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.util.exceptions.InnerMediaQueryException;

import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

public class MediaQueryResultAudioTrack {
	public final String Format;      // NULL if not set
	public final String Language;    // NULL if not set
	public final String CodecID;     // NULL if not set
	public final short Channels;     // -1 if not set
	public final int Samplingrate;   // -1 if not set
	public final int BitRate;        // -1 if not set
	public final boolean Default;

	private MediaQueryResultAudioTrack(String format, String language, String codecID, short channels, int samplerate, int bitrate, boolean aDefault) {
		Format = format;
		Language = MediaQueryResult.isNullLanguage(language) ? null : language;
		CodecID = codecID;
		Channels = channels;
		Samplingrate = samplerate;
		BitRate = bitrate;
		Default = aDefault;
	}

	@SuppressWarnings("nls")
	public static MediaQueryResultAudioTrack parse(CCXMLElement xml) throws InnerMediaQueryException, CCXMLException {
		String format;
		String language;
		String codecID;
		short channels;
		int srate;
		int brate;
		boolean adefault;

		format   = xml.getFirstChildValueOrDefault("Format", null);
		language = xml.getFirstChildValueOrDefault("Language", null);
		codecID  = xml.getFirstChildValueOrDefault("CodecID", null);
		channels = (short)xml.getFirstChildIntValueOrDefault("Channels", -1);
		srate    = xml.getFirstChildIntValueOrDefault("SamplingRate", -1);
		brate    = xml.getFirstChildIntValueOrDefault("BitRate", -1);
		adefault = MediaQueryResult.parseBool(xml.getFirstChildValueOrDefault("Default", "No"));

		return new MediaQueryResultAudioTrack(format, language, codecID, channels, srate, brate, adefault);
	}

	@SuppressWarnings("nls")
	public CCDBLanguage getLanguage() throws InnerMediaQueryException {
		return MediaQueryResult.getLanguage(Language);
	}
}
