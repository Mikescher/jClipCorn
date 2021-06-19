package de.jClipCorn.database.elementValues;

import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;

public interface IPropertyParent {
	boolean updateDB();
	void updateDBWithException() throws DatabaseUpdateException;
	ICalculationCache getCache();
}
