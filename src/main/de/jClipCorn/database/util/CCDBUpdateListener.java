package de.jClipCorn.database.util;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;

public interface CCDBUpdateListener {
	public void onAddDatabaseElement(CCDatabaseElement mov); // Element added
	public void onRemMovie(CCDatabaseElement el); // Element removed
	public void onChangeDatabaseElement(CCDatabaseElement el); // Element changed
	public void onAfterLoad(); //One time - after initial loading
	public void onRefresh(); // Just refresh everything
}
