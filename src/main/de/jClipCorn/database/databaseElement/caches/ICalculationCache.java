package de.jClipCorn.database.databaseElement.caches;

import de.jClipCorn.util.stream.CCStream;

public interface ICalculationCache
{
	void bust();

	CCStream<String> listCachedKeys();
}
