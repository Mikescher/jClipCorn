package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;

public interface CCDBUpdateListener {
	public void onAddDatabaseElement(CCDatabaseElement mov);
	public void onRemMovie(CCDatabaseElement el);
	public void onChangeDatabaseElement(CCDatabaseElement el);
	public void onAfterLoad();
}
