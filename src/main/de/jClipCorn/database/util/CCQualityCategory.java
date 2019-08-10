package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.Str;

import javax.swing.*;

public class CCQualityCategory {
	public static final CCQualityCategory UNSET = new CCQualityCategory(CategoryType.UNKOWN, LocaleBundle.getString("CCQualityCategory.Unknown"), Resources.ICN_TABLE_QUALITY_0, Str.Empty); //$NON-NLS-1$

	private final CategoryType _type;
	private final String _caption;
	private final IconRef _icon;
	private final String _tooltip;

	public CCQualityCategory(CategoryType ct, String capt, IconRef icn, String ttip) {
		_type    = ct;
		_caption = capt;
		_icon    = icn;
		_tooltip = ttip;
	}

	public String getCaption() {
		return _caption;
	}

	public Icon getIcon() {
		return _icon.get();
	}

	public String getTooltip() {
		return _tooltip;
	}

	public static int compare(CCQualityCategory o1, CCQualityCategory o2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
