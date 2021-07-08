package de.jClipCorn.util.adapter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.util.CCDBUpdateListener;

public class CCDBUpdateAdapter implements CCDBUpdateListener
{
	@Override public void onAddDatabaseElement(CCDatabaseElement mov) { }

	@Override public void onAddSeason(CCSeason el) { }

	@Override public void onAddEpisode(CCEpisode el) { }

	@Override public void onRemDatabaseElement(CCDatabaseElement el) { }

	@Override public void onRemSeason(CCSeason el) { }

	@Override public void onRemEpisode(CCEpisode el) { }

	@Override public void onChangeDatabaseElement(CCDatabaseElement rootElement, ICCDatabaseStructureElement actualElement, String[] props) { }

	@Override public void onAfterLoad() { }

	@Override public void onRefresh() { }
}
