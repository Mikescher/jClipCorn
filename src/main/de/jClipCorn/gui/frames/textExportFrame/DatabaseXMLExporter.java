package de.jClipCorn.gui.frames.textExportFrame;

import java.util.List;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.serialization.ExportHelper;

public class DatabaseXMLExporter extends DatabaseTextExporter {

	@Override
	protected String getFormatName() {
		return "XML"; //$NON-NLS-1$
	}

	@Override
	@SuppressWarnings("nls")
	protected String create() {	
		Document document = new Document(new Element("Database"));
		Element root = document.getRootElement();

		List<CCDatabaseElement> elements = getOrderedList();
		
		for (int i = 0; i < elements.size(); i++) {
			CCDatabaseElement ccd_el = elements.get(i);

			if (ccd_el.isMovie()) {				
				CCMovie mov = (CCMovie) ccd_el;

				Element jdom_elem = new Element("movie");
				root.addContent(jdom_elem);
				
				if (mov.getZyklus().isSet()) {
					jdom_elem.setAttribute(new Attribute("zyklus", mov.getZyklus().getTitle()));
					jdom_elem.setAttribute(new Attribute("zyklusnumber", mov.getZyklus().getNumber() + ""));
				}
				
				jdom_elem.setAttribute(new Attribute("title", mov.getTitle()));
				
				if (addLanguage) jdom_elem.setAttribute(new Attribute("languages", mov.getLanguage().iterate().stringjoin(CCDBLanguage::getLongString, ";")));
				if (addFormat) jdom_elem.setAttribute(new Attribute("format", mov.getFormat().asString()));
				if (addYear) jdom_elem.setAttribute(new Attribute("year", mov.getYear() + ""));
				if (addSize) jdom_elem.setAttribute(new Attribute("size", mov.getFilesize().getBytes() + " bytes"));
				if (addViewed) jdom_elem.setAttribute(new Attribute("viewed", mov.isViewed() ? "true" : "false"));
				
			} else if (ccd_el.isSeries()) {
				CCSeries ser = (CCSeries) ccd_el;
				
				Element jdom_elem_ser = new Element("series");
				root.addContent(jdom_elem_ser);
				
				jdom_elem_ser.setAttribute(new Attribute("title", ser.getTitle()));

				for (int j = 0; j < ser.getSeasonCount(); j++) {
					CCSeason season = ser.getSeasonByArrayIndex(j);

					Element jdom_elem_sea = new Element("season");
					jdom_elem_ser.addContent(jdom_elem_sea);

					jdom_elem_sea.setAttribute(new Attribute("title", season.getTitle()));
					if (addYear) jdom_elem_sea.setAttribute(new Attribute("year", season.getYear() + ""));
					
					for (int k = 0; k < season.getEpisodeCount(); k++) {
						CCEpisode episode = season.getEpisodeByArrayIndex(k);
						
						Element jdom_elem_epi = new Element("episode");
						jdom_elem_sea.addContent(jdom_elem_epi);
						
						jdom_elem_epi.setAttribute(new Attribute("title", episode.getTitle()));
						if (addLanguage) jdom_elem_epi.setAttribute(new Attribute("language", episode.getLanguage().iterate().stringjoin(CCDBLanguage::getLongString, ";")));
						if (addFormat) jdom_elem_epi.setAttribute(new Attribute("format", episode.getFormat().asString()));
						if (addSize) jdom_elem_epi.setAttribute(new Attribute("size", episode.getFilesize().getBytes() + " bytes"));
						if (addViewed) jdom_elem_epi.setAttribute(new Attribute("viewed", episode.isViewed() ? "true" : "false"));
					}

				}
			}
		}
		
		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat());
		return xout.outputString(document);
	}

	@Override
	protected String getFileExtension() {
		return ExportHelper.EXTENSION_TEXTEXPORT_XML;
	}
}
