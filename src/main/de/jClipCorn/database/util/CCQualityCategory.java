package de.jClipCorn.database.util;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;

import javax.swing.*;

public class CCQualityCategory {
	public static final CCQualityCategory UNSET = new CCQualityCategory(
			CCQualityCategoryType.UNKOWN,
			CCQualityResolutionType.OTHER,
			LocaleBundle.getString("CCQualityCategory.Unknown"), //$NON-NLS-1$
			Str.Empty,
			-1);

	private final CCQualityCategoryType _type;
	private final CCQualityResolutionType _restype;
	private final String _long;
	private final String _tooltip;
	private final int _bitrate;

	public CCQualityCategory(CCQualityCategoryType ct, CCQualityResolutionType resolutionType, String txtLong, String txtTip, int bitrate) {
		_type    = ct;
		_restype = resolutionType;
		_long    = txtLong;
		_tooltip = txtTip;
		_bitrate = bitrate;
	}

	public String getShortText() {
		return _restype.Text;
	}

	public String getLongText() {
		return _long;
	}

	public Icon getIcon() {
		return _type.getIcon().get();
	}

	public String getTooltip() {
		return _tooltip;
	}

	public CCQualityCategoryType getCategoryType() {
		return _type;
	}

	public CCQualityResolutionType getResolutionType() {
		return _restype;
	}

	public static int compare(CCQualityCategory o1, CCQualityCategory o2) {
		int cc1 = Integer.compare(o1._type.asInt(), o2._type.asInt());
		if (cc1 != 0) return cc1;

		int cc2 = Integer.compare(o1._restype.ID, o2._restype.ID);
		if (cc2 != 0) return cc2;

		return Integer.compare(o1._bitrate, o2._bitrate);
	}
}
