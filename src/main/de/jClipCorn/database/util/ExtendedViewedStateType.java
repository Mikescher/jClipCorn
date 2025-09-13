package de.jClipCorn.database.util;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum ExtendedViewedStateType implements ContinoousEnum<ExtendedViewedStateType>  {

	// VIEWED                => Fully viewed (no tags)
	// MARKED_FOR_AGAIN      => Fully viewed with [LATER] tag
	// MARKED_ABORTED        => Fully viewed with [CANCELLED] tag
	//
	// PARTIAL_VIEWED        => Partially viewed (no tags)
	// MARKED_FOR_CONTINUE   => Partially viewed with [LATER] tag
	//
	// NOT_VIEWED            => Not viewed (no tags)
	// MARKED_FOR_LATER      => Not viewed with [LATER] tag
	// MARKED_FOR_NEVER      => Not viewed with [NEVER] tag

	// --------------------------------------------------------------------

	// [CANCELLED] and [LATER] tags kinda work as synonyms, but the DB check should validate that they are set correctly


	VIEWED(0), 
	NOT_VIEWED(1),
	MARKED_FOR_LATER(2), 
	MARKED_FOR_NEVER(3),
	PARTIAL_VIEWED(4),
	MARKED_FOR_AGAIN(5),
	MARKED_FOR_CONTINUE(6),
	MARKED_ABORTED(7);
	
	private final static String[] NAMES =
	{
		LocaleBundle.getString("FilterTree.Viewed.Viewed"),    //$NON-NLS-1$
		LocaleBundle.getString("FilterTree.Viewed.Unviewed"),  //$NON-NLS-1$
		LocaleBundle.getString("FilterTree.Viewed.Later"),     //$NON-NLS-1$
		LocaleBundle.getString("FilterTree.Viewed.Never"),     //$NON-NLS-1$
		LocaleBundle.getString("FilterTree.Viewed.Partial"),   //$NON-NLS-1$
		LocaleBundle.getString("FilterTree.Viewed.Again"),     //$NON-NLS-1$
		LocaleBundle.getString("FilterTree.Viewed.Continue"),  //$NON-NLS-1$
		LocaleBundle.getString("FilterTree.Viewed.Aborted"),   //$NON-NLS-1$
	};

	private final int id;
	
	private static final EnumWrapper<ExtendedViewedStateType> wrapper = new EnumWrapper<>(VIEWED);

	ExtendedViewedStateType(int val) {
		id = val;
	}
	
	public static EnumWrapper<ExtendedViewedStateType> getWrapper() {
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

	public int getTriStateInt() {

		// used for sorting
		// [VIEWED] -> [PARTIAL] -> [NOT_VIEWED]

		switch (this) {
			case VIEWED:              return 2;
			case NOT_VIEWED:          return 0;
			case MARKED_FOR_LATER:    return 0;
			case MARKED_FOR_NEVER:    return 0;
			case PARTIAL_VIEWED:      return 1;
			case MARKED_FOR_AGAIN:    return 2;
			case MARKED_FOR_CONTINUE: return 1;
			case MARKED_ABORTED:      return 2;
		}

		return -1;
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

	@Override
	public ExtendedViewedStateType[] evalues() {
		return ExtendedViewedStateType.values();
	}

	public boolean toBool() {
		switch (this) {
			case VIEWED:              return true;
			case NOT_VIEWED:          return false;
			case MARKED_FOR_LATER:    return false;
			case MARKED_FOR_NEVER:    return false;
			case PARTIAL_VIEWED:      return false;
			case MARKED_FOR_AGAIN:    return true;
			case MARKED_FOR_CONTINUE: return true;
			case MARKED_ABORTED:      return false;
		}
		
		CCLog.addError(new Exception());
		return false;
	}
}
