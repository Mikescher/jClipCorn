package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.CategoryType;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.Str;

import java.text.MessageFormat;
import java.util.Objects;

public class CCMediaInfo {
	public static final CCMediaInfo EMPTY = new CCMediaInfo();

	private final boolean isSet;

	private final long cdate;          // FileAttr
	private final long mdate;          // FileAttr

	private final long filesize;       // General -> FileSize
	private final double duration;     // General -> Duration

	private final int bitrate;         // (Video -> BitRate) + (Audio -> BitRate)

	private final String videoformat;  // Video -> Format
	private final int width;           // Video -> Width
	private final int height;          // Video -> Height
	private final double framerate;    // Video -> Framerate
	private final short bitdepth;      // Video -> BitDepth
	private final int framecount;      // Video -> FrameCount
	private final String videocodec;   // Video -> CodecID

	private final String audioformat;  // Audio -> Format
	private final short audiochannels; // Audio -> Channels
	private final String audiocodec;   // Audio -> CodecID
	private final int audiosamplerate; // Audio -> SamplingRate

	public CCMediaInfo(long cdate, long mdate, long filesize, double duration, int bitrate, String videoformat, int width,
					   int height, double framerate, short bitdepth, int framecount, String videocodec, String audioformat,
					   short audiochannels, String audiocodec, int audiosamplerate) {
		this.isSet = true;

		this.cdate           = cdate;
		this.mdate           = mdate;
		this.filesize        = filesize;
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
		this.isSet = false;

		this.cdate           = -1;
		this.mdate           = -1;
		this.filesize        = -1;
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

	public static CCMediaInfo createFromDB(Long filesize, Long cdate, Long mdate, String aformat, String vformat, Integer width, Integer height,
										   Double framerate, Double duration, Integer bitdepth, Integer bitrate, Integer framecount, Integer achannels,
										   String vcodec, String acodec, Integer samplerate)
	{
		if (filesize   == null) return CCMediaInfo.EMPTY;
		if (cdate      == null) return CCMediaInfo.EMPTY;
		if (mdate      == null) return CCMediaInfo.EMPTY;
		if (aformat    == null) return CCMediaInfo.EMPTY;
		if (vformat    == null) return CCMediaInfo.EMPTY;
		if (width      == null) return CCMediaInfo.EMPTY;
		if (height     == null) return CCMediaInfo.EMPTY;
		if (framerate  == null) return CCMediaInfo.EMPTY;
		if (duration   == null) return CCMediaInfo.EMPTY;
		if (bitdepth   == null) return CCMediaInfo.EMPTY;
		if (bitrate    == null) return CCMediaInfo.EMPTY;
		if (framecount == null) return CCMediaInfo.EMPTY;
		if (achannels  == null) return CCMediaInfo.EMPTY;
		if (vcodec     == null) return CCMediaInfo.EMPTY;
		if (acodec     == null) return CCMediaInfo.EMPTY;
		if (samplerate == null) return CCMediaInfo.EMPTY;

		return new CCMediaInfo(cdate, mdate, filesize, duration, bitrate, vformat, width, height,
				               framerate, (short)(int)bitdepth, framecount, vcodec, aformat,
				               (short)(int)achannels, acodec, samplerate);
	}

	public boolean isSet() {
		return isSet;
	}

	public boolean isUnset() {
		return !isSet;
	}

	public long getCDate() {
		return cdate;
	}

	public long getMDate() {
		return mdate;
	}

	public long getFilesize() {
		return filesize;
	}

	public double getDuration() {
		return duration;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CCMediaInfo that = (CCMediaInfo) o;

		if (cdate != that.cdate) return false;
		if (mdate != that.mdate) return false;
		if (filesize != that.filesize) return false;
		if (Double.compare(that.duration, duration) != 0) return false;
		if (bitrate != that.bitrate) return false;
		if (width != that.width) return false;
		if (height != that.height) return false;
		if (Double.compare(that.framerate, framerate) != 0) return false;
		if (bitdepth != that.bitdepth) return false;
		if (framecount != that.framecount) return false;
		if (!Objects.equals(videocodec, that.videocodec)) return false;
		if (audiochannels != that.audiochannels) return false;
		if (!Objects.equals(audiocodec, that.audiocodec)) return false;
		if (audiosamplerate != that.audiosamplerate) return false;
		if (!Objects.equals(videoformat, that.videoformat)) return false;
		return Objects.equals(audioformat, that.audioformat);
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = (int) (cdate ^ (cdate >>> 32));
		result = 31 * result + (int) (mdate ^ (mdate >>> 32));
		result = 31 * result + (int) (filesize ^ (filesize >>> 32));
		temp = Double.doubleToLongBits(duration);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + bitrate;
		result = 31 * result + (videoformat != null ? videoformat.hashCode() : 0);
		result = 31 * result + width;
		result = 31 * result + height;
		temp = Double.doubleToLongBits(framerate);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + (int) bitdepth;
		result = 31 * result + framecount;
		result = 31 * result + (videocodec != null ? videocodec.hashCode() : 0);
		result = 31 * result + (audioformat != null ? audioformat.hashCode() : 0);
		result = 31 * result + (int) audiochannels;
		result = 31 * result + (audiocodec != null ? audiocodec.hashCode() : 0);
		result = 31 * result + audiosamplerate;
		return result;
	}

	public CCQualityCategory getCategory() {

		if (!isSet) return CCQualityCategory.UNSET;

		CategoryType ct = getCategoryType();
		String cap = getCategoryCaption();
		String tt = getCategoryTooltip();

		return new CCQualityCategory(ct, cap, getCategoryIcon(ct), tt);
	}

	private IconRef getCategoryIcon(CategoryType ct) {
		switch (ct) {
			case LOW_QUALITY:      return Resources.ICN_TABLE_QUALITY_1;
			case OKAY:             return Resources.ICN_TABLE_QUALITY_2;
			case GOOD:             return Resources.ICN_TABLE_QUALITY_3;
			case VERY_GOOD:        return Resources.ICN_TABLE_QUALITY_4;
			case HIGH_DEFINITION:  return Resources.ICN_TABLE_QUALITY_5;
		}

		CCLog.addDefaultSwitchError(this, ct);
		return null;
	}

	@SuppressWarnings("nls")
	private String getCategoryTooltip() {
		return Str.format("{0,number,#}x{1,number,#} @ {2} FPS ({3,number,#} kb/s)", width, height, getNormalizedFPS(), getNormalizedBitrate());
	}

	private int getNormalizedFPS() {
		return (int)Math.round(framerate);
	}

	private int getNormalizedBitrate() {
		return (int)Math.round(bitrate / 1024.0);
	}

	private CategoryType getCategoryType()
	{
		if (width <  330 || height <  210) return CategoryType.LOW_QUALITY;
		if (width <  630 || height <  470) return CategoryType.OKAY;
		if (width < 1260 || height <  700) return CategoryType.GOOD;
		if (width < 1900 || height < 1060) return CategoryType.VERY_GOOD;

		return CategoryType.HIGH_DEFINITION;
	}

	@SuppressWarnings("nls")
	private String getCategoryCaption()
	{
		if (width < 320 && height < 240) return "Low";

		if (isApproxSize(4096, 3072, 0.05, 0.45)) return "4K";
		if (isApproxSize(2560, 1440, 0.05, 0.45)) return "1440p";
		if (isApproxSize(1920, 1080, 0.05, 0.45)) return "1080p";
		if (isApproxSize(1280, 720, 0.10, 0.50)) return "720p";
		if (isApproxSize(1024, 576, 0.10, 0.50)) return "576p";
		if (isApproxSize(854, 480, 0.10, 0.50)) return "480p";
		if (isApproxSize(320, 240, 0.15, 0.50)) return "240p";
		if (isApproxSize(720, 304, 0.30, 0.65)) return "304p";

		if (width < 320 || height < 240) return "Low";

		return "Other";
	}

	private boolean isApproxSize(int w, int h, double near, double far)
	{
		double dw = Math.abs(width - w) / (Math.max(width, w) * 1d);
		double dh = Math.abs(height - h) / (Math.max(height, h) * 1d);

		if (dw <= near && dh < far) return true;
		if (dw <= far && dh < near) return true;

		return false;
	}

	@SuppressWarnings("nls")
	public String validate()
	{
		if (cdate <= 0) return "CDate";
		if (mdate <= 0) return "MDate";
		if (filesize <= 0) return "Filesize";
		if (duration <= 0) return "Duration";
		if (bitrate <= 0) return "Bitrate";
		if (Str.isNullOrWhitespace(videoformat)) return "VideoFormat";
		if (Str.isNullOrWhitespace(videocodec))  return "VideoCodec";
		if (width <= 0) return "Width";
		if (height <= 0) return "Height";
		if (framerate <= 0) return "Framerate";
		if (bitdepth != 8 && bitdepth != 10) return "Bitdepth";
		if (framecount <= 0) return "Framecount";

		if (Str.isNullOrWhitespace(audioformat)) return "AudioFormat";
		if (Str.isNullOrWhitespace(audiocodec))  return "AudioCodec";
		if (audiochannels <= 0) return "AudioChannels";
		if (audiosamplerate <= 0) return "AudioSamplerate";

		return null;
	}
}
