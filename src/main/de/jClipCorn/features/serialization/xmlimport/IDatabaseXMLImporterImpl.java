package de.jClipCorn.features.serialization.xmlimport;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

public interface IDatabaseXMLImporterImpl {
	void importMovie(CCMovie o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException;
	void importSeries(CCSeries o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException;
	void importSeason(CCSeason o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException;
	void importEpisode(CCEpisode o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException;
}
