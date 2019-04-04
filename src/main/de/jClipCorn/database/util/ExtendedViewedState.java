package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;

import javax.swing.*;

public class ExtendedViewedState {
	private final ExtendedViewedStateType _type;
	private final CCDateTimeList _history;
	private final int _count;

	public ExtendedViewedState(ExtendedViewedStateType t, CCDateTimeList h, Integer viewcount) {
		_type = t;
		_history = h;
		_count = (viewcount == null) ? h.count() : viewcount;
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

	public ImageIcon getIconTable() {
		switch (_type) {
			case VIEWED:
				if (CCProperties.getInstance().PROP_MAINFRAME_SHOW_VIEWCOUNT.getValue()) {
					if (_count <= 1) return Resources.ICN_TABLE_VIEWED_TRUE.get();
					if (_count < Resources.ICN_TABLE_VIEWED_TRUE_CTR.length) return Resources.ICN_TABLE_VIEWED_TRUE_CTR[_count].get();
					return Resources.ICN_TABLE_VIEWED_TRUE_CTR_MORE.get();
				} else {
					return Resources.ICN_TABLE_VIEWED_TRUE.get();
				}
			case NOT_VIEWED:
				return Resources.ICN_TABLE_VIEWED_FALSE.get();
			case MARKED_FOR_LATER:
				return Resources.ICN_TABLE_VIEWED_LATER.get();
			case MARKED_FOR_NEVER:
				return Resources.ICN_TABLE_VIEWED_NEVER.get();
			case PARTIAL_VIEWED:
				return Resources.ICN_TABLE_VIEWED_PARTIAL.get();
			case MARKED_FOR_AGAIN:
				if (CCProperties.getInstance().PROP_MAINFRAME_SHOW_VIEWCOUNT.getValue()) {
					if (_count <= 1) return Resources.ICN_TABLE_VIEWED_AGAIN.get();
					if (_count < Resources.ICN_TABLE_VIEWED_AGAIN_CTR.length) return Resources.ICN_TABLE_VIEWED_AGAIN_CTR[_count].get();
					return Resources.ICN_TABLE_VIEWED_AGAIN_CTR_MORE.get();
				} else {
					return Resources.ICN_TABLE_VIEWED_AGAIN.get();
				}
		}

		return null;
	}
}
