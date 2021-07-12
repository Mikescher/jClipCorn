package de.jClipCorn.gui.frames.addMultiEpisodesFrame;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.IEpisodeData;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryResult;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class NewEpisodeVM implements IEpisodeData {

	public FSPath SourcePath = FSPath.Empty;
	public CCPath TargetPath = CCPath.Empty;

	public boolean IsValid = false;
	public String Problems = Str.Empty;

	public String Title = Str.Empty;
	public CCDBLanguageList Language = CCDBLanguageList.EMPTY;
	public int Length = -1;
	public int EpisodeNumber = -1;
	public CCFileSize Filesize = CCFileSize.ZERO;
	public CCMediaInfo MediaInfo = CCMediaInfo.EMPTY;
	public boolean NoMove = false;

	public MediaQueryResult MediaQueryResult = null;
	public FSPath TargetRoot = FSPath.Empty;

	// -------- -------- -------- -------- -------- -------- -------- --------

	@Override public CCDate getAddDate() { return CCDate.getCurrentDate(); }

	@Override public CCDateTimeList getViewedHistory() { return CCDateTimeList.createEmpty(); }

	@Override public CCTagList getTags() { return CCTagList.EMPTY; }

	@Override public CCDBLanguageList getLanguage() { return Language; }

	@Override public CCMediaInfo getMediaInfo() { return MediaInfo; }

	@Override
	public PartialMediaInfo getPartialMediaInfo() {
		return MediaInfo.toPartial();
	}

	@Override public int getEpisodeNumber() { return EpisodeNumber; }

	@Override public String getTitle() { return Title; }

	@Override public int getLength() { return Length; }

	@Override public CCFileFormat getFormat() { return CCFileFormat.getMovieFormatOrDefault(SourcePath.getExtension()); }

	@Override public CCFileSize getFilesize() { return Filesize; }

	@Override public CCPath getPart() { return TargetPath; }

	public void updateTarget(CCSeason season, CCDBLanguageList commonLang, FSPath globalSeriesRoot)
	{
		if (NoMove)
		{
			TargetPath = CCPath.createFromFSPath(SourcePath);
			return;
		}

		var root = TargetRoot;
		if (root.isEmpty()) root = season.getSeries().guessSeriesRootPath();
		if (root.isEmpty()) root = globalSeriesRoot;

		if (root.isEmpty()) {
			TargetPath = CCPath.Empty;
			return;
		}
		var t = season.getPathForCreatedFolderstructure(root, Title, EpisodeNumber, getFormat(), commonLang);
		if (t == null) {
			TargetPath = CCPath.Empty;
			return;
		}

		TargetPath = CCPath.createFromFSPath(t);
	}

	public void validate(CCSeason s)
	{
		List<UserDataProblem> probs = new ArrayList<>();

		UserDataProblem.testEpisodeData(probs, s.getMovieList(), s, null, this);

		IsValid = probs.isEmpty();
		Problems = "<html>" + CCStreams.iterate(probs).stringjoin(UserDataProblem::getText, "\n<br/>") + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
