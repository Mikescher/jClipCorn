package de.jClipCorn.features.metadata.mediaquery;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryException;

import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

public class MediaQueryResultAudioTrack {
	public final String Format;      // NULL if not set
	public final String Language;    // NULL if not set
	public final String CodecID;     // NULL if not set
	public final short Channels;     // -1 if not set
	public final int Samplingrate;   // -1 if not set
	public final int BitRate;        // -1 if not set
	public final int BitRateNominal; // -1 if not set
	public final boolean Default;

	private MediaQueryResultAudioTrack(String format, String language, String codecID, short channels, int samplerate, int bitrate, int bitrateNominal, boolean aDefault) {
		Format = format;
		Language = MediaQueryResult.isNullLanguage(language) ? null : language;
		CodecID = codecID;
		Channels = channels;
		Samplingrate = samplerate;
		BitRate = bitrate;
		BitRateNominal = bitrateNominal;
		Default = aDefault;
	}

	@SuppressWarnings("nls")
	public static MediaQueryResultAudioTrack parse(CCXMLElement xml) throws InnerMediaQueryException, CCXMLException {
		String  format   = xml.getFirstChildValueOrDefault("Format", null);
		String  language = xml.getFirstChildValueOrDefault("Language", null);
		String  codecID  = xml.getFirstChildValueOrDefault("CodecID", null);
		short   channels = (short)xml.getFirstChildIntValueOrDefault("Channels", -1);
		int     srate    = xml.getFirstChildIntValueOrDefault("SamplingRate", -1);
		int     brate    = xml.getFirstChildIntValueOrDefault("BitRate", -1);
		int     brateNom = xml.getFirstChildIntValueOrDefault("BitRate_Nominal", -1);
		boolean adefault = MediaQueryResult.parseBool(xml.getFirstChildValueOrDefault("Default", "No"));

		return new MediaQueryResultAudioTrack(format, language, codecID, channels, srate, brate, brateNom, adefault);
	}

	public CCDBLanguage getLanguage() throws InnerMediaQueryException {
		return MediaQueryResult.getLanguage(Language);
	}
}
