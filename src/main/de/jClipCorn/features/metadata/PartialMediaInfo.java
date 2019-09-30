package de.jClipCorn.features.metadata;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;

public class PartialMediaInfo {

	public Opt<String> RawOutput = Opt.empty();

	public Opt<Long>       CreationDate     = Opt.empty();
	public Opt<Long>       ModificationDate = Opt.empty();
	public Opt<CCFileSize> Filesize         = Opt.empty();

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

}
