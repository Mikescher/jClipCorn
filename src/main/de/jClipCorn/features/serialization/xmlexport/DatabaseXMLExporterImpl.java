package de.jClipCorn.features.serialization.xmlexport;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.ByteUtilies;
import de.jClipCorn.util.helper.ImageUtilities;
import org.jdom2.Document;
import org.jdom2.Element;

@SuppressWarnings("nls")
public class DatabaseXMLExporterImpl {

	public static Document createDocument() {
		Document xml = new Document(new Element("database"));

		Element root = xml.getRootElement();

		root.setAttribute("version", Main.VERSION);
		root.setAttribute("dbversion", Main.DBVERSION);
		root.setAttribute("xmlversion", Main.JXMLVER);
		root.setAttribute("date", CCDate.getCurrentDate().toStringSerialize());

		return xml;
	}

	public static void exportDatabaseElement(Element e, CCDatabaseElement o, ExportState s) {

		e.setAttribute("localid",      o.getLocalID() + "");
		e.setAttribute("typ",          o.getType().asInt() + "");
		e.setAttribute("title",        o.getTitle());
		e.setAttribute("genres",       o.getGenres().getAllGenres() + "");
		e.setAttribute("onlinescore",  o.getOnlinescore().asInt() + "");
		e.setAttribute("fsk",          o.getFSK().asInt() + "");
		e.setAttribute("score",        o.getScore().asInt() + "");
		e.setAttribute("seriesid",     o.getSeriesID() + "");
		e.setAttribute("groups",       o.getGroups().toSerializationString());
		e.setAttribute("onlinreref",   o.getOnlineReference().toSerializationString());
		e.setAttribute("tags",         o.getTags().asShort() + "");

		if (! s.CoverData) e.setAttribute("covername", o.getCoverName());
		if (s.CoverHash) e.setAttribute("coverhash", o.getCoverMD5());
		if (s.CoverData) e.setAttribute("coverdata", ByteUtilies.byteArrayToHexString(ImageUtilities.imageToByteArray(o.getCover())));
	}

	public static void exportMovie(Element e, CCMovie o, ExportState s) {
		exportDatabaseElement(e, o, s);

		e.setAttribute("adddate",      o.getAddDate().toStringSerialize());
		e.setAttribute("filesize",     o.getFilesize().getBytes() + "");
		e.setAttribute("format",       o.getFormat().asInt() + "");
		e.setAttribute("length",       o.getLength()  + "");
		e.setAttribute("languages",    o.getLanguage().serializeToString());

		for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
			String p = o.getPart(i);
			if (!Str.isNullOrEmpty(p)) e.setAttribute("part_"+i, p);
		}

		e.setAttribute("quality",      o.getQuality().asInt() + "");
		e.setAttribute("viewed",       o.isViewed() + "");
		e.setAttribute("history",      o.getViewedHistory().toSerializationString());
		e.setAttribute("year",         o.getYear() + "");
		e.setAttribute("zyklus",       o.getZyklus().getTitle());
		e.setAttribute("zyklusnumber", o.getZyklus().getNumber() + "");

		if (s.FileHash) e.setAttribute("filehash", o.getFastMD5());
	}

	public static void exportSeries(Element e, CCSeries o, ExportState s) {
		exportDatabaseElement(e, o, s);

		// series has no more attributes than CCDatabaseElement - for now
	}

	public static void exportSeason(Element e, CCSeason o, ExportState s) {
		e.setAttribute("seasonid", o.getSeasonID() + "");
		e.setAttribute("title", o.getTitle());
		e.setAttribute("year", o.getYear() + "");

		if (! s.CoverData) e.setAttribute("covername", o.getCoverName());

		if (s.CoverHash) e.setAttribute("coverhash", o.getCoverMD5());

		if (s.CoverData) e.setAttribute("coverdata", ByteUtilies.byteArrayToHexString(ImageUtilities.imageToByteArray(o.getCover())));
	}

	public static void exportEpisode(Element e, CCEpisode o, ExportState s) {
		e.setAttribute("localid",       o.getLocalID() + "");
		e.setAttribute("title",         o.getTitle());
		e.setAttribute("viewed",        o.isViewed() + "");
		e.setAttribute("adddate",       o.getAddDate().toStringSerialize());
		e.setAttribute("episodenumber", o.getEpisodeNumber() + "");
		e.setAttribute("filesize",      o.getFilesize().getBytes() + "");
		e.setAttribute("format",        o.getFormat().asInt() + "");
		e.setAttribute("history",       o.getViewedHistory().toSerializationString());
		e.setAttribute("length",        o.getLength() + "");
		e.setAttribute("part",          o.getPart());
		e.setAttribute("quality",       o.getQuality().asInt() + "");
		e.setAttribute("tags",          o.getTags().asShort() + "");
		e.setAttribute("languages",     o.getLanguage().serializeToString());

		if (s.FileHash) e.setAttribute("filehash", o.getFastMD5());
	}
}
