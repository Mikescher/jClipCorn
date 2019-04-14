package de.jClipCorn.features.serialization.xmlimport.impl;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.serialization.xmlimport.IDatabaseXMLImporterImpl;
import de.jClipCorn.features.serialization.xmlimport.ImportState;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.helper.ByteUtilies;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

@SuppressWarnings("nls")
public class DatabaseXMLImportImpl_V1 implements IDatabaseXMLImporterImpl {

	public void importDatabaseElement(CCDatabaseElement o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException
	{
		e.execIfAttrExists("title", o::setTitle);
		e.execIfLongAttrExists("genres", o::setGenres);
		e.execIfIntAttrExists("onlinescore", o::setOnlinescore);
		e.execIfIntAttrExists("fsk", o::setFsk);
		e.execIfIntAttrExists("score", o::setScore);
		e.execIfShortAttrExists("tags", o::setTags);

		if (s.ResetTags) o.setTags(CCTagList.EMPTY);

		if (s.ResetScore) o.setScore(CCUserScore.RATING_NO);

		e.execIfAttrExists("covername", o::setCover);

		if (!s.IgnoreCoverData && e.hasAttribute("coverdata")) {
			o.setCover(""); //Damit er nicht probiert was zu löschen
			o.setCover(ImageUtilities.byteArrayToImage(ByteUtilies.hexStringToByteArray(e.getAttributeValueOrThrow("coverdata"))));
		}

		e.execIfAttrExists("groups", o::setGroups);
		e.execIfAttrExists("onlinreref", o::setOnlineReference);
	}

	@Override
	public void importMovie(CCMovie o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			importDatabaseElement(o, e, s);

			e.execIfAttrExists("adddate", v -> o.setAddDate(CCDate.deserialize(v)));

			if (s.ResetAddDate) o.setAddDate(CCDate.getCurrentDate());

			e.execIfLongAttrExists("filesize", o::setFilesize);
			e.execIfIntAttrExists("format", o::setFormat);
			e.execIfIntAttrExists("length", o::setLength);
			e.execIfIntAttrExists("language", v -> o.setLanguage(CCDBLanguageList.single(CCDBLanguage.getWrapper().findOrException(v)))); // backwards compatibility
			e.execIfAttrExists("languages", v -> o.setLanguage(CCDBLanguageList.parseFromString(v)));

			for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
				int fi = i;
				e.execIfAttrExists("part_"+i, v -> o.setPart(fi, v));
			}

			e.execIfIntAttrExists("quality", o::setQuality);
			e.execIfBoolAttrExists("viewed", o::setViewed);

			if (s.ResetViewed) o.setViewed(false);

			e.execIfIntAttrExists("year", o::setYear);
			e.execIfAttrExists("zyklus", o::setZyklusTitle);
			e.execIfIntAttrExists("zyklusnumber", o::setZyklusID);
			e.execIfAttrExists("history", o::setViewedHistory);
		}
		o.endUpdating();
	}

	@Override
	public void importSeries(CCSeries o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			importDatabaseElement(o, e, s);

			for (CCXMLElement xchild : e.getAllChildren("season"))
			{
				importSeason(o.createNewEmptySeason(), xchild, s);
			}
		}
		o.endUpdating();
	}

	@Override
	public void importSeason(CCSeason o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			e.execIfAttrExists("title", o::setTitle);
			e.execIfIntAttrExists("year", o::setYear);
			e.execIfAttrExists("covername", o::setCover);

			for (CCXMLElement xchild : e.getAllChildren("episode"))
			{
				importEpisode(o.createNewEmptyEpisode(), xchild, s);
			}

			if (!s.IgnoreCoverData && e.hasAttribute("coverdata")) {
				o.setCover(""); //Damit er nicht probiert was zu löschen
				o.setCover(ImageUtilities.byteArrayToImage(ByteUtilies.hexStringToByteArray(e.getAttributeValueOrThrow("coverdata"))));
			}
		}
		o.endUpdating();
	}

	@Override
	public void importEpisode(CCEpisode o, CCXMLElement e, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			e.execIfAttrExists("title", o::setTitle);
			e.execIfBoolAttrExists("viewed", o::setViewed);

			if (s.ResetViewed) o.setViewed(false);

			e.execIfAttrExists("adddate", v -> o.setAddDate(CCDate.deserialize(v)));

			if (s.ResetAddDate) o.setAddDate(CCDate.getCurrentDate());

			e.execIfIntAttrExists("episodenumber", o::setEpisodeNumber);
			e.execIfLongAttrExists("filesize", o::setFilesize);
			e.execIfIntAttrExists("format", o::setFormat);
			e.execIfAttrExists("lastviewed", v ->  // backwards compatibility
			{
				CCDate d = CCDate.deserialize(v);
				if (!d.isMinimum()) o.setViewedHistory(CCDateTimeList.create(d));
			});

			e.execIfAttrExists("history", v -> o.setViewedHistory(CCDateTimeList.parse(v)));

			e.execIfIntAttrExists("length", o::setLength);
			e.execIfAttrExists("part", o::setPart);
			e.execIfIntAttrExists("quality", o::setQuality);
			e.execIfShortAttrExists("tags", o::setTags);

			if (s.ResetTags) o.setTags(CCTagList.EMPTY);

			e.execIfAttrExists("languages", v -> o.setLanguage(CCDBLanguageList.parseFromString(v)));
		}
		o.endUpdating();
	}
}
