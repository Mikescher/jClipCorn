package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;

public class ExtendedViewedState {
	public final ExtendedViewedStateType Type;
	public final CCDateTimeList History;

	public ExtendedViewedState(ExtendedViewedStateType t, CCDateTimeList h) {
		Type = t;
		History = h;
	}
	
	public CCDateTimeList getHistory() {
		return History;
	}
	
	public ExtendedViewedStateType getType() {
		return Type;
	}

	public boolean toBool() {
		return Type.toBool();
	}
}
