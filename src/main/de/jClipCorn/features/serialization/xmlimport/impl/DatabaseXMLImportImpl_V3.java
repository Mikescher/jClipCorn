package de.jClipCorn.features.serialization.xmlimport.impl;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.serialization.xmlimport.IDatabaseXMLImporterImpl;
import de.jClipCorn.features.serialization.xmlimport.ImportState;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.helper.ByteUtilies;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

import java.awt.image.BufferedImage;

@SuppressWarnings("nls")
public class DatabaseXMLImportImpl_V3 implements IDatabaseXMLImporterImpl
{
	public void importDatabaseElement(CCDatabaseElement o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException
	{
		e.execIfAttrExists("title", o::setTitle);
		e.execIfAttrExists("genres", v -> o.setGenres(CCGenreList.deserialize(v)));
		e.execIfIntAttrExists("onlinescore", o::setOnlinescore);
		e.execIfIntAttrExists("fsk", o::setFsk);
		e.execIfIntAttrExists("score", o::setScore);
		e.execIfAttrExists("tags", v -> o.setTags(CCTagList.deserialize(v)));

		if (s.ResetTags) o.setTags(CCTagList.EMPTY);

		if (s.ResetScore) o.setScore(CCUserScore.RATING_NO);

		if (!s.IgnoreCoverData && e.hasAttribute("coverdata")) {
			o.setCover(-1); //Damit er nicht probiert was zu löschen
			o.setCover(ImageUtilities.byteArrayToImage(ByteUtilies.hexStringToByteArray(e.getAttributeValueOrThrow("coverdata"))));
		} else if (e.hasAttribute("covername")) {
			BufferedImage img = imgf.invoke(e.getAttributeValueOrThrow("covername"));
			if (img != null) {
				o.setCover(-1); //Damit er nicht probiert was zu löschen
				o.setCover(img);
			}
		}

		e.execIfAttrExists("groups", o::setGroups);
		e.execIfAttrExists("onlinreref", o::setOnlineReference);
	}

	@Override
	public void importMovie(CCMovie o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			importDatabaseElement(o, e, imgf, s);

			e.execIfAttrExists("adddate", v -> o.setAddDate(CCDate.deserializeSQL(v)));

			if (s.ResetAddDate) o.setAddDate(CCDate.getCurrentDate());

			e.execIfLongAttrExists("filesize", o::setFilesize);
			e.execIfIntAttrExists("format", o::setFormat);
			e.execIfIntAttrExists("length", o::setLength);
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
	public void importSeries(CCSeries o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			importDatabaseElement(o, e, imgf, s);

			for (CCXMLElement xchild : e.getAllChildren("season"))
			{
				importSeason(o.createNewEmptySeason(), xchild, imgf, s);
			}
		}
		o.endUpdating();
	}

	@Override
	public void importSeason(CCSeason o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			e.execIfAttrExists("title", o::setTitle);
			e.execIfIntAttrExists("year", o::setYear);

			for (CCXMLElement xchild : e.getAllChildren("episode"))
			{
				importEpisode(o.createNewEmptyEpisode(), xchild, imgf, s);
			}

			if (!s.IgnoreCoverData && e.hasAttribute("coverdata")) {
				o.setCover(-1); //Damit er nicht probiert was zu löschen
				o.setCover(ImageUtilities.byteArrayToImage(ByteUtilies.hexStringToByteArray(e.getAttributeValueOrThrow("coverdata"))));
			} else if (e.hasAttribute("covername")) {
				BufferedImage img = imgf.invoke(e.getAttributeValueOrThrow("covername"));
				if (img != null) {
					o.setCover(-1); //Damit er nicht probiert was zu löschen
					o.setCover(img);
				}
			}
		}
		o.endUpdating();
	}

	@Override
	public void importEpisode(CCEpisode o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			e.execIfAttrExists("title", o::setTitle);
			e.execIfBoolAttrExists("viewed", o::setViewed);

			if (s.ResetViewed) o.setViewed(false);

			e.execIfAttrExists("adddate", v -> o.setAddDate(CCDate.deserializeSQL(v)));

			if (s.ResetAddDate) o.setAddDate(CCDate.getCurrentDate());

			e.execIfIntAttrExists("episodenumber", o::setEpisodeNumber);
			e.execIfLongAttrExists("filesize", o::setFilesize);
			e.execIfIntAttrExists("format", o::setFormat);

			e.execIfAttrExists("history", v -> o.setViewedHistory(CCDateTimeList.parse(v)));

			e.execIfIntAttrExists("length", o::setLength);
			e.execIfAttrExists("part", o::setPart);
			e.execIfIntAttrExists("quality", o::setQuality);
			e.execIfAttrExists("tags", v -> o.setTags(CCTagList.deserialize(v)));

			if (s.ResetTags) o.setTags(CCTagList.EMPTY);

			e.execIfAttrExists("languages", v -> o.setLanguage(CCDBLanguageList.parseFromString(v)));
		}
		o.endUpdating();
	}
}
