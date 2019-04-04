package de.jClipCorn.database.util;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum ExtendedViewedStateType implements ContinoousEnum<ExtendedViewedStateType>  {
	VIEWED(0), 
	NOT_VIEWED(1), 
	MARKED_FOR_LATER(2), 
	MARKED_FOR_NEVER(3),
	PARTIAL_VIEWED(4),
	MARKED_FOR_AGAIN(5);
	
	private final static String[] NAMES =
		{ 
			LocaleBundle.getString("FilterTree.Viewed.Viewed"), 	//$NON-NLS-1$
			LocaleBundle.getString("FilterTree.Viewed.Unviewed"), 	//$NON-NLS-1$
			LocaleBundle.getString("FilterTree.Viewed.Later"), 		//$NON-NLS-1$
			LocaleBundle.getString("FilterTree.Viewed.Never"), 		//$NON-NLS-1$
			LocaleBundle.getString("FilterTree.Viewed.Partial"),	//$NON-NLS-1$
			LocaleBundle.getString("FilterTree.Viewed.Again"),		//$NON-NLS-1$
		};
	private int id;
	
	private static EnumWrapper<ExtendedViewedStateType> wrapper = new EnumWrapper<>(VIEWED);

	private ExtendedViewedStateType(int val) {
		id = val;
	}
	
	public static EnumWrapper<ExtendedViewedStateType> getWrapper() {
		return wrapper;
	}

	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public String[] getList() {
		return NAMES;
	}

	public static int compare(ExtendedViewedStateType o1, ExtendedViewedStateType o2) {
		return Integer.compare(o1.asInt(), o2.asInt());
	}

	public ImageIcon getIconTable() {
		switch (this) {
		case VIEWED:
			return Resources.ICN_TABLE_VIEWED_TRUE.get();
		case NOT_VIEWED:
			return Resources.ICN_TABLE_VIEWED_FALSE.get();
		case MARKED_FOR_LATER:
			return Resources.ICN_TABLE_VIEWED_LATER.get();
		case MARKED_FOR_NEVER:
			return Resources.ICN_TABLE_VIEWED_NEVER.get();
		case PARTIAL_VIEWED:
			return Resources.ICN_TABLE_VIEWED_PARTIAL.get();
		case MARKED_FOR_AGAIN:
			return Resources.ICN_TABLE_VIEWED_AGAIN.get();
		}
		
		return null;
	}

	public ImageIcon getIconSidebar() {
		switch (this) {
		case VIEWED:
			return Resources.ICN_SIDEBAR_VIEWED.get();
		case NOT_VIEWED:
			return Resources.ICN_SIDEBAR_UNVIEWED.get();
		case MARKED_FOR_LATER:
			return Resources.ICN_SIDEBAR_LATER.get();
		case MARKED_FOR_NEVER:
			return Resources.ICN_SIDEBAR_NEVER.get();
		case PARTIAL_VIEWED:
			return Resources.ICN_SIDEBAR_PARTIALLY.get();
		case MARKED_FOR_AGAIN:
			return Resources.ICN_SIDEBAR_AGAIN.get();
		}
		
		return null;
	}

	@Override
	public ExtendedViewedStateType[] evalues() {
		return ExtendedViewedStateType.values();
	}

	public boolean toBool() {
		switch (this) {
			case VIEWED: return true;
			case NOT_VIEWED: return false;
			case MARKED_FOR_LATER: return false;
			case MARKED_FOR_NEVER: return false;
			case PARTIAL_VIEWED: return false;
			case MARKED_FOR_AGAIN: return true;
		}
		
		CCLog.addError(new Exception());
		return false;
	}
}
