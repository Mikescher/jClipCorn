package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;

public interface CCDBUpdateListener {
	public void onAddDatabaseElement(CCDatabaseElement el);
	public void onAddSeason(CCSeason el);
	public void onAddEpisode(CCEpisode el);

	public void onRemDatabaseElement(CCDatabaseElement el);
	public void onRemSeason(CCSeason el);
	public void onRemEpisode(CCEpisode el);

	public void onChangeDatabaseElement(CCDatabaseElement rootElement, ICCDatabaseStructureElement actualElement, String[] props); // Element changed

	public void onAfterLoad(); //One time - after initial loading
	public void onRefresh(); // Just refresh everything
}
