package de.jClipCorn.features.serialization.xmlexport.impl;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.*;
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
		e.setAttribute("onlinescore",  String.valueOf(o.getOnlinescore().toSerializationString()));
		e.setAttribute("fsk",          String.valueOf(o.getFSK().asInt()));
		e.setAttribute("score",        String.valueOf(o.Score.get().asInt()));
		e.setAttribute("comment",      o.ScoreComment.get());
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
		e.setAttribute("year",         String.valueOf((o.getYear())));
		e.setAttribute("zyklus",       o.getZyklus().getTitle());
		e.setAttribute("zyklusnumber", String.valueOf((o.getZyklus().getNumber())));

		var minfo = o.mediaInfo().get();

		minfo.Filesize        .ifPresent( v -> e.setAttribute("mediainfo.filesize",        String.valueOf(v.getBytes())));
		minfo.CDate           .ifPresent( v -> e.setAttribute("mediainfo.cdate",           String.valueOf(v)));
		minfo.MDate           .ifPresent( v -> e.setAttribute("mediainfo.mdate",           String.valueOf(v)));
		minfo.AudioFormat     .ifPresent( v -> e.setAttribute("mediainfo.audioformat",     v));
		minfo.VideoFormat     .ifPresent( v -> e.setAttribute("mediainfo.videoformat",     v));
		minfo.Width           .ifPresent( v -> e.setAttribute("mediainfo.width",           String.valueOf(v)));
		minfo.Height          .ifPresent( v -> e.setAttribute("mediainfo.height",          String.valueOf(v)));
		minfo.Framerate       .ifPresent( v -> e.setAttribute("mediainfo.framerate",       Double.toString(v)));
		minfo.Duration        .ifPresent( v -> e.setAttribute("mediainfo.duration",        Double.toString(v)));
		minfo.Bitdepth        .ifPresent( v -> e.setAttribute("mediainfo.bitdepth",        String.valueOf(v)));
		minfo.Bitrate         .ifPresent( v -> e.setAttribute("mediainfo.bitrate",         String.valueOf(v)));
		minfo.Framecount      .ifPresent( v -> e.setAttribute("mediainfo.framecount",      String.valueOf(v)));
		minfo.AudioChannels   .ifPresent( v -> e.setAttribute("mediainfo.audiochannels",   String.valueOf(v)));
		minfo.VideoCodec      .ifPresent( v -> e.setAttribute("mediainfo.videocodec",      v));
		minfo.AudioCodec      .ifPresent( v -> e.setAttribute("mediainfo.audiocodec",      v));
		minfo.AudioSamplerate .ifPresent( v -> e.setAttribute("mediainfo.audiosamplerate", String.valueOf(v)));
		minfo.Checksum        .ifPresent( v -> e.setAttribute("mediainfo.checksum",        v));

		if (s.FileHash) e.setAttribute("filehash", o.getFastViewHashSafe());
	}

	public static void exportSeries(Element e, CCSeries o, ExportOptions s) {
		exportDatabaseElement(e, o, s);

		// series has no more attributes than CCDatabaseElement - for now
	}

	public static void exportSeason(Element e, CCSeason o, ExportOptions s) {
		if (s.ExportLocalID) e.setAttribute("seasonid", o.getLocalID() + "");

		e.setAttribute("title",   o.getTitle());
		e.setAttribute("year",    o.getYear() + "");
		e.setAttribute("score",   String.valueOf(o.Score.get().asInt()));
		e.setAttribute("comment", o.ScoreComment.get());

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
		e.setAttribute("score",         String.valueOf(o.Score.get().asInt()));
		e.setAttribute("comment",       o.ScoreComment.get());

		var minfo = o.mediaInfo().get();

		minfo.Filesize        .ifPresent( v -> e.setAttribute("mediainfo.filesize",        String.valueOf(v.getBytes())));
		minfo.CDate           .ifPresent( v -> e.setAttribute("mediainfo.cdate",           String.valueOf(v)));
		minfo.MDate           .ifPresent( v -> e.setAttribute("mediainfo.mdate",           String.valueOf(v)));
		minfo.AudioFormat     .ifPresent( v -> e.setAttribute("mediainfo.audioformat",     v));
		minfo.VideoFormat     .ifPresent( v -> e.setAttribute("mediainfo.videoformat",     v));
		minfo.Width           .ifPresent( v -> e.setAttribute("mediainfo.width",           String.valueOf(v)));
		minfo.Height          .ifPresent( v -> e.setAttribute("mediainfo.height",          String.valueOf(v)));
		minfo.Framerate       .ifPresent( v -> e.setAttribute("mediainfo.framerate",       Double.toString(v)));
		minfo.Duration        .ifPresent( v -> e.setAttribute("mediainfo.duration",        Double.toString(v)));
		minfo.Bitdepth        .ifPresent( v -> e.setAttribute("mediainfo.bitdepth",        String.valueOf(v)));
		minfo.Bitrate         .ifPresent( v -> e.setAttribute("mediainfo.bitrate",         String.valueOf(v)));
		minfo.Framecount      .ifPresent( v -> e.setAttribute("mediainfo.framecount",      String.valueOf(v)));
		minfo.AudioChannels   .ifPresent( v -> e.setAttribute("mediainfo.audiochannels",   String.valueOf(v)));
		minfo.VideoCodec      .ifPresent( v -> e.setAttribute("mediainfo.videocodec",      v));
		minfo.AudioCodec      .ifPresent( v -> e.setAttribute("mediainfo.audiocodec",      v));
		minfo.AudioSamplerate .ifPresent( v -> e.setAttribute("mediainfo.audiosamplerate", String.valueOf(v)));
		minfo.Checksum        .ifPresent( v -> e.setAttribute("mediainfo.checksum",        v));

		if (s.FileHash) e.setAttribute("filehash", o.getFastViewHashSafe());
	}
}
