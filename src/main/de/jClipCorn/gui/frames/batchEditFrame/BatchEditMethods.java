package de.jClipCorn.gui.frames.batchEditFrame;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ChecksumHelper;

import java.io.File;

@SuppressWarnings("CodeBlock2Expr")
public class BatchEditMethods
{
	public static BatchEditMethod<Integer> TITLE_DELETE_FIRST_CHARS = new BatchEditMethod<>((ep, param) ->
	{
		ep.title = ep.title.substring(param);
	});

	public static BatchEditMethod<Integer> PATH_DELETE_FIRST_CHARS = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = ep.part.substring(param);
	});

	public static BatchEditMethod<Integer> TITLE_DELETE_LAST_CHARS = new BatchEditMethod<>((ep, param) ->
	{
		ep.title = ep.title.substring(0, ep.title.length() - param);
	});

	public static BatchEditMethod<Integer> PATH_DELETE_LAST_CHARS = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = ep.part.substring(0, ep.title.length() - param);
	});

	public static BatchEditMethod<Tuple<String, String>> TITLE_STRING_REPLACE = new BatchEditMethod<>((ep, param) ->
	{
		ep.title = ep.title.replace(param.Item1, param.Item2);
	});

	public static BatchEditMethod<Tuple<String, String>> PATH_STRING_REPLACE = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = ep.part.replace(param.Item1, param.Item2);
	});

	public static BatchEditMethod<Tuple<String, String>> TITLE_REGEX_REPLACE = new BatchEditMethod<>((ep, param) ->
	{
		ep.title = ep.title.replaceAll(param.Item1, param.Item2);
	});

	public static BatchEditMethod<Tuple<String, String>> PATH_REGEX_REPLACE = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = ep.part.replaceAll(param.Item1, param.Item2);
	});

	public static BatchEditMethod<Void> TITLE_TRIM = new BatchEditMethod<>((ep, param) ->
	{
		ep.title = ep.title.trim();
	});

	public static BatchEditMethod<Void> PATH_TRIM = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = ep.part.trim();
	});

	public static BatchEditMethod<String> TITLE_APPEND = new BatchEditMethod<>((ep, param) ->
	{
		ep.title = param + ep.title;
	});

	public static BatchEditMethod<String> PATH_APPEND = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = param + ep.part;
	});

	public static BatchEditMethod<String> TITLE_PREPEND = new BatchEditMethod<>((ep, param) ->
	{
		ep.title = ep.title + param;
	});

	public static BatchEditMethod<String> PATH_PREPEND = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = ep.part + param;
	});

	public static BatchEditMethod<Tuple<Integer, Integer>> TITLE_SUBSTRING_DELETE = new BatchEditMethod<>((ep, param) ->
	{
		int start = param.Item1;
		int end   = param.Item2;
		String val = ep.title;
		val = val.substring(0, start).concat(val.substring(end));
		ep.title = val;
	});

	public static BatchEditMethod<Tuple<Integer, Integer>> PATH_SUBSTRING_DELETE = new BatchEditMethod<>((ep, param) ->
	{
		int start = param.Item1;
		int end   = param.Item2;
		String val = ep.part;
		val = val.substring(0, start).concat(val.substring(end));
		ep.part = val;
	});

	public static BatchEditMethod<String> TITLE_SEARCH_AND_DELETE = new BatchEditMethod<>((ep, param) ->
	{
		ep.title = ep.title.replace(param, Str.Empty);
	});

	public static BatchEditMethod<String> PATH_SEARCH_AND_DELETE = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = ep.part.replace(param, Str.Empty);
	});

	public static BatchEditMethod<Integer> EPISODEINDEX_ADD = new BatchEditMethod<>((ep, param) ->
	{
		ep.episodeNumber += param;
	});

	public static BatchEditMethod<Void> FILESIZE_FROM_FILE = new BatchEditMethod<>((ep, param) ->
	{
		File f = new File(PathFormatter.fromCCPath(ep.part));
		if (f.exists()) ep.filesize = new CCFileSize(f.length());
	});

	public static BatchEditMethod<Void> VIEWED_CLEAR = new BatchEditMethod<>((ep, param) ->
	{
		ep.viewedHistory = CCDateTimeList.createEmpty();
	});

	public static BatchEditMethod<Integer> LENGTH_SET = new BatchEditMethod<>((ep, param) ->
	{
		ep.length = param;
	});

	public static BatchEditMethod<CCFileFormat> FORMAT_SET = new BatchEditMethod<>((ep, param) ->
	{
		if (param == null) return;
		ep.format = param;
	});

	public static BatchEditMethod<Void> FORMAT_FROM_FILE = new BatchEditMethod<>((ep, param) ->
	{
		ep.format = CCFileFormat.getMovieFormatOrDefault(PathFormatter.getExtension(PathFormatter.fromCCPath(ep.part)));
	});

	public static BatchEditMethod<CCDBLanguageList> LANGUAGE_SET = new BatchEditMethod<>((ep, param) ->
	{
		if (param == null) return;
		ep.language = param;
	});

	public static BatchEditMethod<Void> LANGUAGE_FROM_FILE_MEDIAINFO = new BatchEditMethod<>((ep, param) ->
	{
		MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.part), false);

		if (dat.AudioLanguages == null) throw new MediaQueryException("No language in file");

		CCDBLanguageList dbll = dat.AudioLanguages;

		if (dbll.isEmpty()) throw new MediaQueryException("Language is empty");
		ep.language = dbll;
	});

	public static BatchEditMethod<Void> LENGTH_FROM_FILE_MEDIAINFO = new BatchEditMethod<>((ep, param) ->
	{
		MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.part), true);

		int dur = dat.Duration==-1 ? -1 :(int)(dat.Duration/60);
		if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
		ep.length = dur;
	});

	public static BatchEditMethod<Void> MEDIAINFO_FROM_FILE = new BatchEditMethod<>((ep, param) ->
	{
		MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.part), true);
		CCMediaInfo minfo = dat.toMediaInfo();

		if (minfo.isSet()) ep.mediaInfo = minfo;
	});

	public static BatchEditMethod<Void> MEDIAINFO_CLEAR = new BatchEditMethod<>((ep, param) ->
	{
		ep.mediaInfo = CCMediaInfo.EMPTY;
	});

	public static BatchEditMethod<Void> MEDIAINFO_CALC_HASH = new BatchEditMethod<>((ep, param) ->
	{
		var p = ep.mediaInfo.toPartial();
		p.Checksum = Opt.of(ChecksumHelper.fastVideoHash(new File(PathFormatter.fromCCPath(ep.part))));
		ep.mediaInfo = p.toMediaInfo();
	});

	public static BatchEditMethod<CCDateTime> VIEWED_ADD = new BatchEditMethod<>((ep, param) ->
	{
		ep.viewedHistory = ep.viewedHistory.add(param);
	});

	public static BatchEditMethod<Void> PATH_TO_CCPATH = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = PathFormatter.getCCPath(ep.part);
	});

	public static BatchEditMethod<Void> PATH_FROM_CCPATH = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = PathFormatter.fromCCPath(ep.part);
	});

	public static BatchEditMethod<Void> PATH_DELETE_EXTENSION = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = PathFormatter.getWithoutExtension(ep.part);
	});

	public static BatchEditMethod<Void> PATH_DELETE_FILENAME_WITH_EXT = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = PathFormatter.getDirectory(ep.part);
	});

	public static BatchEditMethod<Void> PATH_DELETE_FILENAME_WITHOUT_EXT = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = PathFormatter.getDirectory(ep.part).concat(PathFormatter.getExtension(ep.part));
	});

	public static BatchEditMethod<Void> PATH_DELETE_FILEPATH = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = PathFormatter.getFilenameWithExt(ep.part);
	});

	public static BatchEditMethod<Void> TITLE_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.title = ep.getSource().getTitle();
	});

	public static BatchEditMethod<Void> EPISODEINDEX_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.episodeNumber = ep.getSource().getEpisodeNumber();
	});

	public static BatchEditMethod<Void> FORMAT_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.format = ep.getSource().getFormat();
	});

	public static BatchEditMethod<Void> MEDIAINFO_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.mediaInfo = ep.getSource().getMediaInfo();
	});

	public static BatchEditMethod<Void> LANGUAGE_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.language = ep.getSource().getLanguage();
	});

	public static BatchEditMethod<Void> LENGTH_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.length = ep.getSource().getLength();
	});

	public static BatchEditMethod<Void> FILESIZE_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.filesize = ep.getSource().getFilesize();
	});

	public static BatchEditMethod<Void> ADDDATE_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.addDate = ep.getSource().getAddDate();
	});

	public static BatchEditMethod<Void> PATH_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.part = ep.getSource().getPart();
	});

	public static BatchEditMethod<Void> VIEWED_RESET = new BatchEditMethod<>((ep, param) ->
	{
		ep.viewedHistory = ep.getSource().getViewedHistory();
	});
}
