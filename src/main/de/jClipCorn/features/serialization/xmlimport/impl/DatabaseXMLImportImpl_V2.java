package de.jClipCorn.features.serialization.xmlimport.impl;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.serialization.xmlimport.IDatabaseXMLImporterImpl;
import de.jClipCorn.features.serialization.xmlimport.ImportState;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

public class DatabaseXMLImportImpl_V2 implements IDatabaseXMLImporterImpl
{
	@Override
	public void importMovie(CCMovie o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException
	{

	}

	@Override
	public void importSeries(CCSeries o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException
	{

	}

	@Override
	public void importSeason(CCSeason o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException
	{

	}

	@Override
	public void importEpisode(CCEpisode o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException
	{

	}
}
