package de.jClipCorn.gui.frames.batchEditFrame;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.features.metadata.impl.MediaInfoRunner;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ChecksumHelper;

import java.io.File;

@SuppressWarnings("CodeBlock2Expr")
public class BatchEditMethods
{
	public static BatchEditMethod<Integer> TITLE_DELETE_FIRST_CHARS = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.title = ep.title.substring(param);
	});

	public static BatchEditMethod<Integer> PATH_DELETE_FIRST_CHARS = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(ep.part.toString().substring(param));
	});

	public static BatchEditMethod<Integer> TITLE_DELETE_LAST_CHARS = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.title = ep.title.substring(0, ep.title.length() - param);
	});

	public static BatchEditMethod<Integer> PATH_DELETE_LAST_CHARS = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(ep.part.toString().substring(0, ep.title.length() - param));
	});

	public static BatchEditMethod<Tuple<String, String>> TITLE_STRING_REPLACE = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.title = ep.title.replace(param.Item1, param.Item2);
	});

	public static BatchEditMethod<Tuple<String, String>> PATH_STRING_REPLACE = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(ep.part.toString().replace(param.Item1, param.Item2));
	});

	public static BatchEditMethod<Tuple<String, String>> TITLE_REGEX_REPLACE = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.title = ep.title.replaceAll(param.Item1, param.Item2);
	});

	public static BatchEditMethod<Tuple<String, String>> PATH_REGEX_REPLACE = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(ep.part.toString().replaceAll(param.Item1, param.Item2));
	});

	public static BatchEditMethod<Void> TITLE_TRIM = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.title = ep.title.trim();
	});

	public static BatchEditMethod<Void> PATH_TRIM = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(ep.part.toString().trim());
	});

	public static BatchEditMethod<String> TITLE_APPEND = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.title = param + ep.title;
	});

	public static BatchEditMethod<String> PATH_APPEND = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(param + ep.part.toString());
	});

	public static BatchEditMethod<String> TITLE_PREPEND = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.title = ep.title + param;
	});

	public static BatchEditMethod<String> PATH_PREPEND = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(ep.part.toString() + param);
	});

	public static BatchEditMethod<Tuple<Integer, Integer>> TITLE_SUBSTRING_DELETE = new BatchEditMethod<>((ep, param, opt) ->
	{
		int start = param.Item1;
		int end   = param.Item2;
		String val = ep.title;
		val = val.substring(0, start).concat(val.substring(end));
		ep.title = val;
	});

	public static BatchEditMethod<Tuple<Integer, Integer>> PATH_SUBSTRING_DELETE = new BatchEditMethod<>((ep, param, opt) ->
	{
		int start = param.Item1;
		int end   = param.Item2;
		String val = ep.part.toString();
		val = val.substring(0, start).concat(val.substring(end));
		ep.part = CCPath.create(val);
	});

	public static BatchEditMethod<String> TITLE_SEARCH_AND_DELETE = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.title = ep.title.replace(param, Str.Empty);
	});

	public static BatchEditMethod<String> PATH_SEARCH_AND_DELETE = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(ep.part.toString().replace(param, Str.Empty));
	});

	public static BatchEditMethod<Integer> EPISODEINDEX_ADD = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.episodeNumber += param;
	});

	public static BatchEditMethod<Void> FILESIZE_FROM_FILE = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (ep.part.toFSPath(ep.ccprops()).toFile().exists()) ep.filesize = ep.part.toFSPath(ep.ccprops()).filesize();
	});

	public static BatchEditMethod<Void> VIEWED_CLEAR = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.viewedHistory = CCDateTimeList.createEmpty();
	});

	public static BatchEditMethod<Integer> LENGTH_SET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.length = param;
	});

	public static BatchEditMethod<CCFileFormat> FORMAT_SET = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.format = param;
	});

	public static BatchEditMethod<Void> FORMAT_FROM_FILE = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.format = CCFileFormat.getMovieFormatOrDefault(ep.part.getExtension());
	});

	public static BatchEditMethod<CCDBLanguageSet> LANGUAGE_SET = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.language = param;
	});

	public static BatchEditMethod<CCDBLanguageList> SUBTITLE_SET = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.subtitles = param;
	});

	public static BatchEditMethod<CCTagList> TAGS_SET = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.tags = param;
	});

	public static BatchEditMethod<CCDate> ADDDATE_SET = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.addDate = param;
	});

	public static BatchEditMethod<Void> LANGUAGE_FROM_FILE_MEDIAINFO = new BatchEditMethod<>((ep, param, opt) ->
	{
		var dat = new MediaInfoRunner(ep.getSource().getMovieList()).run(ep.part.toFSPath(ep.ccprops()));

		if (!dat.allAudioLanguagesValid()) throw new MediaQueryException("Errorneous language in file"); //$NON-NLS-1$

		CCDBLanguageSet dbll = dat.getValidAudioLanguages();

		if (dbll.isEmpty()) throw new MediaQueryException("Language is empty"); //$NON-NLS-1$
		ep.language = dbll;
	});

	public static BatchEditMethod<Void> SUBTITLES_FROM_FILE_MEDIAINFO = new BatchEditMethod<>((ep, param, opt) ->
	{
		var dat = new MediaInfoRunner(ep.getSource().getMovieList()).run(ep.part.toFSPath(ep.ccprops()));

		if (!dat.allSubtitleLanguagesValid()) throw new MediaQueryException("Errorneous language in file"); //$NON-NLS-1$

		ep.subtitles = dat.getValidSubtitleLanguages();
	});

	public static BatchEditMethod<Void> LENGTH_FROM_FILE_MEDIAINFO = new BatchEditMethod<>((ep, param, opt) ->
	{
		var dat = new MediaInfoRunner(ep.getSource().getMovieList()).run(ep.part.toFSPath(ep.ccprops()));

		if (dat.Duration.isError()) throw new MediaQueryException(dat.Duration.getErr().TextShort, dat.Duration.getErr());
		if (dat.Duration.isEmpty()) throw new MediaQueryException("Duration is Empty");

		ep.length = (int)(dat.Duration.get()/60);
	});

	public static BatchEditMethod<Void> MEDIAINFO_FROM_FILE = new BatchEditMethod<>((ep, param, opt) ->
	{
		var dat = new MediaInfoRunner(ep.getSource().getMovieList()).run(ep.part.toFSPath(ep.ccprops()));
		ep.mediaInfo = dat.toMediaInfo();
	});

	public static BatchEditMethod<Void> MEDIAINFO_CLEAR = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.mediaInfo = CCMediaInfo.EMPTY;
	});

	public static BatchEditMethod<Void> MEDIAINFO_CALC_HASH = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.mediaInfo = ep.mediaInfo.WithChecksum(Opt.of(ChecksumHelper.fastVideoHash(ep.part.toFSPath(ep.ccprops()))));
	});

	public static BatchEditMethod<CCDateTime> VIEWED_ADD = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.viewedHistory = ep.viewedHistory.add(param);
	});

	public static BatchEditMethod<Void> PATH_TO_CCPATH = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.createFromFSPath(FSPath.create(ep.part.toString()), ep.ccprops());
	});

	public static BatchEditMethod<Void> PATH_FROM_CCPATH = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(ep.part.toFSPath(ep.ccprops()).toString());
	});

	public static BatchEditMethod<Void> PATH_DELETE_EXTENSION = new BatchEditMethod<>((ep, param, opt) ->
	{
		var fs = ep.part.toFSPath(ep.ccprops());
		ep.part = ep.part.getParent().append(ep.part.getFilenameWithoutExt());
	});

	public static BatchEditMethod<Void> PATH_DELETE_FILENAME_WITH_EXT = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = ep.part.getParent();
	});

	public static BatchEditMethod<Void> PATH_DELETE_FILENAME_WITHOUT_EXT = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = ep.part.getParent().append(ep.part.getExtension());
	});

	public static BatchEditMethod<Void> PATH_DELETE_FILEPATH = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = CCPath.create(ep.part.getFilenameWithExt());
	});

	public static BatchEditMethod<Void> TITLE_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.title = ep.getSource().Title.get();
	});

	public static BatchEditMethod<Void> EPISODEINDEX_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.episodeNumber = ep.getSource().EpisodeNumber.get();
	});

	public static BatchEditMethod<Void> FORMAT_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.format = ep.getSource().Format.get();
	});

	public static BatchEditMethod<Void> MEDIAINFO_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.mediaInfo = ep.getSource().mediaInfo().get();
	});

	public static BatchEditMethod<Void> LANGUAGE_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.language = ep.getSource().Language.get();
	});

	public static BatchEditMethod<Void> SUBTITLES_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.subtitles = ep.getSource().Subtitles.get();
	});

	public static BatchEditMethod<Void> LENGTH_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.length = ep.getSource().Length.get();
	});

	public static BatchEditMethod<Void> FILESIZE_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.filesize = ep.getSource().FileSize.get();
	});

	public static BatchEditMethod<Void> ADDDATE_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.addDate = ep.getSource().AddDate.get();
	});

	public static BatchEditMethod<Void> PATH_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.part = ep.getSource().Part.get();
	});

	public static BatchEditMethod<Void> VIEWED_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.viewedHistory = ep.getSource().ViewedHistory.get();
	});

	public static BatchEditMethod<Void> TAGS_RESET = new BatchEditMethod<>((ep, param, opt) ->
	{
		ep.tags = ep.getSource().Tags.get();
	});

	public static BatchEditMethod<Tuple3<File[], Integer, Boolean>> PATH_FROM_DIALOG = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param.Item1 == null) return;
		if (param.Item3)
		{
			if (opt.Item2.size() != (param.Item1.length+param.Item2)) throw new Exception("Not enough / too much files ("+(opt.Item2.size() - param.Item2)+" <> "+param.Item1.length+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if ((opt.Item1-param.Item2) < 0) return;
		}
		else
		{
			if ((opt.Item1-param.Item2) < 0) return;
			if ((opt.Item1-param.Item2) >= param.Item1.length) return;
		}

		var file = param.Item1[opt.Item1-param.Item2];

		ep.part = CCPath.createFromFSPath(FSPath.create(file), ep.ccprops());
	});

	public static BatchEditMethod<CCSingleTag> TAG_ADD = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.tags = ep.tags.getSetTag(param, true);
	});

	public static BatchEditMethod<CCSingleTag> TAG_REM = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.tags = ep.tags.getSetTag(param, false);
	});

	public static BatchEditMethod<CCDBLanguage> LANGUAGE_ADD = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.language = ep.language.getAdd(param);
	});

	public static BatchEditMethod<CCDBLanguage> LANGUAGE_REM = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.language = ep.language.getRemove(param);
	});

	public static BatchEditMethod<CCDBLanguage> SUBTITLE_ADD = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.subtitles = ep.subtitles.getAdd(param);
	});

	public static BatchEditMethod<CCDBLanguage> SUBTITLE_REM = new BatchEditMethod<>((ep, param, opt) ->
	{
		if (param == null) return;
		ep.subtitles = ep.subtitles.getRemoveLast(param);
	});
}
