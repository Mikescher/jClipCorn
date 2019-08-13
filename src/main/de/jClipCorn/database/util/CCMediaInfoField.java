package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;

import java.util.TimeZone;

@SuppressWarnings({"nls", "Convert2MethodRef"})
public enum CCMediaInfoField implements ContinoousEnum<CCMediaInfoField>
{
	IS_SET(0,           "MediaInfo is set",              e -> e.isSet() ? "true" : "false"),
	CDATE_SEC(1,        "Creation date (sec)",           e -> String.valueOf(e.getCDate())),
	CDATE_DT(2,         "Creation date (timestamp)",     e -> CCDateTime.createFromFileTimestamp(e.getCDate(), TimeZone.getDefault()).toStringISO()),
	MDATE_SEC(3,        "Modification date (sec)",       e -> String.valueOf(e.getMDate())),
	MDATE_DT(4,         "Modification date (timestamp)", e -> CCDateTime.createFromFileTimestamp(e.getMDate(), TimeZone.getDefault()).toStringISO()),
	FILESIZE(5,         "Filesize (bytes)",              e -> String.valueOf(e.getFilesize())),
	DURATION_SEC(6,     "Filesize (seconds)",            e -> String.valueOf(e.getNormalizedDuration())),
	DURATION_MIN(7,     "Filesize (minutes)",            e -> String.valueOf(Math.round(e.getDuration() / 60))),
	BITRATE_BIT(8,      "Bitrate (bit/s)",               e -> String.valueOf(e.getBitrate())),
	BITRATE_KBIT(9,     "Bitrate (Kbit/s)",              e -> String.valueOf(e.getNormalizedBitrate())),
	BITRATE_MBIT(10,    "Bitrate (Mbit/s)",              e -> String.valueOf((int)Math.round(e.getBitrate() / 1000.0 / 1000.0))),
	VIDEOFORMAT(11,     "Video format",                  e -> e.getVideoFormat()),
	WIDTH(12,           "Video width",                   e -> String.valueOf(e.getWidth())),
	HEIGHT(13,          "Video height",                  e -> String.valueOf(e.getHeight())),
	FRAMERATE(14,       "Framerate",                     e -> String.valueOf(e.getFramerate())),
	BITDEPTH(15,        "Bitdepth",                      e -> String.valueOf(e.getBitdepth())),
	FRAMECOUNT(16,      "Framecount",                    e -> String.valueOf(e.getFramecount())),
	VIDEOCODEC(17,      "Video codec",                   e -> e.getVideoCodec()),
	AUDIOFORMAT(18,     "Audio format",                  e -> e.getAudioFormat()),
	AUDIOCHANNELS(19,   "Framecount",                    e -> String.valueOf(e.getAudioChannels())),
	AUDIOCODEC(20,      "Audio codec",                   e -> e.getAudioCodec()),
	AUDIOSAMPLERATE(21, "Audio samplerate",              e -> String.valueOf(e.getAudioSamplerate()));

	private final int id;
	private final String desc;
	private final Func1to1<CCMediaInfo, String> getter;

	private static EnumWrapper<CCMediaInfoField> wrapper = new EnumWrapper<>(IS_SET);

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