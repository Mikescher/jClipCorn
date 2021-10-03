package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.CCQualityCategoryType;
import de.jClipCorn.database.util.CCQualityResolutionType;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CCMediaInfo {
	public static final CCMediaInfo EMPTY = new CCMediaInfo();

	private final boolean _isEmpty;

	private final long cdate;           // FileAttr
	private final long mdate;           // FileAttr

	private final String checksum;      // CCFVH

	private final CCFileSize filesize;  // General -> FileSize
	private final double duration;      // General -> Duration             (seconds)

	private final int bitrate;          // (Video -> BitRate) + (Audio -> BitRate)

	private final String videoformat;   // Video -> Format
	private final int width;            // Video -> Width
	private final int height;           // Video -> Height
	private final double framerate;     // Video -> Framerate
	private final short bitdepth;       // Video -> BitDepth
	private final int framecount;       // Video -> FrameCount
	private final String videocodec;    // Video -> CodecID

	private final String audioformat;   // Audio -> Format
	private final short audiochannels;  // Audio -> Channels
	private final String audiocodec;    // Audio -> CodecID
	private final int audiosamplerate;  // Audio -> SamplingRate


	private CCQualityCategory _cat = null;
	private CCGenreList _cat_source = null;

	private CCMediaInfo(long cdate, long mdate, CCFileSize filesize, String checksum,
						double duration, int bitrate,
						String videoformat, int width, int height, double framerate, short bitdepth, int framecount,
						String videocodec, String audioformat, short audiochannels, String audiocodec, int audiosamplerate)
	{
		this._isEmpty = false;

		this.cdate           = cdate;
		this.mdate           = mdate;
		this.filesize        = filesize;
		this.checksum        = checksum;

		this.duration        = duration;
		this.bitrate         = bitrate;

		this.videoformat     = videoformat;
		this.width           = width;
		this.height          = height;
		this.framerate       = framerate;
		this.bitdepth        = bitdepth;
		this.framecount      = framecount;
		this.videocodec      = videocodec;

		this.audioformat     = audioformat;
		this.audiochannels   = audiochannels;
		this.audiocodec      = audiocodec;
		this.audiosamplerate = audiosamplerate;
	}

	private CCMediaInfo() {
		this._isEmpty = true;

		this.cdate           = -1;
		this.mdate           = -1;
		this.filesize        = CCFileSize.ZERO;
		this.checksum        = Str.Empty;

		this.duration        = -1;
		this.bitrate         = -1;

		this.videoformat     = Str.Empty;
		this.width           = -1;
		this.height          = -1;
		this.framerate       = -1;
		this.bitdepth        = -1;
		this.framecount      = -1;
		this.videocodec      = Str.Empty;

		this.audioformat     = Str.Empty;
		this.audiochannels   = -1;
		this.audiocodec      = Str.Empty;
		this.audiosamplerate = -1;
	}

	public static CCMediaInfo create(long cdate, long mdate, CCFileSize filesize, String checksum,
									 double duration, int bitrate,
									 String videoformat, int width, int height, double framerate, short bitdepth, int framecount, String videocodec,
									 String audioformat, short audiochannels, String audiocodec, int audiosamplerate)
	{
		return create
		(
			cdate, mdate, filesize, checksum,
			duration, bitrate,
			videoformat, width, height, framerate, bitdepth, framecount, videocodec,
			audioformat, audiochannels, audiocodec, audiosamplerate,
			new RefParam<>());
	}

	@SuppressWarnings("nls")
	public static CCMediaInfo create(long cdate, long mdate, CCFileSize filesize, String checksum,
									 double duration, int bitrate,
									 String videoformat, int width, int height, double framerate, short bitdepth, int framecount, String videocodec,
									 String audioformat, short audiochannels, String audiocodec, int audiosamplerate,
									 RefParam<List<String>> valErr)
	{

		var errs = new ArrayList<String>();

		if (cdate <= 0)                     errs.add("CDate");
		if (mdate <= 0)                     errs.add("MDate");
		if (filesize.getBytes() <= 0)       errs.add("Filesize");
		if (Str.isNullOrEmpty(checksum))    errs.add("Checksum");

		if (duration <= 0)                  errs.add("Duration");
		if (bitrate <= 0)                   errs.add("Bitrate");

		if (Str.isNullOrEmpty(videoformat)) errs.add("VideoFormat");
		if (width <= 0)                     errs.add("Width");
		if (height <= 0)                    errs.add("Height");
		if (framerate <= 0)                 errs.add("Framerate");
		if (bitdepth <= 0)                  errs.add("Bitdepth");
		if (framecount <= 0)                errs.add("Framecount");
		if (Str.isNullOrEmpty(videocodec))  errs.add("VideoCodec");

		if (Str.isNullOrEmpty(audioformat)) errs.add("AudioFormat");
		if (audiochannels <= 0)             errs.add("AudioChannels");
		if (Str.isNullOrEmpty(audiocodec))  errs.add("AudioCodec");
		if (audiosamplerate <= 0)           errs.add("AudioSamplerate");

		if (errs.size() > 0) { valErr.Value = errs; return CCMediaInfo.EMPTY; }

		return new CCMediaInfo
		(
			cdate, mdate, filesize, checksum,
			duration, bitrate,
			videoformat, width, height, framerate, bitdepth, framecount, videocodec,
			audioformat, audiochannels, audiocodec, audiosamplerate
		);
	}

	public boolean isSet() {
		return !_isEmpty;
	}

	public boolean isUnset() {
		return _isEmpty;
	}

	public long getCDate() {
		return cdate;
	}

	public long getMDate() {
		return mdate;
	}

	public CCFileSize getFilesize() {
		return filesize;
	}

	public double getDuration() {
		return duration;
	}

	public int getDurationInMinutes() {
		return (int)Math.round(duration / 60);
	}

	public int getBitrate() {
		return bitrate;
	}

	public String getVideoFormat() {
		return videoformat;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getFramerate() {
		return framerate;
	}

	public short getBitdepth() {
		return bitdepth;
	}

	public int getFramecount() {
		return framecount;
	}

	public String getVideoCodec() {
		return videocodec;
	}

	public String getAudioFormat() {
		return audioformat;
	}

	public short getAudioChannels() {
		return audiochannels;
	}

	public String getAudioCodec() {
		return audiocodec;
	}

	public int getAudioSamplerate() {
		return audiosamplerate;
	}

	public String getChecksum() {
		return checksum;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CCMediaInfo that = (CCMediaInfo) o;

		return isEqual(that);
	}

	public boolean isEqual(CCMediaInfo that) {
		if (that == null) return false;

		if (cdate != that.cdate) return false;
		if (mdate != that.mdate) return false;
		if (!CCFileSize.isEqual(filesize, that.filesize)) return false;
		if (!Objects.equals(checksum, that.checksum)) return false;

		if (Double.compare(that.duration, duration) != 0) return false;
		if (bitrate != that.bitrate) return false;

		if (!Objects.equals(videoformat, that.videoformat)) return false;
		if (width != that.width) return false;
		if (height != that.height) return false;
		if (Double.compare(that.framerate, framerate) != 0) return false;
		if (bitdepth != that.bitdepth) return false;
		if (framecount != that.framecount) return false;
		if (!Objects.equals(videocodec, that.videocodec)) return false;

		if (!Objects.equals(audioformat, that.audioformat)) return false;
		if (audiochannels != that.audiochannels) return false;
		if (!Objects.equals(audiocodec, that.audiocodec)) return false;
		if (audiosamplerate != that.audiosamplerate) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = (int) (cdate ^ (cdate >>> 32));
		result = 31 * result + (int) (mdate ^ (mdate >>> 32));
		result = 31 * result + (int) (filesize.getBytes() ^ (filesize.getBytes() >>> 32));
		temp = Double.doubleToLongBits(duration);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + bitrate;
		result = 31 * result + (videoformat != null ? videoformat.hashCode() : 0);
		result = 31 * result + width;
		result = 31 * result + height;
		temp = Double.doubleToLongBits(framerate);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + bitdepth;
		result = 31 * result + framecount;
		result = 31 * result + (videocodec != null ? videocodec.hashCode() : 0);
		result = 31 * result + (audioformat != null ? audioformat.hashCode() : 0);
		result = 31 * result + audiochannels;
		result = 31 * result + (audiocodec != null ? audiocodec.hashCode() : 0);
		result = 31 * result + audiosamplerate;
		result = 31 * result + (checksum != null ? checksum.hashCode() : 0);
		return result;
	}

	public CCQualityCategory getCategory(CCGenreList source) {

		if (_cat != null && _cat_source == source) return _cat;

		if (!isSet()) { _cat_source=source; return _cat = CCQualityCategory.UNSET; }

		CCQualityCategoryType ct = getCategoryType(source != null && source.shouldIgnoreBitrateInMediaInfo());
		CCQualityResolutionType rt = getCategoryResType();
		String tx2 = getCategoryTextLong();
		String tx3 = getCategoryToolTip();

		_cat_source=source;
		return _cat = new CCQualityCategory(ct, rt, tx2, tx3, bitrate);
	}

	@SuppressWarnings("nls")
	private String getCategoryToolTip() {
		StringBuilder b = new StringBuilder();
		b.append("<html>");
		b.append("Resolution: ").append(width).append(" x ").append(height).append("<br/>");
		b.append("Bitrate: ").append(Str.spacegroupformat((int) Math.round(bitrate / 1000.0))).append(" kbit/s").append("<br/>");
		b.append("Duration: ").append(TimeIntervallFormatter.formatSeconds(duration)).append("<br/>");
		b.append("Framerate: ").append(Math.round(framerate)).append(" fps").append("<br/>");
		b.append("</html>");
		return b.toString();
	}

	@SuppressWarnings("nls")
	private String getCategoryTextLong() {
		return Str.format("{0,number,#}x{1,number,#} @ {2} FPS ({3,number,#} kbit/s)", width, height, getNormalizedFPS(), getNormalizedBitrate());
	}

	public int getNormalizedFPS() {
		return (int)Math.round(framerate);
	}

	public int getNormalizedBitrate() {
		return (int)Math.round(bitrate / 1000.0);
	}

	public int getNormalizedDuration() {
		return (int)Math.round(duration);
	}

	private CCQualityCategoryType getCategoryType(boolean ignoreBitrate)
	{
		if (ignoreBitrate) return CCQualityCategoryType.min(getCategoryTypeByFramerate(), getCategoryTypeByResolution());
		return CCQualityCategoryType.min(getCategoryTypeByFramerate(), getCategoryTypeByBitrate(), getCategoryTypeByResolution());
	}

	private CCQualityCategoryType getCategoryTypeByFramerate()
	{
		if (framerate <  20) return CCQualityCategoryType.LOW_QUALITY;
		if (framerate <  23) return CCQualityCategoryType.VERY_GOOD;

		return CCQualityCategoryType.HIGH_DEFINITION;
	}

	private CCQualityCategoryType getCategoryTypeByBitrate()
	{
		if (bitrate <  900 * 1000) return CCQualityCategoryType.LOW_QUALITY;
		if (bitrate < 1400 * 1000) return CCQualityCategoryType.OKAY;
		if (bitrate < 1900 * 1000) return CCQualityCategoryType.GOOD;
		if (bitrate < 2500 * 1000) return CCQualityCategoryType.VERY_GOOD;

		return CCQualityCategoryType.HIGH_DEFINITION;
	}

	private CCQualityCategoryType getCategoryTypeByResolution()
	{
		if (width*height <   70000) return CCQualityCategoryType.LOW_QUALITY;
		if (width*height <  300000) return CCQualityCategoryType.OKAY;
		if (width*height <  900000) return CCQualityCategoryType.GOOD;
		if (width*height < 1536000) return CCQualityCategoryType.VERY_GOOD;

		return CCQualityCategoryType.HIGH_DEFINITION;
	}

	private CCQualityResolutionType getCategoryResType()
	{
		if (width < 320 && height < 240) return CCQualityResolutionType.R_LOW;

		if (isApproxSize(3840, 2160, 0.05, 0.45)) return CCQualityResolutionType.R_4K;
		if (isApproxSize(2560, 1440, 0.05, 0.45)) return CCQualityResolutionType.R_1440;
		if (isApproxSize(1920, 1080, 0.05, 0.45)) return CCQualityResolutionType.R_1080;
		if (isApproxSize(1280,  720, 0.10, 0.50)) return CCQualityResolutionType.R_720;
		if (isApproxSize(1024,  576, 0.10, 0.50)) return CCQualityResolutionType.R_576;
		if (isApproxSize( 854,  480, 0.10, 0.50)) return CCQualityResolutionType.R_480;
		if (isApproxSize( 320,  240, 0.15, 0.50)) return CCQualityResolutionType.R_240;
		if (isApproxSize( 720,  304, 0.30, 0.65)) return CCQualityResolutionType.R_304;

		if (width < 320 || height < 240) return CCQualityResolutionType.R_LOW;

		return CCQualityResolutionType.OTHER;
	}

	private boolean isApproxSize(int w, int h, double near, double far)
	{
		double dw = Math.abs(width - w) / (Math.max(width, w) * 1d);
		double dh = Math.abs(height - h) / (Math.max(height, h) * 1d);

		if (dw <= near && dh < far) return true;
		if (dw <= far && dh < near) return true;

		return false;
	}

	public PartialMediaInfo toPartial() {
		if (_isEmpty) return PartialMediaInfo.EMPTY;

		return PartialMediaInfo.create
		(
			Opt.empty(),
			Opt.of(cdate), Opt.of(mdate), Opt.of(filesize), Opt.of(checksum),
			Opt.of(duration), Opt.of(bitrate),
			Opt.of(videoformat), Opt.of(width), Opt.of(height), Opt.of(framerate), Opt.of(bitdepth), Opt.of(framecount), Opt.of(videocodec),
			Opt.of(audioformat), Opt.of(audiochannels), Opt.of(audiocodec), Opt.of(audiosamplerate)
		);
	}
}
