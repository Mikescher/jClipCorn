package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;

import java.util.TimeZone;

@SuppressWarnings({"nls", "Convert2MethodRef"})
public enum CCMediaInfoField implements ContinoousEnum<CCMediaInfoField>
{
	IS_SET(0,           LocaleBundle.getString("Mediainfofield.is_set"),          e -> Opt.of(e.isFullySet() ? "true" : "false")),
	CDATE_SEC(1,        LocaleBundle.getString("Mediainfofield.cdate_sec"),       e -> e.CDate.map(String::valueOf)),
	CDATE_DT(2,         LocaleBundle.getString("Mediainfofield.cdate_dt"),        e -> e.CDate.map(p -> CCDateTime.createFromFileTimestamp(p, TimeZone.getDefault()).toStringISO()).map(String::valueOf)),
	MDATE_SEC(3,        LocaleBundle.getString("Mediainfofield.mdate_sec"),       e -> e.MDate.map(String::valueOf)),
	MDATE_DT(4,         LocaleBundle.getString("Mediainfofield.mdate_dt"),        e -> e.MDate.map(p -> CCDateTime.createFromFileTimestamp(p, TimeZone.getDefault()).toStringISO()).map(String::valueOf)),
	FILESIZE_B(5,       LocaleBundle.getString("Mediainfofield.filesize_b"),      e -> e.Filesize.map(CCFileSize::getBytes).map(String::valueOf)),
	FILESIZE_KB(6,      LocaleBundle.getString("Mediainfofield.filesize_kb"),     e -> e.Filesize.map(CCFileSize::getKiloBytes).map(String::valueOf)),
	FILESIZE_MB(7,      LocaleBundle.getString("Mediainfofield.filesize_mb"),     e -> e.Filesize.map(CCFileSize::getMegaBytes).map(String::valueOf)),
	DURATION_SEC(8,     LocaleBundle.getString("Mediainfofield.duration_sec"),    e -> e.getNormalizedDuration().map(String::valueOf)),
	DURATION_MIN(9,     LocaleBundle.getString("Mediainfofield.duration_min"),    e -> e.Duration.map(p -> Math.round(p/60)).map(String::valueOf)),
	BITRATE_BIT(10,     LocaleBundle.getString("Mediainfofield.bitrate_bit"),     e -> e.Bitrate.map(String::valueOf)),
	BITRATE_KBIT(11,    LocaleBundle.getString("Mediainfofield.bitrate_kbit"),    e -> e.getNormalizedBitrate().map(String::valueOf)),
	BITRATE_MBIT(12,    LocaleBundle.getString("Mediainfofield.bitrate_mbit"),    e -> e.Bitrate.map(p -> Math.round(p / 1000.0 / 1000.0)).map(String::valueOf)),
	VIDEOFORMAT(13,     LocaleBundle.getString("Mediainfofield.videoformat"),     e -> e.VideoFormat),
	WIDTH(14,           LocaleBundle.getString("Mediainfofield.width"),           e -> e.Width.map(String::valueOf)),
	HEIGHT(15,          LocaleBundle.getString("Mediainfofield.height"),          e -> e.Height.map(String::valueOf)),
	FRAMERATE(16,       LocaleBundle.getString("Mediainfofield.framerate"),       e -> e.Framerate.map(String::valueOf)),
	BITDEPTH(17,        LocaleBundle.getString("Mediainfofield.bitdepth"),        e -> e.Bitdepth.map(String::valueOf)),
	FRAMECOUNT(18,      LocaleBundle.getString("Mediainfofield.framecount"),      e -> e.Framecount.map(String::valueOf)),
	VIDEOCODEC(19,      LocaleBundle.getString("Mediainfofield.videocodec"),      e -> e.VideoCodec),
	AUDIOFORMAT(20,     LocaleBundle.getString("Mediainfofield.audioformat"),     e -> e.AudioFormat),
	AUDIOCHANNELS(21,   LocaleBundle.getString("Mediainfofield.audiochannels"),   e -> e.AudioChannels.map(String::valueOf)),
	AUDIOCODEC(22,      LocaleBundle.getString("Mediainfofield.audiocodec"),      e -> e.AudioCodec),
	AUDIOSAMPLERATE(23, LocaleBundle.getString("Mediainfofield.audiosamplerate"), e -> e.AudioSamplerate.map(String::valueOf)),
	FASTVIDEOHASH(24,   LocaleBundle.getString("Mediainfofield.checksum"),        e -> e.Checksum),
	IS_UNSET(25,        LocaleBundle.getString("Mediainfofield.is_unset"),        e -> Opt.of(e.isFullyEmpty()   ? "true" : "false")),
	IS_PARTSET(26,      LocaleBundle.getString("Mediainfofield.is_mixed_set"),    e -> Opt.of(e.isPartiallySet() ? "true" : "false"));

	private final int id;
	private final String desc;
	private final Func1to1<CCMediaInfo, Opt<String>> getter;

	private static final EnumWrapper<CCMediaInfoField> wrapper = new EnumWrapper<>(IS_SET);

	CCMediaInfoField(int val, String description, Func1to1<CCMediaInfo, Opt<String>> valueGetter) {
		id = val;
		desc = description;
		getter = valueGetter;
	}

	public static EnumWrapper<CCMediaInfoField> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
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

	public Opt<String> get(CCMediaInfo minfo) {
		return getter.invoke(minfo);
	}
}