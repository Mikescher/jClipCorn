package de.jClipCorn.features.serialization.xmlimport.impl;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.serialization.xmlimport.IDatabaseXMLImporterImpl;
import de.jClipCorn.features.serialization.xmlimport.ImportState;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.helper.ByteUtilies;
import de.jClipCorn.util.helper.ImageUtilities;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.xml.CCXMLElement;
import de.jClipCorn.util.xml.CCXMLException;

import java.awt.image.BufferedImage;

@SuppressWarnings("nls")
public class DatabaseXMLImportImpl_V5 implements IDatabaseXMLImporterImpl
{
	public void importDatabaseElement(CCDatabaseElement o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException
	{
		e.execIfAttrExists("title", o.Title::set);
		e.execIfAttrExists("genres", v -> o.Genres.set(CCGenreList.deserialize(v)));
		e.execIfIntAttrExists("onlinescore", o.OnlineScore::set);
		e.execIfIntAttrExists("fsk", o.FSK::set);
		e.execIfIntAttrExists("score", o.Score::set);
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

		e.execIfAttrExists("groups", o.Groups::set);
		e.execIfAttrExists("onlinreref", o.OnlineReference::set);
	}

	@Override
	public void importMovie(CCMovie o, CCXMLElement e, Func1to1<String, BufferedImage> imgf, ImportState s) throws CCFormatException, CCXMLException
	{
		o.beginUpdating();
		{
			importDatabaseElement(o, e, imgf, s);

			e.execIfAttrExists("adddate", v -> o.AddDate.set(CCDate.deserializeSQL(v)));

			if (s.ResetAddDate) o.AddDate.set(CCDate.getCurrentDate());

			e.execIfLongAttrExists("filesize", o.FileSize::set);
			e.execIfIntAttrExists("format", o.Format::set);
			e.execIfIntAttrExists("length", o.Length::set);
			e.execIfAttrExists("languages", v -> o.Language.set(CCDBLanguageList.parseFromString(v)));

			for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
				int fi = i;
				e.execIfAttrExists("part_"+i, v -> o.Parts.set(fi, v));
			}

			e.execIfIntAttrExists("year", o.Year::set);
			e.execIfAttrExists("zyklus", o.Zyklus::setTitle);
			e.execIfIntAttrExists("zyklusnumber", o.Zyklus::setNumber);
			e.execIfAttrExists("history", o.ViewedHistory::set);

			if (s.ResetViewed) o.ViewedHistory.set(CCDateTimeList.createEmpty());

			if (e.hasAllAttributes("mediainfo.filesize", "mediainfo.cdate", "mediainfo.mdate", "mediainfo.audioformat", "mediainfo.videoformat", "mediainfo.width", "mediainfo.height", "mediainfo.framerate", "mediainfo.duration", "mediainfo.bitdepth", "mediainfo.bitrate", "mediainfo.framecount", "mediainfo.audiochannels", "mediainfo.videocodec", "mediainfo.audiocodec", "mediainfo.audiosamplerate"))
			{
				o.MediaInfo.set(new CCMediaInfo(
					e.getAttributeLongValueOrThrow("mediainfo.cdate"),
					e.getAttributeLongValueOrThrow("mediainfo.mdate"),
					e.getAttributeLongValueOrThrow("mediainfo.filesize"),
					e.getAttributeDoubleValueOrThrow("mediainfo.duration"),
					e.getAttributeIntValueOrThrow("mediainfo.bitrate"),
					e.getAttributeValueOrThrow("mediainfo.videoformat"),
					e.getAttributeIntValueOrThrow("mediainfo.width"),
					e.getAttributeIntValueOrThrow("mediainfo.height"),
					e.getAttributeDoubleValueOrThrow("mediainfo.framerate"),
					e.getAttributeShortValueOrThrow("mediainfo.bitdepth"),
					e.getAttributeIntValueOrThrow("mediainfo.framecount"),
					e.getAttributeValueOrThrow("mediainfo.videocodec"),
					e.getAttributeValueOrThrow("mediainfo.audioformat"),
					e.getAttributeShortValueOrThrow("mediainfo.audiochannels"),
					e.getAttributeValueOrThrow("mediainfo.audiocodec"),
					e.getAttributeIntValueOrThrow("mediainfo.audiosamplerate"),
					Str.Empty));
			}
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
			e.execIfAttrExists("title", o.Title::set);
			e.execIfIntAttrExists("year", o.Year::set);

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
			e.execIfAttrExists("title", o.Title::set);

			e.execIfAttrExists("adddate", v -> o.AddDate.set(CCDate.deserializeSQL(v)));

			if (s.ResetAddDate) o.AddDate.set(CCDate.getCurrentDate());

			e.execIfIntAttrExists("episodenumber", o.EpisodeNumber::set);
			e.execIfLongAttrExists("filesize", o.FileSize::set);
			e.execIfIntAttrExists("format", o.Format::set);

			e.execIfAttrExists("history", v -> o.ViewedHistory.set(CCDateTimeList.parse(v)));

			e.execIfIntAttrExists("length", o.Length::set);
			e.execIfAttrExists("part", o.Part::set);
			e.execIfAttrExists("tags", v -> o.Tags.set(CCTagList.deserialize(v)));

			if (s.ResetTags) o.Tags.set(CCTagList.EMPTY);

			e.execIfAttrExists("languages", v -> o.Language.set(CCDBLanguageList.parseFromString(v)));

			if (s.ResetViewed) o.ViewedHistory.set(CCDateTimeList.createEmpty());

			if (e.hasAllAttributes("mediainfo.filesize", "mediainfo.cdate", "mediainfo.mdate", "mediainfo.audioformat", "mediainfo.videoformat", "mediainfo.width", "mediainfo.height", "mediainfo.framerate", "mediainfo.duration", "mediainfo.bitdepth", "mediainfo.bitrate", "mediainfo.framecount", "mediainfo.audiochannels", "mediainfo.videocodec", "mediainfo.audiocodec", "mediainfo.audiosamplerate"))
			{
				o.MediaInfo.set(new CCMediaInfo(
					e.getAttributeLongValueOrThrow("mediainfo.cdate"),
					e.getAttributeLongValueOrThrow("mediainfo.mdate"),
					e.getAttributeLongValueOrThrow("mediainfo.filesize"),
					e.getAttributeDoubleValueOrThrow("mediainfo.duration"),
					e.getAttributeIntValueOrThrow("mediainfo.bitrate"),
					e.getAttributeValueOrThrow("mediainfo.videoformat"),
					e.getAttributeIntValueOrThrow("mediainfo.width"),
					e.getAttributeIntValueOrThrow("mediainfo.height"),
					e.getAttributeDoubleValueOrThrow("mediainfo.framerate"),
					e.getAttributeShortValueOrThrow("mediainfo.bitdepth"),
					e.getAttributeIntValueOrThrow("mediainfo.framecount"),
					e.getAttributeValueOrThrow("mediainfo.videocodec"),
					e.getAttributeValueOrThrow("mediainfo.audioformat"),
					e.getAttributeShortValueOrThrow("mediainfo.audiochannels"),
					e.getAttributeValueOrThrow("mediainfo.audiocodec"),
					e.getAttributeIntValueOrThrow("mediainfo.audiosamplerate"),
					Str.Empty));
			}
		}
		o.endUpdating();
	}
}
