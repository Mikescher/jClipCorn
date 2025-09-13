package de.jClipCorn.database.util;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleTag;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;

import javax.swing.*;

// This is mostly a matrix out of [viewed] x [tags] | with the additional view_count information

/*
	  +-------------------+---------------------------+----------------------------+----------------------------------+----------------------------------+
	  |                   | [none]                    | [LATER]                    | [NEVER]                          | [CANCELLED]                      |
	  +-------------------+---------------------------+----------------------------+----------------------------------+----------------------------------+
	  | Viewed (Movie)    | ICN_TABLE_VIEWED_TRUE_CTR | ICN_TABLE_VIEWED_AGAIN_CTR | ICN_TABLE_VIEWED_ABORTED_CTR     | ICN_TABLE_VIEWED_ABORTED_CTR     |
	  +-------------------+---------------------------+----------------------------+----------------------------------+----------------------------------+
	  | Viewed (Series)   | ICN_TABLE_VIEWED_TRUE_CTR | ICN_TABLE_VIEWED_AGAIN_CTR | ICN_TABLE_VIEWED_ABORTED_CTR     | ICN_TABLE_VIEWED_ABORTED_CTR     |
	  +-------------------+---------------------------+----------------------------+----------------------------------+----------------------------------+
	  | Viewed (Season)   | ICN_TABLE_VIEWED_TRUE_CTR | -                          | -                                | -                                |
	  +-------------------+---------------------------+----------------------------+----------------------------------+----------------------------------+
	  | Viewed (Episode)  | ICN_TABLE_VIEWED_TRUE_CTR | ICN_TABLE_VIEWED_AGAIN_CTR | ICN_TABLE_VIEWED_ABORTED_CTR     | ICN_TABLE_VIEWED_ABORTED_CTR     |
	  +-------------------+---------------------------+----------------------------+----------------------------------+----------------------------------+
	  | Partial           | ICN_TABLE_VIEWED_PARTIAL  | ICN_TABLE_VIEWED_CONTINUE  | ICN_TABLE_VIEWED_PARTIAL_ABORTED | ICN_TABLE_VIEWED_PARTIAL_ABORTED |
	  +-------------------+---------------------------+----------------------------+----------------------------------+----------------------------------+
	  | NotViewed         | ICN_TABLE_VIEWED_FALSE    | ICN_TABLE_VIEWED_LATER     | ICN_TABLE_VIEWED_FALSE_ABORTED   | ICN_TABLE_VIEWED_FALSE_ABORTED   |
	  +-------------------+---------------------------+----------------------------+----------------------------------+----------------------------------+
 */

public class ExtendedViewedState {
	private final ExtendedViewedStateType _type;
	private final CCDateTimeList _history;
	private final int _count;
	private final IconRef _icon;

	private ExtendedViewedState(ExtendedViewedStateType type, CCDateTimeList history, int count, IconRef icon) {
		_type    = type;
		_history = (history == null) ? CCDateTimeList.createEmpty() : history;
		_count   = count;
		_icon    = icon;
	}

	public static ExtendedViewedState create(CCMovie elem) {
		var hist = elem.ViewedHistory.get();

		var count = hist.count();

		var isViewed        = (count > 0);
		var isNotViewed     = (count == 0);
		var isViewedUnknown = (count == 1) && hist.isOnlyUnspecified();
		var isViewedTooMany = count >= Resources.ICN_TABLE_VIEWED_TRUE_CTR.length;
		var isViewedSingle  = (count == 1) && !hist.isOnlyUnspecified();

		var isLater     = elem.Tags.get().getTag(CCSingleTag.TAG_WATCH_LATER);
		var isNever     = elem.Tags.get().getTag(CCSingleTag.TAG_WATCH_NEVER);
		var isCancelled = elem.Tags.get().getTag(CCSingleTag.TAG_WATCH_CANCELLED);
		var isNoTag     = (!isLater && !isNever && !isCancelled);

		if (!elem.ccprops().PROP_MAINFRAME_SHOW_VIEWCOUNT.getValue() && isViewed) {
			// force non-ctr icon
			isViewedUnknown = false;
			isViewedTooMany = false;
			isViewedSingle  = true;
		}

		if (isNoTag && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE);
		if (isNoTag && isViewedUnknown) return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR_UNKNOWN);
		if (isNoTag && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR_MORE);
		if (isNoTag && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR[count]);

