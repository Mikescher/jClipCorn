package de.jClipCorn.features.metadata;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.helper.ChecksumHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PartialMediaInfo {

	public final static PartialMediaInfo EMPTY = new PartialMediaInfo
	(
		Opt.empty(),
		Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(),
		Opt.empty(), Opt.empty(),
		Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(),
		Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty()
	);

	public final Opt<String> RawOutput;

	public final Opt<Long>       CDate;
	public final Opt<Long>       MDate;
	public final Opt<CCFileSize> Filesize;
	public final Opt<String>     Checksum;

	public final Opt<Double>  Duration; // seconds
	public final Opt<Integer> Bitrate;

	public final Opt<String>  VideoFormat;
	public final Opt<Integer> Width;
	public final Opt<Integer> Height;
	public final Opt<Double>  Framerate;
	public final Opt<Short>   Bitdepth;
	public final Opt<Integer> Framecount;
	public final Opt<String>  VideoCodec;

	public final Opt<String>  AudioFormat;
	public final Opt<Short>   AudioChannels;
	public final Opt<String>  AudioCodec;
	public final Opt<Integer> AudioSamplerate;

	private PartialMediaInfo(Opt<String> rawOutput,
							 Opt<Long> cdate, Opt<Long> mdate, Opt<CCFileSize> filesize, Opt<String> checksum,
							 Opt<Double> duration, Opt<Integer> bitrate,
							 Opt<String> videoFormat, Opt<Integer> width, Opt<Integer> height, Opt<Double> framerate, Opt<Short> bitdepth, Opt<Integer> framecount, Opt<String> videoCodec,
							 Opt<String> audioFormat, Opt<Short> audioChannels, Opt<String> audioCodec, Opt<Integer> audioSamplerate)
	{
		RawOutput        = rawOutput;
		CDate            = cdate;
		MDate            = mdate;
		Filesize         = filesize;
		Checksum         = checksum;
		Duration         = duration;
		Bitrate          = bitrate;
		VideoFormat      = videoFormat;
		Width            = width;
		Height           = height;
		Framerate        = framerate;
		Bitdepth         = bitdepth;
		Framecount       = framecount;
		VideoCodec       = videoCodec;
		AudioFormat      = audioFormat;
		AudioChannels    = audioChannels;
		AudioCodec       = audioCodec;
		AudioSamplerate  = audioSamplerate;
	}

	public static PartialMediaInfo create(Opt<String> rawOutput,
										  Opt<Long> creationDate, Opt<Long> modificationDate, Opt<CCFileSize> filesize, Opt<String> checksum,
										  Opt<Double> duration, Opt<Integer> bitrate,
										  Opt<String> videoFormat, Opt<Integer> width, Opt<Integer> height, Opt<Double> framerate, Opt<Short> bitdepth, Opt<Integer> frameCount, Opt<String> videoCodec,
										  Opt<String> audioFormat, Opt<Short> audioChannels, Opt<String> audioCodec, Opt<Integer> audioSamplerate)
	{
		return new PartialMediaInfo
		(
			rawOutput,
			creationDate, modificationDate, filesize, checksum,
			duration, bitrate,
			videoFormat, width, height, framerate, bitdepth, frameCount, videoCodec,
			audioFormat, audioChannels, audioCodec, audioSamplerate
		);
	}

	public CCMediaInfo toMediaInfo()
	{
		return toMediaInfo(new RefParam<>());
	}

	private CCMediaInfo toMediaInfo(RefParam<List<String>> valErr)
	{
		return CCMediaInfo.create
		(
			CDate.orElse(-1L),
			MDate.orElse(-1L),
			Filesize.orElse(CCFileSize.ZERO),
			Checksum.orElse(Str.Empty),
			Duration.orElse(-1.0),
			Bitrate.orElse(-1),
			VideoFormat.orElse(Str.Empty),
			Width.orElse(-1),
			Height.orElse(-1),
			Framerate.orElse(-1.0),
			Bitdepth.orElse((short)-1),
			Framecount.orElse(-1),
			VideoCodec.orElse(Str.Empty),
			AudioFormat.orElse(Str.Empty),
			AudioChannels.orElse((short)-1),
			AudioCodec.orElse(Str.Empty),
			AudioSamplerate.orElse(-1),
			valErr
		);
	}

	public List<String> validate()
	{
		return validate(true);
	}

	@SuppressWarnings("nls")
	public List<String> validate(boolean additional)
	{
		var validation = new RefParam<List<String>>(null);
		toMediaInfo(validation);

		if (validation.Value != null) return validation.Value;

		var r = new ArrayList<String>();

		if (additional)
		{

			if (Bitdepth.isPresent() && Bitdepth.get() != 8 && Bitdepth.get() != 10 && Bitdepth.get() != 12) r.add("Bitdepth");
			if (Checksum.isPresent() && !ChecksumHelper.isValidVideoHash(Checksum.get())) r.add("Checksum");
		}

		return r;
	}

	public PartialMediaInfo WithChecksum(Opt<String> new_cs)
	{
		return new PartialMediaInfo
		(
			RawOutput,
			CDate, MDate, Filesize, new_cs,
			Duration, Bitrate,
			VideoFormat, Width, Height, Framerate, Bitdepth, Framecount, VideoCodec,
			AudioFormat, AudioChannels, AudioCodec, AudioSamplerate
		);
	}

	public boolean isEqual(PartialMediaInfo that)
	{
		if (that == null) return false;

		if (!this.CDate.isEqual(that.CDate, Objects::equals)) return false;
		if (!this.MDate.isEqual(that.MDate, Objects::equals)) return false;
		if (!this.Filesize.isEqual(that.Filesize, CCFileSize::equals)) return false;
		if (!this.Checksum.isEqual(that.Checksum, Str::equals)) return false;

		if (!this.Duration.isEqual(that.Duration, (a,b) -> Double.compare(a,b) == 0)) return false;
		if (!this.Bitrate.isEqual(that.Bitrate, Objects::equals)) return false;

		if (!this.VideoFormat.isEqual(that.VideoFormat, Str::equals)) return false;
		if (!this.Width.isEqual(that.Width, Objects::equals)) return false;
		if (!this.Height.isEqual(that.Height, Objects::equals)) return false;
		if (!this.Framerate.isEqual(that.Framerate, (a,b) -> Double.compare(a,b) == 0)) return false;
		if (!this.Bitdepth.isEqual(that.Bitdepth, Objects::equals)) return false;
		if (!this.Framecount.isEqual(that.Framecount, Objects::equals)) return false;
		if (!this.VideoCodec.isEqual(that.VideoCodec, Str::equals)) return false;

		if (!this.AudioFormat.isEqual(that.AudioFormat, Str::equals)) return false;
		if (!this.AudioChannels.isEqual(that.AudioChannels, Objects::equals)) return false;
		if (!this.AudioCodec.isEqual(that.AudioCodec, Str::equals)) return false;
		if (!this.AudioSamplerate.isEqual(that.AudioSamplerate, Objects::equals)) return false;

		return true;
	}
}
