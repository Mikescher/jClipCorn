package de.jClipCorn.features.serialization.xmlimport;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

import java.awt.image.BufferedImage;

public interface IDatabaseXMLImporterImpl {
	void importMovie(CCMovie o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException;
	void importSeries(CCSeries o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException;
	void importSeason(CCSeason o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException;
	void importEpisode(CCEpisode o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException;
}
