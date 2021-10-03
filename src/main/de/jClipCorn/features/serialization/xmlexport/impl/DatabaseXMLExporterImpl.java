package de.jClipCorn.features.serialization.xmlexport.impl;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.serialization.xmlexport.ExportOptions;
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

	public static void exportDatabaseElement(Element e, CCDatabaseElement o, ExportOptions s) {
		if (s.ExportLocalID) e.setAttribute("localid",      o.getLocalID() + "");

		e.setAttribute("title",        o.getTitle());
		e.setAttribute("genres",       o.getGenres().serialize());
		e.setAttribute("onlinescore",  String.valueOf(o.getOnlinescore().asInt()));
		e.setAttribute("fsk",          String.valueOf(o.getFSK().asInt()));
		e.setAttribute("score",        String.valueOf(o.Score.get().asInt()));
		e.setAttribute("groups",       o.getGroups().toSerializationString());
		e.setAttribute("onlinreref",   o.getOnlineReference().toSerializationString());
		e.setAttribute("tags",         o.getTags().serialize());

		if (! s.CoverData) e.setAttribute("covername", o.getCoverInfo().Filename);
		if (! s.CoverData) e.setAttribute("coverid", o.getCoverInfo().ID + "");
		if (s.CoverHash) e.setAttribute("coverhash", o.getCoverInfo().Checksum);
		
		if (s.CoverData) e.setAttribute("coverdata", ByteUtilies.byteArrayToHexString(ImageUtilities.imageToByteArray(o.getCover())));
	}

	public static void exportMovie(Element e, CCMovie o, ExportOptions s) {
		exportDatabaseElement(e, o, s);

		e.setAttribute("adddate",      o.getAddDate().toStringSQL());
		e.setAttribute("filesize",     o.getFilesize().getBytes() + "");
		e.setAttribute("format",       o.getFormat().asInt() + "");
		e.setAttribute("length",       o.getLength()  + "");
		e.setAttribute("languages",    o.getLanguage().serializeToString());
		e.setAttribute("subtitles",    o.Subtitles.get().serializeToLongString());

		for (int i = 0; i < CCMovie.PARTCOUNT_MAX; i++) {
			var p = o.Parts.get(i);
			if (!p.isEmpty()) e.setAttribute("part_"+i, p.toString());
		}

		e.setAttribute("history",      o.ViewedHistory.get().toSerializationString());
		e.setAttribute("year",         o.getYear() + "");
		e.setAttribute("zyklus",       o.getZyklus().getTitle());
		e.setAttribute("zyklusnumber", o.getZyklus().getNumber() + "");

		CCMediaInfo minfo = o.mediaInfo().get();
		if (minfo.isSet()) {
			e.setAttribute("mediainfo.filesize",        minfo.getFilesize().getBytes()+"");
			e.setAttribute("mediainfo.cdate",           minfo.getCDate()+"");
			e.setAttribute("mediainfo.mdate",           minfo.getMDate()+"");
			e.setAttribute("mediainfo.audioformat",     minfo.getAudioFormat());
			e.setAttribute("mediainfo.videoformat",     minfo.getVideoFormat());
			e.setAttribute("mediainfo.width",           minfo.getWidth()+"");
			e.setAttribute("mediainfo.height",          minfo.getHeight()+"");
			e.setAttribute("mediainfo.framerate",       Double.toString(minfo.getFramerate()));
			e.setAttribute("mediainfo.duration",        Double.toString(minfo.getDuration()));
			e.setAttribute("mediainfo.bitdepth",        minfo.getBitdepth()+"");
			e.setAttribute("mediainfo.bitrate",         minfo.getBitrate()+"");
			e.setAttribute("mediainfo.framecount",      minfo.getFramecount()+"");
			e.setAttribute("mediainfo.audiochannels",   minfo.getAudioChannels()+"");
			e.setAttribute("mediainfo.videocodec",      minfo.getVideoCodec());
			e.setAttribute("mediainfo.audiocodec",      minfo.getAudioCodec());
			e.setAttribute("mediainfo.audiosamplerate", minfo.getAudioSamplerate()+"");
			e.setAttribute("mediainfo.checksum",        minfo.getChecksum());
		}

		if (s.FileHash) e.setAttribute("filehash", o.getFastViewHashSafe());
	}

	public static void exportSeries(Element e, CCSeries o, ExportOptions s) {
		exportDatabaseElement(e, o, s);

		// series has no more attributes than CCDatabaseElement - for now
	}

	public static void exportSeason(Element e, CCSeason o, ExportOptions s) {
		if (s.ExportLocalID) e.setAttribute("seasonid", o.getLocalID() + "");

		e.setAttribute("title", o.getTitle());
		e.setAttribute("year", o.getYear() + "");

		if (! s.CoverData) e.setAttribute("covername", o.getCoverInfo().Filename);
		if (! s.CoverData) e.setAttribute("coverid", o.getCoverInfo().ID + "");

		if (s.CoverHash) e.setAttribute("coverhash", o.getCoverInfo().Checksum);

		if (s.CoverData) e.setAttribute("coverdata", ByteUtilies.byteArrayToHexString(ImageUtilities.imageToByteArray(o.getCover())));
	}

	public static void exportEpisode(Element e, CCEpisode o, ExportOptions s) {
		if (s.ExportLocalID) e.setAttribute("localid",       o.getLocalID() + "");

		e.setAttribute("title",         o.getTitle());
		e.setAttribute("adddate",       o.getAddDate().toStringSQL());
		e.setAttribute("episodenumber", o.getEpisodeNumber() + "");
		e.setAttribute("filesize",      o.getFilesize().getBytes() + "");
		e.setAttribute("format",        o.getFormat().asInt() + "");
		e.setAttribute("history",       o.ViewedHistory.get().toSerializationString());
		e.setAttribute("length",        o.getLength() + "");
		e.setAttribute("part",          o.getPart().toString());
		e.setAttribute("tags",          o.getTags().serialize());
		e.setAttribute("languages",     o.getLanguage().serializeToString());
		e.setAttribute("subtitles",     o.Subtitles.get().serializeToLongString());

		CCMediaInfo minfo = o.mediaInfo().get();
		if (minfo.isSet()) {
			e.setAttribute("mediainfo.filesize",        minfo.getFilesize().getBytes()+"");
			e.setAttribute("mediainfo.cdate",           minfo.getCDate()+"");
			e.setAttribute("mediainfo.mdate",           minfo.getMDate()+"");
			e.setAttribute("mediainfo.audioformat",     minfo.getAudioFormat());
			e.setAttribute("mediainfo.videoformat",     minfo.getVideoFormat());
			e.setAttribute("mediainfo.width",           minfo.getWidth()+"");
			e.setAttribute("mediainfo.height",          minfo.getHeight()+"");
			e.setAttribute("mediainfo.framerate",       Double.toString(minfo.getFramerate()));
			e.setAttribute("mediainfo.duration",        Double.toString(minfo.getDuration()));
			e.setAttribute("mediainfo.bitdepth",        minfo.getBitdepth()+"");
			e.setAttribute("mediainfo.bitrate",         minfo.getBitrate()+"");
			e.setAttribute("mediainfo.framecount",      minfo.getFramecount()+"");
			e.setAttribute("mediainfo.audiochannels",   minfo.getAudioChannels()+"");
			e.setAttribute("mediainfo.videocodec",      minfo.getVideoCodec());
			e.setAttribute("mediainfo.audiocodec",      minfo.getAudioCodec());
			e.setAttribute("mediainfo.audiosamplerate", minfo.getAudioSamplerate()+"");
			e.setAttribute("mediainfo.checksum",        minfo.getChecksum());
		}

		if (s.FileHash) e.setAttribute("filehash", o.getFastViewHashSafe());
	}
}
