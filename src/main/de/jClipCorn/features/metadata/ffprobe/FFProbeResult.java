package de.jClipCorn.features.metadata.ffprobe;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;

public class FFProbeResult {
	public final String Raw;

	public final long CDate;
	public final long MDate;
	public final long FileSize;        // -1 if not set

	public final String Checksum;      // NULL if not set

	public final String CodecName;     // NULL if not set
	public final String CodecLongName; // NULL if not set
	public final String Profile;       // NULL if not set

	public final int Width;            // -1 if not set
	public final int Height;           // -1 if not set

	public final short BitDepth;       // -1 if not set
	public final int FrameCount;       // -1 if not set
	public final double FrameRate;     // -1 if not set

	public final double Duration;      // -1 if not set
	public final int Bitrate;          // -1 if not set

	public final String AudioCodecName;     // NULL if not set
	public final String AudioCodecLongName; // NULL if not set
	public final int AudioSampleRate;       // -1 if not set
	public final short AudioChannels;       // -1 if not set

	public FFProbeResult(String raw,
						 long cdate, long mdate, long fsize,
						 String codecName, String codecLongName, String profile, int width, int height,
						 short bitsPerRawSample, int frameCount, double frameRate,
						 double duration, int bitrate,
						 String aCodecName, String aCodecLongName, int aSampleRate, short aChannels,
						 String checksum)
	{
		Raw                = raw;
		CDate              = cdate;
		MDate              = mdate;
		FileSize           = fsize;
		CodecName          = codecName;
		CodecLongName      = codecLongName;
		Profile            = profile;
		Width              = width;
		Height             = height;
		BitDepth           = bitsPerRawSample;
		FrameCount         = frameCount;
		FrameRate          = frameRate;
		Duration           = duration;
		Bitrate            = bitrate;
		AudioCodecName     = aCodecName;
		AudioCodecLongName = aCodecLongName;
		AudioSampleRate    = aSampleRate;
		AudioChannels      = aChannels;
		Checksum           = checksum;
	}

	public PartialMediaInfo toPartial() {
		PartialMediaInfo pmi = new PartialMediaInfo();
		pmi.RawOutput = Opt.of(Raw);

		pmi.CreationDate     = (CDate == -1) ? Opt.empty() : Opt.of(CDate);
		pmi.ModificationDate = (MDate == -1) ? Opt.empty() : Opt.of(MDate);
		pmi.Filesize         = (FileSize == -1) ? Opt.empty() : Opt.of(new CCFileSize(FileSize));
		pmi.Duration         = (Duration == -1) ? Opt.empty() : Opt.of(Duration);
		pmi.Bitrate          = (Bitrate == -1) ? Opt.empty() : Opt.of(Bitrate);
		pmi.VideoFormat      = Opt.empty();
		pmi.PixelSize        = (Width == -1 || Height == -1) ? Opt.empty() : Opt.of(Tuple.Create(Width, Height));
		pmi.Framerate        = (FrameRate == -1) ? Opt.empty() : Opt.of(FrameRate);
		pmi.Bitdepth         = (BitDepth == -1) ? Opt.empty() : Opt.of(BitDepth);
		pmi.FrameCount       = (FrameCount == -1) ? Opt.empty() : Opt.of(FrameCount);
		pmi.VideoCodec       = Opt.empty();
		pmi.AudioFormat      = Opt.empty();
		pmi.AudioChannels    = (AudioChannels == -1) ? Opt.empty() : Opt.of(AudioChannels);
		pmi.AudioCodec       = Opt.empty();
		pmi.AudioSamplerate  = (AudioSampleRate == -1) ? Opt.empty() : Opt.of(AudioSampleRate);
		pmi.Checksum         = Str.isNullOrWhitespace(Checksum) ? Opt.empty() : Opt.of(Checksum);

		return pmi;
	}
}
