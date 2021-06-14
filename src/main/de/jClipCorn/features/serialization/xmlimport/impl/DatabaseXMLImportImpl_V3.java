package de.jClipCorn.features.serialization.xmlimport.impl;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.serialization.xmlimport.IDatabaseXMLImporterImpl;
import de.jClipCorn.features.serialization.xmlimport.ImportState;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
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
		e.execIfAttrExists("title", v -> o.title().set(v));
		e.execIfAttrExists("genres", v -> o.Genres.set(CCGenreList.deserialize(v)));
		e.execIfIntAttrExists("onlinescore", v -> o.onlineScore().set(v));
		e.execIfIntAttrExists("fsk", v -> o.fsk().set(v));
		e.execIfIntAttrExists("score", v -> o.score().set(v));
		e.execIfAttrExists("tags", v -> o.Tags.set(CCTagList.deserialize(v)));

		if (s.ResetTags) o.Tags.set(CCTagList.EMPTY);

		if (s.ResetScore) o.Score.set(CCUserScore.RATING_NO);

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
		e.execIfAttrExists("onlinreref", v -> o.onlineReference().set(v));
	}

	@Override
	public void importMovie(CCMovie o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			importDatabaseElement(o, e, imgf, s);

			e.execIfAttrExists("adddate", v -> o.AddDate.set(CCDate.deserializeSQL(v)));

			if (s.ResetAddDate) o.AddDate.set(CCDate.getCurrentDate());

			e.execIfLongAttrExists("filesize", v -> o.fileSize().set(v));
			e.execIfIntAttrExists("format", v -> o.format().set(v));
			e.execIfIntAttrExists("length", v -> o.length().set(v));
			e.execIfAttrExists("languages", v -> o.Language.set(CCDBLanguageList.parseFromString(v)));

			for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
				int fi = i;
				e.execIfAttrExists("part_"+i, v -> o.Parts.set(fi, v));
			}

			e.execIfIntAttrExists("year", v -> o.year().set(v));
			e.execIfAttrExists("zyklus", v -> o.zyklus().setTitle(v));
			e.execIfIntAttrExists("zyklusnumber", v -> o.zyklus().setNumber(v));
			e.execIfAttrExists("history", v -> o.viewedHistory().set(v));

			if (!o.isViewed() && e.hasAttribute("viewed") && e.getAttributeBoolValueOrThrow("viewed")) o.ViewedHistory.add(CCDateTime.getUnspecified());
			if (s.ResetViewed) o.ViewedHistory.set(CCDateTimeList.createEmpty());
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
			e.execIfAttrExists("title", v -> o.title().set(v));
			e.execIfIntAttrExists("year", v -> o.year().set(v));

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
			e.execIfAttrExists("title", v -> o.title().set(v));

			e.execIfAttrExists("adddate", v -> o.AddDate.set(CCDate.deserializeSQL(v)));

			if (s.ResetAddDate) o.AddDate.set(CCDate.getCurrentDate());

			e.execIfIntAttrExists("episodenumber", v -> o.episodeNumber().set(v));
			e.execIfLongAttrExists("filesize", v -> o.fileSize().set(v));
			e.execIfIntAttrExists("format", v -> o.format().set(v));

			e.execIfAttrExists("history", v -> o.ViewedHistory.set(CCDateTimeList.parse(v)));

			e.execIfIntAttrExists("length", v -> o.length().set(v));
			e.execIfAttrExists("part", v -> o.part().set(v));
			e.execIfAttrExists("tags", v -> o.Tags.set(CCTagList.deserialize(v)));

			if (s.ResetTags) o.Tags.set(CCTagList.EMPTY);

			e.execIfAttrExists("languages", v -> o.Language.set(CCDBLanguageList.parseFromString(v)));

			if (!o.isViewed() && e.hasAttribute("viewed") && e.getAttributeBoolValueOrThrow("viewed")) o.addToViewedHistory(CCDateTime.getUnspecified());
			if (s.ResetViewed) o.ViewedHistory.set(CCDateTimeList.createEmpty());
		}
		o.endUpdating();
	}
}
