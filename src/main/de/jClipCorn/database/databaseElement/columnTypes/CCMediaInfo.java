package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.Str;

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
}
