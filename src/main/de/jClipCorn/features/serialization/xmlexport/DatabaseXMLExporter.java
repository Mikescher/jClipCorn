package de.jClipCorn.features.serialization.xmlexport;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.xmlexport.impl.DatabaseXMLExporterImpl;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import org.jdom2.Document;
import org.jdom2.Element;

import java.util.List;

@SuppressWarnings("nls")
public class DatabaseXMLExporter {
	public static Document export(List<CCDatabaseElement> elements, ExportOptions s) {
		return export(elements, s, null);
	}

	public static Document export(List<CCDatabaseElement> elements, ExportOptions s, ProgressCallbackListener l) {

		Document doc = DatabaseXMLExporterImpl.createDocument();
		Element root = doc.getRootElement();

		root.setAttribute("elementcount", elements.size() + "");

		if (l != null) l.setMax(elements.size());

		for (CCDatabaseElement el : elements) {
			root.addContent(export(el, s));
			if (l != null) l.step();
		}

		return doc;
	}

	public static Element export(CCDatabaseElement e, ExportOptions s) {

		if (e.isMovie()) return export(e.asMovie(), s);
		if (e.isSeries()) return export(e.asSeries(), s);

		CCLog.addUndefinied("Element is neither MOVIE nor SERIES");
		return null;
	}

	public static Element export(CCMovie movie, ExportOptions s) {
		Element movie_xml = new Element("movie");
		DatabaseXMLExporterImpl.exportMovie(movie_xml, movie, s);
		return movie_xml;
	}

	public static Element export(CCSeries series, ExportOptions s) {
		Element series_xml = new Element("series");
		DatabaseXMLExporterImpl.exportSeries(series_xml, series, s);

		for (int i = 0; i < series.getSeasonCount(); i++) {
			Element season_xml = new Element("season");
			CCSeason season = series.getSeasonByArrayIndex(i);

			DatabaseXMLExporterImpl.exportSeason(season_xml, season, s);

			for (int j = 0; j < season.getEpisodeCount(); j++) {
				Element episode_xml = new Element("episode");
				CCEpisode episode = season.getEpisodeByArrayIndex(j);

				DatabaseXMLExporterImpl.exportEpisode(episode_xml, episode, s);

				season_xml.addContent(episode_xml);
			}

			series_xml.addContent(season_xml);
		}

		return series_xml;
	}
}
