package de.jClipCorn.features.serialization.xmlimport;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.features.serialization.xmlimport.impl.*;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.SerializationException;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;
import de.jClipCorn.util.xml.CCXMLParser;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public class DatabaseXMLImporter {

	public static List<CCDatabaseElement> run(String xml, Func0to1<CCMovie> fm, Func0to1<CCSeries> fs, Func1to1<String, BufferedImage> imgf, ImportOptions opt) throws CCXMLException, SerializationException, CCFormatException
	{
		return run(xml, fm, fs, imgf, opt, null);
	}

	public static List<CCDatabaseElement> run(String xml, Func0to1<CCMovie> fm, Func0to1<CCSeries> fs, Func1to1<String, BufferedImage> imgf, ImportOptions opt, ProgressCallbackListener l) throws CCXMLException, SerializationException, CCFormatException
	{
		CCXMLParser doc = CCXMLParser.parse(xml);
		CCXMLElement root = doc.getRoot("database");

		int xmlver = root.getAttributeIntValueOrDefault("xmlversion", 1);

		ImportState state = new ImportState(doc, xmlver, opt);

		IDatabaseXMLImporterImpl impl = getImportImpl(state);

		if (l != null) l.setMax(root.getChildrenCount());

		List<CCDatabaseElement> result = new ArrayList<>();

		try {
			for (CCXMLElement xchild : root.getAllChildren())
			{
				if (xchild.getName().equals("movie")) { //$NON-NLS-1$
					CCMovie mov = fm.invoke();
					impl.importMovie(mov, xchild, imgf, state);
					result.add(mov);
				} else if (xchild.getName().equals("series")) { //$NON-NLS-1$
					CCSeries ser = fs.invoke();
					impl.importSeries(ser, xchild, imgf, state);
					result.add(ser);
				}

				if (l != null) l.step();
			}
		} catch (NumberFormatException e) {
			throw new SerializationException(e.getMessage(), e);
		}

		return result;
	}

	private static IDatabaseXMLImporterImpl getImportImpl(ImportState s) throws SerializationException
	{
		if (s.XMLVersion == 1) return new DatabaseXMLImportImpl_V1();
		if (s.XMLVersion == 2) return new DatabaseXMLImportImpl_V2();
		if (s.XMLVersion == 3) return new DatabaseXMLImportImpl_V3();
		if (s.XMLVersion == 4) return new DatabaseXMLImportImpl_V4();
		if (s.XMLVersion == 5) return new DatabaseXMLImportImpl_V5();
		if (s.XMLVersion == 6) return new DatabaseXMLImportImpl_V6();

		throw new SerializationException("Unknown XMLVersion: " + s.XMLVersion);
	}

	public static void parseSingleMovie(CCMovie mov, CCXMLElement xml, Func1to1<String, BufferedImage> imgf, ImportState state) throws SerializationException, CCFormatException, CCXMLException
	{
		try {
			IDatabaseXMLImporterImpl impl = getImportImpl(state);
			impl.importMovie(mov, xml, imgf, state);
		} catch (NumberFormatException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	public static void parseSingleSeries(CCSeries ser, CCXMLElement xml, Func1to1<String, BufferedImage> imgf, ImportState state) throws SerializationException, CCFormatException, CCXMLException
	{
		try {
			IDatabaseXMLImporterImpl impl = getImportImpl(state);
			impl.importSeries(ser, xml, imgf, state);
		} catch (NumberFormatException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	public static void parseSingleSeason(CCSeason sea, CCXMLElement xml, Func1to1<String, BufferedImage> imgf, ImportState state) throws SerializationException, CCFormatException, CCXMLException
	{
		try {
			IDatabaseXMLImporterImpl impl = getImportImpl(state);
			impl.importSeason(sea, xml, imgf, state);
		} catch (NumberFormatException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	public static void parseSingleEpisode(CCEpisode epi, CCXMLElement xml, Func1to1<String, BufferedImage> imgf, ImportState state) throws SerializationException, CCFormatException, CCXMLException
	{
		try {
			IDatabaseXMLImporterImpl impl = getImportImpl(state);
			impl.importEpisode(epi, xml, imgf, state);
		} catch (NumberFormatException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}
}
