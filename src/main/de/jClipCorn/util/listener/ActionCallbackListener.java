package de.jClipCorn.util.listener;

import de.jClipCorn.database.databaseElement.CCEpisode;

public interface ActionCallbackListener {
	void onUpdate(Object o);

	void onCallbackPlayed(CCEpisode e);


	static UpdateCallbackListener toUpdateCallbackListener(ActionCallbackListener acl) {
		if (acl == null) return null;
		return acl::onUpdate;
	}
}
