package de.jClipCorn.database.util;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
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
	
	private final static String NAMES[] = 
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
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_TRUE);
		case NOT_VIEWED:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_FALSE);
		case MARKED_FOR_LATER:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_LATER);
		case MARKED_FOR_NEVER:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_NEVER);
		case PARTIAL_VIEWED:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_PARTIAL);
		case MARKED_FOR_AGAIN:
			return CachedResourceLoader.getIcon(Resources.ICN_TABLE_VIEWED_AGAIN);
		}
		
		return null;
	}

	public ImageIcon getIconSidebar() {
		switch (this) {
		case VIEWED:
			return CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_VIEWED);
		case NOT_VIEWED:
			return CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_UNVIEWED);
		case MARKED_FOR_LATER:
			return CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_LATER);
		case MARKED_FOR_NEVER:
			return CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_NEVER);
		case PARTIAL_VIEWED:
			return CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_PARTIALLY);
		case MARKED_FOR_AGAIN:
			return CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_AGAIN);
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
