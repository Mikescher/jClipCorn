package de.jClipCorn.database.util;

import de.jClipCorn.gui.log.CCLog;

public enum ExtendedViewedState {
	VIEWED, 
	NOT_VIEWED, 
	MARKED_FOR_LATER;

	public boolean toBool() {
		switch (this) {
		case VIEWED: return true;
		case NOT_VIEWED: return false;
		case MARKED_FOR_LATER: return false;
		}
		
		CCLog.addError(new Exception());
		return false;
	}
}
