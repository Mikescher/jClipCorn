package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;

public interface CCDBUpdateListener {
	void onAddDatabaseElement(CCDatabaseElement el);
	void onAddSeason(CCSeason el);
	void onAddEpisode(CCEpisode el);

	void onRemDatabaseElement(CCDatabaseElement el);
	void onRemSeason(CCSeason el);
	void onRemEpisode(CCEpisode el);

	void onChangeDatabaseElement(CCDatabaseElement rootElement, ICCDatabaseStructureElement actualElement, String[] props); // Element changed

	void onAfterLoad(); //One time - after initial loading
	void onRefresh(); // Just refresh everything
}
