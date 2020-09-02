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

@SuppressWarnings("Convert2MethodRef")
public class BatchEditMethods
{
	public static BatchEditMethod<Integer> TITLE_DELETE_FIRST_CHARS = new BatchEditMethod<>((ep, param) ->
	{
		ep.setTitle(ep.getTitle().substring(param));
	});

	public static BatchEditMethod<Integer> PATH_DELETE_FIRST_CHARS = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(ep.getPart().substring(param));
	});

	public static BatchEditMethod<Integer> TITLE_DELETE_LAST_CHARS = new BatchEditMethod<>((ep, param) ->
	{
		ep.setTitle(ep.getTitle().substring(0, ep.getTitle().length() - param));
	});

	public static BatchEditMethod<Integer> PATH_DELETE_LAST_CHARS = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(ep.getPart().substring(0, ep.getTitle().length() - param));
	});

	public static BatchEditMethod<Tuple<String, String>> TITLE_STRING_REPLACE = new BatchEditMethod<>((ep, param) ->
	{
		ep.setTitle(ep.getTitle().replace(param.Item1, param.Item2));
	});

	public static BatchEditMethod<Tuple<String, String>> PATH_STRING_REPLACE = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(ep.getPart().replace(param.Item1, param.Item2));
	});

	public static BatchEditMethod<Tuple<String, String>> TITLE_REGEX_REPLACE = new BatchEditMethod<>((ep, param) ->
	{
		ep.setTitle(ep.getTitle().replaceAll(param.Item1, param.Item2));
	});

	public static BatchEditMethod<Tuple<String, String>> PATH_REGEX_REPLACE = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(ep.getPart().replaceAll(param.Item1, param.Item2));
	});

	public static BatchEditMethod<Void> TITLE_TRIM = new BatchEditMethod<>((ep, param) ->
	{
		ep.setTitle(ep.getTitle().trim());
	});

	public static BatchEditMethod<Void> PATH_TRIM = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(ep.getPart().trim());
	});

	public static BatchEditMethod<String> TITLE_APPEND = new BatchEditMethod<>((ep, param) ->
	{
		ep.setTitle(param + ep.getTitle());
	});

	public static BatchEditMethod<String> PATH_APPEND = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(param + ep.getPart());
	});

	public static BatchEditMethod<String> TITLE_PREPEND = new BatchEditMethod<>((ep, param) ->
	{
		ep.setTitle(ep.getTitle() + param);
	});

	public static BatchEditMethod<String> PATH_PREPEND = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(ep.getPart() + param);
	});

	public static BatchEditMethod<Tuple<Integer, Integer>> TITLE_SUBSTRING_DELETE = new BatchEditMethod<>((ep, param) ->
	{
		int start = param.Item1;
		int end   = param.Item2;
		String val = ep.getTitle();
		val = val.substring(0, start).concat(val.substring(end));
		ep.setTitle(val);
	});

	public static BatchEditMethod<Tuple<Integer, Integer>> PATH_SUBSTRING_DELETE = new BatchEditMethod<>((ep, param) ->
	{
		int start = param.Item1;
		int end   = param.Item2;
		String val = ep.getPart();
		val = val.substring(0, start).concat(val.substring(end));
		ep.setPart(val);
	});

	public static BatchEditMethod<String> TITLE_SEARCH_AND_DELETE = new BatchEditMethod<>((ep, param) ->
	{
		ep.setTitle(ep.getTitle().replace(param, Str.Empty));
	});

	public static BatchEditMethod<String> PATH_SEARCH_AND_DELETE = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(ep.getPart().replace(param, Str.Empty));
	});

	public static BatchEditMethod<Integer> EPISODEINDEX_ADD = new BatchEditMethod<>((ep, param) ->
	{
		ep.setEpisodeNumber(ep.getEpisodeNumber() + param);
	});

	public static BatchEditMethod<Void> FILESIZE_FROM_FILE = new BatchEditMethod<>((ep, param) ->
	{
		File f = new File(PathFormatter.fromCCPath(ep.getPart()));
		if (f.exists()) ep.setFilesize(new CCFileSize(f.length()));
	});

	public static BatchEditMethod<Void> VIEWED_CLEAR = new BatchEditMethod<>((ep, param) ->
	{
		ep.setViewedHistory(CCDateTimeList.createEmpty());
	});

	public static BatchEditMethod<Integer> LENGTH_SET = new BatchEditMethod<>((ep, param) ->
	{
		ep.setLength(param);
	});

	public static BatchEditMethod<CCFileFormat> FORMAT_SET = new BatchEditMethod<>((ep, param) ->
	{
		if (param == null) return;
		ep.setFormat(param);
	});

	public static BatchEditMethod<Void> FORMAT_FROM_FILE = new BatchEditMethod<>((ep, param) ->
	{
		ep.setFormat(CCFileFormat.getMovieFormatOrDefault(PathFormatter.getExtension(PathFormatter.fromCCPath(ep.getPart()))));
	});

	public static BatchEditMethod<CCDBLanguageList> LANGUAGE_SET = new BatchEditMethod<>((ep, param) ->
	{
		if (param == null) return;
		ep.setLanguage(param);
	});

	public static BatchEditMethod<Void> LANGUAGE_FROM_FILE_MEDIAINFO = new BatchEditMethod<>((ep, param) ->
	{
		MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.getPart()), false);

		if (dat.AudioLanguages == null) throw new MediaQueryException("No language in file");

		CCDBLanguageList dbll = dat.AudioLanguages;

		if (dbll.isEmpty()) throw new MediaQueryException("Language is empty");
		ep.setLanguage(dbll);
	});

	public static BatchEditMethod<Void> LENGTH_FROM_FILE_MEDIAINFO = new BatchEditMethod<>((ep, param) ->
	{
		MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.getPart()), true);

		int dur = (dat.Duration==-1)?(-1):(int)(dat.Duration/60);
		if (dur == -1) throw new MediaQueryException("Duration == -1"); //$NON-NLS-1$
		ep.setLength(dur);
	});

	public static BatchEditMethod<Void> MEDIAINFO_FROM_FILE = new BatchEditMethod<>((ep, param) ->
	{
		MediaQueryResult dat = MediaQueryRunner.query(PathFormatter.fromCCPath(ep.getPart()), true);
		CCMediaInfo minfo = dat.toMediaInfo();

		if (minfo.isSet()) ep.setMediaInfo(minfo);
	});

	public static BatchEditMethod<Void> MEDIAINFO_CLEAR = new BatchEditMethod<>((ep, param) ->
	{
		ep.setMediaInfo(CCMediaInfo.EMPTY);
	});

	public static BatchEditMethod<Void> MEDIAINFO_CALC_HASH = new BatchEditMethod<>((ep, param) ->
	{
		var p = ep.getMediaInfo().toPartial();
		p.Checksum = Opt.of(ChecksumHelper.fastVideoHash(new File(PathFormatter.fromCCPath(ep.getPart()))));
		ep.setMediaInfo(p.toMediaInfo());
	});

	public static BatchEditMethod<CCDateTime> VIEWED_ADD = new BatchEditMethod<>((ep, param) ->
	{
		ep.setViewedHistory(ep.getViewedHistory().add(param));
	});

	public static BatchEditMethod<Void> PATH_TO_CCPATH = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(PathFormatter.getCCPath(ep.getPart()));
	});

	public static BatchEditMethod<Void> PATH_FROM_CCPATH = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(PathFormatter.fromCCPath(ep.getPart()));
	});

	public static BatchEditMethod<Void> PATH_DELETE_EXTENSION = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(PathFormatter.getWithoutExtension(ep.getPart()));
	});

	public static BatchEditMethod<Void> PATH_DELETE_FILENAME_WITH_EXT = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(PathFormatter.getDirectory(ep.getPart()));
	});

	public static BatchEditMethod<Void> PATH_DELETE_FILENAME_WITHOUT_EXT = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(PathFormatter.getDirectory(ep.getPart()).concat(PathFormatter.getExtension(ep.getPart())));
	});

	public static BatchEditMethod<Void> PATH_DELETE_FILEPATH = new BatchEditMethod<>((ep, param) ->
	{
		ep.setPart(PathFormatter.getFilenameWithExt(ep.getPart()));
	});
}
