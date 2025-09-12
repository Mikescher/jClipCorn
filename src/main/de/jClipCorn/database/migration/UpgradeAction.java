package de.jClipCorn.database.migration;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.driver.CCDatabase;

public interface UpgradeAction {
	void onAfterConnect(CCMovieList ml, CCDatabase db);
}
