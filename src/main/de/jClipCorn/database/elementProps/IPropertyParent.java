package de.jClipCorn.database.elementProps;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBStructureElementTyp;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;

public interface IPropertyParent {
	boolean updateDB();
	void updateDBWithException() throws DatabaseUpdateException;
	ICalculationCache getCache();

	CCDBStructureElementTyp getStructureType();

	CCMovieList getMovieList();
}
