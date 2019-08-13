package de.jClipCorn.database.util;

import de.jClipCorn.gui.localization.LocaleBundle;

@SuppressWarnings("nls")
public enum CCQualityResolutionType {
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

	public final int ID;
	public final String Text;

	private CCQualityResolutionType(int id, String txt) {
		ID = id;
		Text = txt;
	}
}
