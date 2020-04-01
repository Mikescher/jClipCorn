package de.jClipCorn.features.metadata.mediaquery;

import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryException;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

public class MediaQueryResultVideoTrack {
	public final String Format;           // NULL if not set
	public final String Format_Profile;   // NULL if not set
	public final String CodecID;          // NULL if not set
	public final int BitRate;             // -1 if not set
	public final int BitRateNominal;      // -1 if not set
	public final int Width;
	public final int Height;
	public final double FrameRate;        // -1 if not set
	public final int FrameCount;          // -1 if not set
	public final short BitDepth;          // -1 if not set
	public final double Duration;         // -1 if not set
	public final boolean Default;

	private MediaQueryResultVideoTrack(String format, String format_Profile, String codecID, int bitRate, int bitRateNominal, int width, int height, double frameRate, int frameCount, short bitDepth, double duration, boolean vdefault) {
		Format         = format;
		Format_Profile = format_Profile;
		CodecID        = codecID;
		BitRate        = bitRate;
		BitRateNominal = bitRateNominal;
		Width          = width;
		Height         = height;
		FrameRate      = frameRate;
		FrameCount     = frameCount;
		BitDepth       = bitDepth;
		Duration       = duration;
		Default        = vdefault;
	}

	@SuppressWarnings("nls")
	public static MediaQueryResultVideoTrack parse(CCXMLElement xml) throws CCXMLException, InnerMediaQueryException {
		String  format         = xml.getFirstChildValueOrDefault("Format", null);
		String  format_Profile = xml.getFirstChildValueOrDefault("Format_Profile", null);
		String  codecID        = xml.getFirstChildValueOrDefault("CodecID", null);
		int     bitRate        = xml.getFirstChildIntValueOrDefault("BitRate", -1);
		int     bitRateNominal = xml.getFirstChildValueOrDefault("BitRate_Nominal", "").contains("/") ? -1 : xml.getFirstChildIntValueOrDefault("BitRate_Nominal", -1);
		int     width          = xml.getFirstChildIntValueOrThrow("Width");
		int     height         = xml.getFirstChildIntValueOrThrow("Height");
		double  frameRate      = xml.getFirstChildDoubleValueOrDefault("FrameRate", -1);
		int     frameCount     = xml.getFirstChildIntValueOrDefault("FrameCount", -1);
		short   bitDepth       = (short)xml.getFirstChildIntValueOrDefault("BitDepth", -1);
		double  duration       = xml.getFirstChildDoubleValueOrDefault("Duration", -1);
		boolean vdefault       = MediaQueryResult.parseBool(xml.getFirstChildValueOrDefault("Default", "No"));

		return new MediaQueryResultVideoTrack(format,format_Profile, codecID, bitRate, bitRateNominal, width, height, frameRate, frameCount, bitDepth, duration, vdefault);
	}
}
