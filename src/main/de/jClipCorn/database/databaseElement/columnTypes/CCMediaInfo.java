package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.CCQualityCategoryType;
import de.jClipCorn.database.util.CCQualityResolutionType;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.ChecksumHelper;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CCMediaInfo {

	public final static CCMediaInfo EMPTY = new CCMediaInfo
	(
		Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(),
		Opt.empty(), Opt.empty(),
		Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(),
		Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty()
	);

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

	private Boolean                _isFullySet   = null;
	private Boolean                _isFullyEmpty = null;
	private Opt<CCQualityCategory> _cat          = null;
	private CCGenreList            _cat_source   = null;

	private CCMediaInfo(Opt<Long> cdate, Opt<Long> mdate, Opt<CCFileSize> filesize, Opt<String> checksum,
						Opt<Double> duration, Opt<Integer> bitrate,
						Opt<String> videoFormat, Opt<Integer> width, Opt<Integer> height, Opt<Double> framerate, Opt<Short> bitdepth, Opt<Integer> framecount, Opt<String> videoCodec,
						Opt<String> audioFormat, Opt<Short> audioChannels, Opt<String> audioCodec, Opt<Integer> audioSamplerate)
	{
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

	public static CCMediaInfo create(Opt<Long> creationDate, Opt<Long> modificationDate, Opt<CCFileSize> filesize, Opt<String> checksum,
									 Opt<Double> duration, Opt<Integer> bitrate,
									 Opt<String> videoFormat, Opt<Integer> width, Opt<Integer> height, Opt<Double> framerate, Opt<Short> bitdepth, Opt<Integer> frameCount, Opt<String> videoCodec,
									 Opt<String> audioFormat, Opt<Short> audioChannels, Opt<String> audioCodec, Opt<Integer> audioSamplerate)
	{
		return new CCMediaInfo
		(
			creationDate, modificationDate, filesize, checksum,
			duration, bitrate,
			videoFormat, width, height, framerate, bitdepth, frameCount, videoCodec,
			audioFormat, audioChannels, audioCodec, audioSamplerate
		);
	}

	public static CCMediaInfo create(long creationDate, long modificationDate, CCFileSize filesize, String checksum,
									 double duration, int bitrate,
									 String videoFormat, int width, int height, Double framerate, short bitdepth, int frameCount, String videoCodec,
									 String audioFormat, short audioChannels, String audioCodec, int audioSamplerate)
	{
		return new CCMediaInfo
		(
			Opt.of(creationDate), Opt.of(modificationDate), Opt.of(filesize), Opt.of(checksum),
			Opt.of(duration), Opt.of(bitrate),
			Opt.of(videoFormat), Opt.of(width), Opt.of(height), Opt.of(framerate), Opt.of(bitdepth), Opt.of(frameCount), Opt.of(videoCodec),
			Opt.of(audioFormat), Opt.of(audioChannels), Opt.of(audioCodec), Opt.of(audioSamplerate)
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

		if (validation.Value != null) return validation.Value;

		var r = new ArrayList<String>();

		if (additional)
		{

			if (Bitdepth.isPresent() && Bitdepth.get() != 8 && Bitdepth.get() != 10 && Bitdepth.get() != 12) r.add("Bitdepth");
			if (Checksum.isPresent() && !ChecksumHelper.isValidVideoHash(Checksum.get())) r.add("Checksum");
		}

		return r;
	}

	public CCMediaInfo WithChecksum(Opt<String> new_cs)
	{
		return new CCMediaInfo
		(
			CDate, MDate, Filesize, new_cs,
			Duration, Bitrate,
			VideoFormat, Width, Height, Framerate, Bitdepth, Framecount, VideoCodec,
			AudioFormat, AudioChannels, AudioCodec, AudioSamplerate
		);
	}

	public boolean isEqual(CCMediaInfo that)
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

	public boolean isFullySet() {
		if (_isFullySet != null) return _isFullySet;

		var result =
			CDate           .isPresent() &&
			MDate           .isPresent() &&
			Filesize        .isPresent() &&
			Checksum        .isPresent() &&
			Duration        .isPresent() &&
			Bitrate         .isPresent() &&
			VideoFormat     .isPresent() &&
			Width           .isPresent() &&
			Height          .isPresent() &&
			Framerate       .isPresent() &&
			Bitdepth        .isPresent() &&
			Framecount      .isPresent() &&
			VideoCodec      .isPresent() &&
			AudioFormat     .isPresent() &&
			AudioChannels   .isPresent() &&
			AudioCodec      .isPresent() &&
			AudioSamplerate .isPresent();

		return _isFullySet = result;
	}

	public boolean isFullyEmpty() {
		if (_isFullyEmpty != null) return _isFullyEmpty;

		var result =
				CDate.           isEmpty() &&
						MDate.           isEmpty() &&
						Filesize.        isEmpty() &&
						Checksum.        isEmpty() &&
						Duration.        isEmpty() &&
						Bitrate.         isEmpty() &&
						VideoFormat.     isEmpty() &&
						Width.           isEmpty() &&
						Height.          isEmpty() &&
						Framerate.       isEmpty() &&
						Bitdepth.        isEmpty() &&
						Framecount.      isEmpty() &&
						VideoCodec.      isEmpty() &&
						AudioFormat.     isEmpty() &&
						AudioChannels.   isEmpty() &&
						AudioCodec.      isEmpty() &&
						AudioSamplerate. isEmpty();

		return _isFullyEmpty = result;
	}

	public boolean isPartiallySet() {
		return !isFullyEmpty() && ! isFullySet();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 37);

		hcb.append(CDate);
		hcb.append(MDate);
		hcb.append(Filesize);
		hcb.append(Checksum);

		hcb.append(Duration);
		hcb.append(Bitrate);

		hcb.append(VideoFormat);
		hcb.append(Width);
		hcb.append(Height);
		hcb.append(Framerate);
		hcb.append(Bitdepth);
		hcb.append(Framecount);
		hcb.append(VideoCodec);

		hcb.append(AudioFormat);
		hcb.append(AudioChannels);
		hcb.append(AudioCodec);
		hcb.append(AudioSamplerate);

		return hcb.toHashCode();
	}

	public Opt<Integer> getDurationInMinutes() {
		return Duration.map(p -> (int)Math.round(p / 60));
	}

	public Opt<CCQualityCategory> getCategory(CCGenreList source) {

		if (_cat != null && _cat_source == source) return _cat;

		var ct = getCategoryType(source != null && source.shouldIgnoreBitrateInMediaInfo());
		var rt = getCategoryResType();

		if (ct.isEmpty() || rt.isEmpty() || Bitrate.isEmpty())
		{
			_cat_source=source;
			return _cat = Opt.empty();
		}

		String tx2 = getCategoryTextLong();
		String tx3 = getCategoryToolTip();

		_cat_source=source;
		return _cat = Opt.of(new CCQualityCategory(ct.get(), rt.get(), tx2, tx3, Bitrate.get()));
	}

	@SuppressWarnings("nls")
	private String getCategoryToolTip() {
		StringBuilder b = new StringBuilder();

		b.append("<html>");
		b.append("Resolution: ").append(Width.map(Object::toString).orElse("-")).append(" x ").append(Height.map(Object::toString).orElse("-")).append("<br/>");
		b.append("Bitrate: ").append(Bitrate.map(p -> ((int) Math.round(p / 1000.0)) + " kbit/s").orElse("-")).append("<br/>");
		b.append("Duration: ").append(Duration.map(TimeIntervallFormatter::formatSeconds).orElse ("-")).append("<br/>");
		b.append("Framerate: ").append(Framerate.map(p -> Math.round(p) + " fps").orElse("-")).append("<br/>");
		b.append("</html>");

		return b.toString();
	}

	@SuppressWarnings("nls")
	private String getCategoryTextLong() {
		return Str.format("{0,number,#}x{1,number,#} @ {2} FPS ({3,number,#} kbit/s)", Width.orElse(0), Height.orElse(0), getNormalizedFPS().orElse(0), getNormalizedBitrate().orElse(0));
	}

	public Opt<Integer> getNormalizedFPS() {
		return Framerate.map(p -> (int)Math.round(p));
	}

	public Opt<Integer> getNormalizedBitrate() {
		return Bitrate.map(p -> (int)Math.round(p / 1000.0));
	}

	public Opt<Integer> getNormalizedDuration() {
		return Duration.map(p -> (int)Math.round(p));
	}

	private Opt<CCQualityCategoryType> getCategoryType(boolean ignoreBitrate)
	{
		var a = getCategoryTypeByFramerate();
		var b = getCategoryTypeByBitrate();
		var c = getCategoryTypeByResolution();

		if (ignoreBitrate) b = Opt.empty();

		if (a.isEmpty() && b.isEmpty() && c.isEmpty()) return Opt.empty();
		if (b.isEmpty() && c.isEmpty()) return a;
		if (a.isEmpty() && c.isEmpty()) return b;
		if (a.isEmpty() && b.isEmpty()) return c;
		if (a.isEmpty()) return Opt.of(CCQualityCategoryType.min(b.get(), c.get()));
		if (b.isEmpty()) return Opt.of(CCQualityCategoryType.min(a.get(), c.get()));
		if (c.isEmpty()) return Opt.of(CCQualityCategoryType.min(a.get(), b.get()));

		return Opt.of(CCQualityCategoryType.min(a.get(), b.get(), c.get()));
	}

	private Opt<CCQualityCategoryType> getCategoryTypeByFramerate()
	{
		if (Framerate.isEmpty()) return Opt.empty();

		if (Framerate.get() <  20) return Opt.of(CCQualityCategoryType.LOW_QUALITY);
		if (Framerate.get() <  23) return Opt.of(CCQualityCategoryType.VERY_GOOD);

		return Opt.of(CCQualityCategoryType.HIGH_DEFINITION);
	}

	private Opt<CCQualityCategoryType> getCategoryTypeByBitrate()
	{
		if (Bitrate.isEmpty()) return Opt.empty();

		if (Bitrate.get() <  900 * 1000) return Opt.of(CCQualityCategoryType.LOW_QUALITY);
		if (Bitrate.get() < 1400 * 1000) return Opt.of(CCQualityCategoryType.OKAY);
		if (Bitrate.get() < 1900 * 1000) return Opt.of(CCQualityCategoryType.GOOD);
		if (Bitrate.get() < 2500 * 1000) return Opt.of(CCQualityCategoryType.VERY_GOOD);

		return Opt.of(CCQualityCategoryType.HIGH_DEFINITION);
	}

	private Opt<CCQualityCategoryType> getCategoryTypeByResolution()
	{
		if (Width.isEmpty() || Height.isEmpty()) return Opt.empty();

		int width  = Width.get();
		int height = Height.get();

		if (width*height <   70000) return Opt.of(CCQualityCategoryType.LOW_QUALITY);
		if (width*height <  300000) return Opt.of(CCQualityCategoryType.OKAY);
		if (width*height <  900000) return Opt.of(CCQualityCategoryType.GOOD);
		if (width*height < 1536000) return Opt.of(CCQualityCategoryType.VERY_GOOD);

		return Opt.of(CCQualityCategoryType.HIGH_DEFINITION);
	}

	private Opt<CCQualityResolutionType> getCategoryResType()
	{
		if (Width.isEmpty() || Height.isEmpty()) return Opt.empty();

		int width  = Width.get();
		int height = Height.get();

		if (width < 320 && height < 240) return Opt.of(CCQualityResolutionType.R_LOW);

		if (isApproxSize(3840, 2160, 0.05, 0.45, width, height)) return Opt.of(CCQualityResolutionType.R_4K);
		if (isApproxSize(2560, 1440, 0.05, 0.45, width, height)) return Opt.of(CCQualityResolutionType.R_1440);
		if (isApproxSize(1920, 1080, 0.05, 0.45, width, height)) return Opt.of(CCQualityResolutionType.R_1080);
		if (isApproxSize(1280,  720, 0.10, 0.50, width, height)) return Opt.of(CCQualityResolutionType.R_720);
		if (isApproxSize(1024,  576, 0.10, 0.50, width, height)) return Opt.of(CCQualityResolutionType.R_576);
		if (isApproxSize( 854,  480, 0.10, 0.50, width, height)) return Opt.of(CCQualityResolutionType.R_480);
		if (isApproxSize( 320,  240, 0.15, 0.50, width, height)) return Opt.of(CCQualityResolutionType.R_240);
		if (isApproxSize( 720,  304, 0.30, 0.65, width, height)) return Opt.of(CCQualityResolutionType.R_304);

		if (width < 320 || height < 240) return Opt.of(CCQualityResolutionType.R_LOW);

		return Opt.of(CCQualityResolutionType.OTHER);
	}

	private static boolean isApproxSize(int w, int h, double near, double far, int width, int height)
	{
		double dw = Math.abs(width - w)  / (Math.max(width, w) * 1d);
		double dh = Math.abs(height - h) / (Math.max(height, h) * 1d);

		if (dw <= near && dh < far)  return true;
		if (dw <= far  && dh < near) return true;

		return false;
	}

	public Opt<Tuple<Integer, Integer>> getResolution() {
		if (Width.isEmpty() || Height.isEmpty()) return Opt.empty();
		return Opt.of(Tuple.Create(Width.get(), Height.get()));
	}
}
