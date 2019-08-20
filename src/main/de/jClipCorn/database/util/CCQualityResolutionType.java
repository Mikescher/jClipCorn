package de.jClipCorn.database.util;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.stream.CCStreams;

@SuppressWarnings("nls")
public enum CCQualityResolutionType implements ContinoousEnum<CCQualityResolutionType>
{
	R_LOW(0, "Low"),
	R_240(1, "240p"),
	R_304(2, "304p"),
	R_480(3, "480p"),
	R_576(4, "576p"),
	R_720(5, "720p"),
	R_1080(6, "1080p"),
	R_1440(7, "1440p"),
	R_4K(8, "4k"),

	OTHER(9, "Other"),
	UNKNOWN(10, LocaleBundle.getString("CCQualityCategory.Unknown")),
	MULTIPLE(11, LocaleBundle.getString("CCQualityCategory.Multiple"));

	private static String[] _list = CCStreams.iterate(values()).map(CCQualityResolutionType::asString).toArray(new String[0]);

	public final int ID;
	public final String Text;

	private static EnumWrapper<CCQualityResolutionType> wrapper = new EnumWrapper<>(UNKNOWN);

	CCQualityResolutionType(int id, String txt) {
		ID = id;
		Text = txt;
	}

	public static EnumWrapper<CCQualityResolutionType> getWrapper() {
		return wrapper;
	}

	@Override
	public int asInt() {
		return ID;
	}

	@Override
	public String asString() {
		return Text;
	}

	@Override
	public String[] getList() {
		return _list;
	}

	@Override
	public CCQualityResolutionType[] evalues() {
		return values();
	}
}
