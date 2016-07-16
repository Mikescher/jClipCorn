package de.jClipCorn.database.util;

import de.jClipCorn.gui.log.CCLog;

public enum ExtendedViewedStateType {
	VIEWED, 
	NOT_VIEWED, 
	MARKED_FOR_LATER, 
	MARKED_FOR_NEVER,
	PARTIAL_VIEWED;

	public boolean toBool() {
		switch (this) {
			case VIEWED: return true;
			case NOT_VIEWED: return false;
			case MARKED_FOR_LATER: return false;
			case MARKED_FOR_NEVER: return false;
			case PARTIAL_VIEWED: return false;
		}
		
		CCLog.addError(new Exception());
		return false;
	}
}
