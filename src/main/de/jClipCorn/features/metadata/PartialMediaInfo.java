package de.jClipCorn.features.metadata;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;

public class PartialMediaInfo {

	public Opt<String> RawOutput = Opt.empty();

	public Opt<Long>       CreationDate     = Opt.empty();
	public Opt<Long>       ModificationDate = Opt.empty();
	public Opt<CCFileSize> Filesize         = Opt.empty();
	public Opt<String>     Checksum         = Opt.empty();

	public Opt<Double>  Duration = Opt.empty(); // seconds
	public Opt<Integer> Bitrate  = Opt.empty();

	public Opt<String>                  VideoFormat = Opt.empty();
	public Opt<Tuple<Integer, Integer>> PixelSize   = Opt.empty();
	public Opt<Double>                  Framerate   = Opt.empty();
	public Opt<Short>                   Bitdepth    = Opt.empty();
	public Opt<Integer>                 FrameCount  = Opt.empty();
	public Opt<String>                  VideoCodec  = Opt.empty();

	public Opt<String>  AudioFormat     = Opt.empty();
	public Opt<Short>   AudioChannels   = Opt.empty();
	public Opt<String>  AudioCodec      = Opt.empty();
	public Opt<Integer> AudioSamplerate = Opt.empty();

	public CCMediaInfo toMediaInfo()
	{
		return new CCMediaInfo
		(
				CreationDate.orElse(-1L),
				ModificationDate.orElse(-1L),
				Filesize.map(CCFileSize::getBytes).orElse(-1L),
				Duration.orElse(-1.0),
				Bitrate.orElse(-1),
				VideoFormat.orElse(Str.Empty),
				PixelSize.map(p -> p.Item1).orElse(-1),
				PixelSize.map(p -> p.Item2).orElse(-1),
				Framerate.orElse(-1.0),
				Bitdepth.orElse((short)-1),
				FrameCount.orElse(-1),
				VideoCodec.orElse(Str.Empty),
				AudioFormat.orElse(Str.Empty),
				AudioChannels.orElse((short)-1),
				AudioCodec.orElse(Str.Empty),
				AudioSamplerate.orElse(-1),
				Checksum.orElse(Str.Empty)
		);
	}
}