		if (isLater && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, hist, count, Resources.ICN_TABLE_VIEWED_AGAIN);
		if (isLater && isViewedUnknown) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, hist, count, Resources.ICN_TABLE_VIEWED_AGAIN_CTR_UNKNOWN);
		if (isLater && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, hist, count, Resources.ICN_TABLE_VIEWED_AGAIN_CTR_MORE);
		if (isLater && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, hist, count, Resources.ICN_TABLE_VIEWED_AGAIN_CTR[count]);

		if (isNever && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_ABORTED);
		if (isNever && isViewedUnknown) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_UNKNOWN);
		if (isNever && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_MORE);
		if (isNever && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR[count]);

		if (isCancelled && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_ABORTED);
		if (isCancelled && isViewedUnknown) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_UNKNOWN);
		if (isCancelled && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_MORE);
		if (isCancelled && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR[count]);

		if (isNoTag     && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED,       hist, count, Resources.ICN_TABLE_VIEWED_FALSE);
		if (isLater     && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, hist, count, Resources.ICN_TABLE_VIEWED_LATER);
		if (isNever     && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, hist, count, Resources.ICN_TABLE_VIEWED_FALSE_ABORTED);
		if (isCancelled && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, hist, count, Resources.ICN_TABLE_VIEWED_FALSE_ABORTED);

		CCLog.addUndefinied("Invalid ExtendedViewedState error for movie " + elem.getLocalID()); //$NON-NLS-1$
		return null;
	}

	public static ExtendedViewedState create(CCSeries elem) {
		var count = elem.getFullViewCount();

		var isViewed        = elem.isViewed();
		var isPartial       = elem.isPartialViewed();
		var isNotViewed     = elem.isUnviewed();
		var isViewedTooMany = count >= Resources.ICN_TABLE_VIEWED_TRUE_CTR.length;
		var isViewedSingle  = isViewed && (count == 1);

		var isLater     = elem.Tags.get().getTag(CCSingleTag.TAG_WATCH_LATER);
		var isNever     = elem.Tags.get().getTag(CCSingleTag.TAG_WATCH_NEVER);
		var isCancelled = elem.Tags.get().getTag(CCSingleTag.TAG_WATCH_CANCELLED);
		var isNoTag     = (!isLater && !isNever && !isCancelled);

		if (!elem.ccprops().PROP_MAINFRAME_SHOW_VIEWCOUNT.getValue() && isViewed) {
			// force non-ctr icon
			isViewedTooMany = false;
			isViewedSingle  = true;
		}

		if (isNoTag && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, null, count, Resources.ICN_TABLE_VIEWED_TRUE);
		if (isNoTag && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, null, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR_MORE);
		if (isNoTag && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, null, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR[count]);

		if (isLater && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, null, count, Resources.ICN_TABLE_VIEWED_AGAIN);
		if (isLater && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, null, count, Resources.ICN_TABLE_VIEWED_AGAIN_CTR_MORE);
		if (isLater && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, null, count, Resources.ICN_TABLE_VIEWED_AGAIN_CTR[count]);

		if (isNever && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, null, count, Resources.ICN_TABLE_VIEWED_TRUE_ABORTED);
		if (isNever && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, null, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_MORE);
		if (isNever && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, null, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR[count]);

		if (isCancelled && isViewedSingle) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, null, count, Resources.ICN_TABLE_VIEWED_TRUE_ABORTED);
		if (isCancelled && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, null, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_MORE);
		if (isCancelled && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, null, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR[count]);

		if (isNoTag     && isPartial) return new ExtendedViewedState(ExtendedViewedStateType.VIEWED,              null, count, Resources.ICN_TABLE_VIEWED_PARTIAL);
		if (isLater     && isPartial) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_CONTINUE, null, count, Resources.ICN_TABLE_VIEWED_CONTINUE);
		if (isNever     && isPartial) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED,      null, count, Resources.ICN_TABLE_VIEWED_PARTIAL_ABORTED);
		if (isCancelled && isPartial) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED,      null, count, Resources.ICN_TABLE_VIEWED_PARTIAL_ABORTED);

		if (isNoTag     && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED,       null, count, Resources.ICN_TABLE_VIEWED_FALSE);
		if (isLater     && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, null, count, Resources.ICN_TABLE_VIEWED_LATER);
		if (isNever     && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, null, count, Resources.ICN_TABLE_VIEWED_FALSE_ABORTED);
		if (isCancelled && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, null, count, Resources.ICN_TABLE_VIEWED_FALSE_ABORTED);

		CCLog.addUndefinied("Invalid ExtendedViewedState error for series " + elem.getLocalID()); //$NON-NLS-1$
		return null;
	}

	public static ExtendedViewedState create(CCSeason elem) {
		var count = elem.getFullViewCount();

		var isViewed        = elem.isViewed();
		var isPartial       = elem.isPartialViewed();
		var isNotViewed     = elem.isUnviewed();
		var isViewedTooMany = count >= Resources.ICN_TABLE_VIEWED_TRUE_CTR.length;
		var isViewedSingle  = isViewed && (count == 1);

		if (!elem.ccprops().PROP_MAINFRAME_SHOW_VIEWCOUNT.getValue() && isViewed) {
			// force non-ctr icon
			isViewedTooMany = false;
			isViewedSingle  = true;
		}

		if (isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.VIEWED,     null, count, Resources.ICN_TABLE_VIEWED_TRUE);
		if (isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.VIEWED,     null, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR_MORE);
		if (isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.VIEWED,     null, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR[count]);
		if (isPartial)       return new ExtendedViewedState(ExtendedViewedStateType.VIEWED,     null, count, Resources.ICN_TABLE_VIEWED_PARTIAL);
		if (isNotViewed)     return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, null, count, Resources.ICN_TABLE_VIEWED_FALSE);

		CCLog.addUndefinied("Invalid ExtendedViewedState error for season " + elem.getLocalID()); //$NON-NLS-1$
		return null;
	}

	public static ExtendedViewedState create(CCEpisode elem) {
		var hist = elem.ViewedHistory.get();

		var count = hist.count();

		var isViewed        = (count > 0);
		var isNotViewed     = (count == 0);
		var isViewedUnknown = (count == 1) && hist.isOnlyUnspecified();
		var isViewedTooMany = count >= Resources.ICN_TABLE_VIEWED_TRUE_CTR.length;
		var isViewedSingle  = (count == 1) && !hist.isOnlyUnspecified();

		var isLater     = elem.Tags.get().getTag(CCSingleTag.TAG_WATCH_LATER);
		var isNever     = elem.Tags.get().getTag(CCSingleTag.TAG_WATCH_NEVER);
		var isCancelled = elem.Tags.get().getTag(CCSingleTag.TAG_WATCH_CANCELLED);
		var isNoTag     = (!isLater && !isNever && !isCancelled);

		if (!elem.ccprops().PROP_MAINFRAME_SHOW_VIEWCOUNT.getValue() && isViewed) {
			// force non-ctr icon
			isViewedTooMany = false;
			isViewedSingle  = true;
		}

		if (isNoTag && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE);
		if (isNoTag && isViewedUnknown) return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR_UNKNOWN);
		if (isNoTag && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR_MORE);
		if (isNoTag && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_CTR[count]);

		if (isLater && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, hist, count, Resources.ICN_TABLE_VIEWED_AGAIN);
		if (isLater && isViewedUnknown) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, hist, count, Resources.ICN_TABLE_VIEWED_AGAIN_CTR_UNKNOWN);
		if (isLater && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, hist, count, Resources.ICN_TABLE_VIEWED_AGAIN_CTR_MORE);
		if (isLater && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, hist, count, Resources.ICN_TABLE_VIEWED_AGAIN_CTR[count]);

		if (isNever && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_ABORTED);
		if (isNever && isViewedUnknown) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_UNKNOWN);
		if (isNever && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_MORE);
		if (isNever && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR[count]);

		if (isCancelled && isViewedSingle)  return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_TRUE_ABORTED);
		if (isCancelled && isViewedUnknown) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_UNKNOWN);
		if (isCancelled && isViewedTooMany) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR_MORE);
		if (isCancelled && isViewed)        return new ExtendedViewedState(ExtendedViewedStateType.MARKED_ABORTED, hist, count, Resources.ICN_TABLE_VIEWED_ABORTED_CTR[count]);

		if (isNoTag     && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED,       hist, count, Resources.ICN_TABLE_VIEWED_FALSE);
		if (isLater     && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, hist, count, Resources.ICN_TABLE_VIEWED_LATER);
		if (isNever     && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, hist, count, Resources.ICN_TABLE_VIEWED_FALSE_ABORTED);
		if (isCancelled && isNotViewed) return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, hist, count, Resources.ICN_TABLE_VIEWED_FALSE_ABORTED);

		CCLog.addUndefinied("Invalid ExtendedViewedState error for episode " + elem.getLocalID()); //$NON-NLS-1$
		return null;
	}

	public CCDateTimeList getHistory() {
		return _history;
	}
	
	public ExtendedViewedStateType getType() {
		return _type;
	}

	public int getViewCount() {
		return _count;
	}

	public boolean toBool() {
		return _type.toBool();
	}

	public ImageIcon getIconTable(CCMovieList ml) {
		return _icon.get();
	}
}
