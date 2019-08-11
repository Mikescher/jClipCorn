package de.jClipCorn.database.util;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.Str;

import javax.swing.*;

public class CCQualityCategory {
	public static final CCQualityCategory UNSET = new CCQualityCategory(
			CategoryType.UNKOWN,
			Resources.ICN_TABLE_QUALITY_0,
			LocaleBundle.getString("CCQualityCategory.Unknown"), //$NON-NLS-1$
			LocaleBundle.getString("CCQualityCategory.Unknown"), //$NON-NLS-1$
			Str.Empty);

	private final CategoryType _type;
	private final IconRef _icon;
	private final String _tooltip;
	private final String _short;
	private final String _long;

	public CCQualityCategory(CategoryType ct, IconRef icn, String txtShort, String txtLong, String txtTip) {
		_type    = ct;
		_short   = txtShort;
		_long    = txtLong;
		_icon    = icn;
		_tooltip = txtTip;
	}

	public String getShortText() {
		return _short;
	}

	public String getLongText() {
		return _long;
	}

	public Icon getIcon() {
		return _icon.get();
	}

	public String getTooltip() {
		return _tooltip;
	}

	public static int compare(CCQualityCategory o1, CCQualityCategory o2) {
		return Integer.compare(o1._type.asInt(), o2._type.asInt());
	}
}
