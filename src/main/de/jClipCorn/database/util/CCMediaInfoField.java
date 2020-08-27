package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;

import java.util.TimeZone;

@SuppressWarnings({"nls", "Convert2MethodRef"})
public enum CCMediaInfoField implements ContinoousEnum<CCMediaInfoField>
{
	IS_SET(0,           LocaleBundle.getString("Mediainfofield.is_set"),          e -> e.isSet() ? "true" : "false"),
	CDATE_SEC(1,        LocaleBundle.getString("Mediainfofield.cdate_sec"),       e -> String.valueOf(e.getCDate())),
	CDATE_DT(2,         LocaleBundle.getString("Mediainfofield.cdate_dt"),        e -> CCDateTime.createFromFileTimestamp(e.getCDate(), TimeZone.getDefault()).toStringISO()),
	MDATE_SEC(3,        LocaleBundle.getString("Mediainfofield.mdate_sec"),       e -> String.valueOf(e.getMDate())),
	MDATE_DT(4,         LocaleBundle.getString("Mediainfofield.mdate_dt"),        e -> CCDateTime.createFromFileTimestamp(e.getMDate(), TimeZone.getDefault()).toStringISO()),
	FILESIZE_B(5,       LocaleBundle.getString("Mediainfofield.filesize_b"),      e -> String.valueOf(e.getFilesize())),
	FILESIZE_KB(6,      LocaleBundle.getString("Mediainfofield.filesize_kb"),     e -> String.valueOf((int)(e.getFilesize()/1024))),
	FILESIZE_MB(7,      LocaleBundle.getString("Mediainfofield.filesize_mb"),     e -> String.valueOf((int)(e.getFilesize()/(1024*1024)))),
	DURATION_SEC(8,     LocaleBundle.getString("Mediainfofield.duration_sec"),    e -> String.valueOf(e.getNormalizedDuration())),
	DURATION_MIN(9,     LocaleBundle.getString("Mediainfofield.duration_min"),    e -> String.valueOf(Math.round(e.getDuration() / 60))),
	BITRATE_BIT(10,     LocaleBundle.getString("Mediainfofield.bitrate_bit"),     e -> String.valueOf(e.getBitrate())),
	BITRATE_KBIT(11,    LocaleBundle.getString("Mediainfofield.bitrate_kbit"),    e -> String.valueOf(e.getNormalizedBitrate())),
	BITRATE_MBIT(12,    LocaleBundle.getString("Mediainfofield.bitrate_mbit"),    e -> String.valueOf((int)Math.round(e.getBitrate() / 1000.0 / 1000.0))),
	VIDEOFORMAT(13,     LocaleBundle.getString("Mediainfofield.videoformat"),     e -> e.getVideoFormat()),
	WIDTH(14,           LocaleBundle.getString("Mediainfofield.width"),           e -> String.valueOf(e.getWidth())),
	HEIGHT(15,          LocaleBundle.getString("Mediainfofield.height"),          e -> String.valueOf(e.getHeight())),
	FRAMERATE(16,       LocaleBundle.getString("Mediainfofield.framerate"),       e -> String.valueOf(e.getFramerate())),
	BITDEPTH(17,        LocaleBundle.getString("Mediainfofield.bitdepth"),        e -> String.valueOf(e.getBitdepth())),
	FRAMECOUNT(18,      LocaleBundle.getString("Mediainfofield.framecount"),      e -> String.valueOf(e.getFramecount())),
	VIDEOCODEC(19,      LocaleBundle.getString("Mediainfofield.videocodec"),      e -> e.getVideoCodec()),
	AUDIOFORMAT(20,     LocaleBundle.getString("Mediainfofield.audioformat"),     e -> e.getAudioFormat()),
	AUDIOCHANNELS(21,   LocaleBundle.getString("Mediainfofield.audiochannels"),   e -> String.valueOf(e.getAudioChannels())),
	AUDIOCODEC(22,      LocaleBundle.getString("Mediainfofield.audiocodec"),      e -> e.getAudioCodec()),
	AUDIOSAMPLERATE(23, LocaleBundle.getString("Mediainfofield.audiosamplerate"), e -> String.valueOf(e.getAudioSamplerate())),
	FASTVIDEOHASH(24,   LocaleBundle.getString("Mediainfofield.checksum"),        e -> e.getChecksum());

	private final int id;
	private final String desc;
	private final Func1to1<CCMediaInfo, String> getter;

	private static final EnumWrapper<CCMediaInfoField> wrapper = new EnumWrapper<>(IS_SET);

	private CCMediaInfoField(int val, String description, Func1to1<CCMediaInfo, String> valueGetter) {
		id = val;
		desc = description;
		getter = valueGetter;
	}

	public static EnumWrapper<CCMediaInfoField> getWrapper() {
		return wrapper;
	}

	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return desc;
	}

	@Override
	public String[] getList() {
		return CCStreams.iterate(values()).map(e -> e.asString()).toArray(new String[0]);
	}

	@Override
	public CCMediaInfoField[] evalues() {
		return CCMediaInfoField.values();
	}

	public String get(CCMediaInfo minfo) {
		return getter.invoke(minfo);
	}
}