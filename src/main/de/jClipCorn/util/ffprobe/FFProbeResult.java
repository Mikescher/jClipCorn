package de.jClipCorn.util.ffprobe;

public class FFProbeResult {
	public final String Raw;

	public final String CodecName;     // NULL if not set
	public final String CodecLongName; // NULL if not set
	public final String Profile;       // NULL if not set

	public final int Width;            // -1 if not set
	public final int Height;           // -1 if not set

	public final int BitDepth;         // -1 if not set
	public final int FrameCount;       // -1 if not set
	public final double FrameRate;     // -1 if not set

	public FFProbeResult(String raw, String codecName, String codecLongName, String profile, int width, int height, int bitsPerRawSample, int frameCount, double frameRate) {
		Raw = raw;
		CodecName = codecName;
		CodecLongName = codecLongName;
		Profile = profile;
		Width = width;
		Height = height;
		BitDepth = bitsPerRawSample;
		FrameCount = frameCount;
		FrameRate = frameRate;
	}
}
