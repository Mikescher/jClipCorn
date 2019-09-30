package de.jClipCorn.gui.frames.addMultiEpisodesFrame;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.util.stream.CCStreams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewEpisodeVM {

	public String SourcePath = Str.Empty;
	public String TargetPath = Str.Empty;

	public boolean IsValid = false;
	public String Problems = Str.Empty;

	public String Title = Str.Empty;
	public CCDBLanguageList Language = CCDBLanguageList.EMPTY;
	public int Length = -1;
	public int EpisodeNumber = -1;
	public long Filesize = -1;
	public CCMediaInfo MediaInfo = CCMediaInfo.EMPTY;
	public boolean NoMove = false;

	public MediaQueryResult MediaQueryResult = null;
	public String TargetRoot = null;

	// -------- -------- -------- -------- -------- -------- -------- --------

	public CCDate getAddDate() { return CCDate.getCurrentDate(); }

	public CCDateTimeList getViewedHistory() { return CCDateTimeList.createEmpty(); }

	public CCFileFormat getFormat() { return CCFileFormat.getMovieFormatOrDefault(PathFormatter.getExtension(SourcePath)); }

	public void updateTarget(CCSeason season, CCDBLanguageList commonLang, String globalSeriesRoot)
	{
		if (NoMove)
		{
			TargetPath = PathFormatter.getCCPath(SourcePath);
			return;
		}

		String root = TargetRoot;
		if (Str.isNullOrWhitespace(root)) root = season.getSeries().guessSeriesRootPath();
		if (Str.isNullOrWhitespace(root)) root = globalSeriesRoot;

		if (Str.isNullOrWhitespace(root)) {
			TargetPath = Str.Empty;
			return;
		}
		File t = season.getFileForCreatedFolderstructure(new File(root), Title, EpisodeNumber, getFormat(), commonLang);
		if (t == null) {
			TargetPath = Str.Empty;
			return;
		}

		TargetPath = PathFormatter.getCCPath(t.getAbsolutePath());
	}

	public void validate(CCSeason s)
	{
		List<UserDataProblem> probs = new ArrayList<>();
		UserDataProblem.testEpisodeData(probs, s, null, Title, Length, EpisodeNumber, getAddDate(), getViewedHistory(), Filesize, getFormat().asString(), getFormat().asStringAlt(), TargetPath, MediaInfo, Language);

		IsValid = probs.isEmpty();
		Problems = "<html>" + CCStreams.iterate(probs).stringjoin(UserDataProblem::getText, "\n<br/>") + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
