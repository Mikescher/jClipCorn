package de.jClipCorn.util.mediaquery;

import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

public class MediaQueryResultVideoTrack {
	public final String Format;           // NULL if not set
	public final String Format_Profile;   // NULL if not set
	public final String CodecID;          // NULL if not set
	public final int BitRate;             // -1 if not set
	public final int Width;
	public final int Height;
	public final double FrameRate;        // -1 if not set
	public final int FrameCount;          // -1 if not set
	public final int BitDepth;            // -1 if not set
	public final double Duration;        // -1 if not set

	private MediaQueryResultVideoTrack(String format, String format_Profile, String codecID, int bitRate, int width, int height, double frameRate, int frameCount, int bitDepth, double duration) {
		Format = format;
		Format_Profile = format_Profile;
		CodecID = codecID;
		BitRate = bitRate;
		Width = width;
		Height = height;
		FrameRate = frameRate;
		FrameCount = frameCount;
		BitDepth = bitDepth;
		Duration = duration;
	}

	@SuppressWarnings("nls")
	public static MediaQueryResultVideoTrack parse(CCXMLElement xml) throws CCXMLException {
		String format;
		String format_Profile;
		String codecID;
		int bitRate;
		int width;
		int height;
		double frameRate;
		double duration;
		int frameCount;
		int bitDepth;

		format = xml.getFirstChildValueOrDefault("Format", null);
		format_Profile = xml.getFirstChildValueOrDefault("Format_Profile", null);
		codecID = xml.getFirstChildValueOrDefault("CodecID", null);
		bitRate = xml.getFirstChildIntValueOrDefault("BitRate", -1);
		width = xml.getFirstChildIntValueOrThrow("Width");
		height = xml.getFirstChildIntValueOrThrow("Height");
		frameRate = xml.getFirstChildDoubleValueOrDefault("FrameRate", -1);
		frameCount = xml.getFirstChildIntValueOrDefault("FrameCount", -1);
		bitDepth = xml.getFirstChildIntValueOrDefault("BitDepth", -1);
		duration = xml.getFirstChildDoubleValueOrDefault("Duration", -1);

		return new MediaQueryResultVideoTrack(format,format_Profile, codecID, bitRate, width, height, frameRate, frameCount, bitDepth, duration);
	}
}
