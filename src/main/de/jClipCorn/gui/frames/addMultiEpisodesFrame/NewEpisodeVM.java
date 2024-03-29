package de.jClipCorn.gui.frames.addMultiEpisodesFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.IEpisodeData;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.metadata.VideoMetadata;
import de.jClipCorn.features.userdataProblem.UserDataProblem;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.ICCPropertySource;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class NewEpisodeVM implements IEpisodeData, ICCPropertySource {

	public FSPath SourcePath = FSPath.Empty;
	public CCPath TargetPath = CCPath.Empty;

	public boolean IsValid = false;
	public String Problems = Str.Empty;

	public String Title = Str.Empty;
	public CCDBLanguageSet Language = CCDBLanguageSet.EMPTY;
	public CCDBLanguageList Subtitles = CCDBLanguageList.EMPTY;
	public int Length = -1;
	public int EpisodeNumber = -1;
	public CCFileSize Filesize = CCFileSize.ZERO;
	public CCMediaInfo MediaInfo = CCMediaInfo.EMPTY;
	public CCUserScore Score = CCUserScore.RATING_NO;
	public String ScoreComment = Str.Empty;

	public boolean NoMove = false;

	public VideoMetadata MediaQueryResult = null;
	public FSPath TargetRoot = FSPath.Empty;

	private final CCMovieList movielist;

	// -------- -------- -------- -------- -------- -------- -------- --------


	public NewEpisodeVM(CCMovieList ml) {
		movielist = ml;
	}

	@Override
	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	@Override public CCDate getAddDate() { return CCDate.getCurrentDate(); }

	@Override public CCDateTimeList getViewedHistory() { return CCDateTimeList.createEmpty(); }

	@Override public CCTagList getTags() { return CCTagList.EMPTY; }

	@Override public CCDBLanguageSet getLanguage() { return Language; }

	@Override public CCDBLanguageList getSubtitles() { return Subtitles; }

	@Override
	public CCMediaInfo getMediaInfo() {
		return MediaInfo;
	}

	@Override public int getEpisodeNumber() { return EpisodeNumber; }

	@Override public String getTitle() { return Title; }

	@Override public int getLength() { return Length; }

	@Override public CCFileFormat getFormat() { return CCFileFormat.getMovieFormatOrDefault(SourcePath.getExtension()); }

	@Override public CCFileSize getFilesize() { return Filesize; }

	@Override public CCPath getPart() { return TargetPath; }

	@Override
	public CCUserScore getScore() {
		return Score;
	}

	@Override
	public String getScoreComment() {
		return ScoreComment;
	}

	public void updateTarget(CCSeason season, CCDBLanguageSet commonLang, FSPath globalSeriesRoot)
	{
		if (NoMove)
		{
			TargetPath = CCPath.createFromFSPath(SourcePath, this);
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

		TargetPath = CCPath.createFromFSPath(t, this);
	}

	public void validate(CCSeason s)
	{
		List<UserDataProblem> probs = new ArrayList<>();

		UserDataProblem.testEpisodeData(probs, s.getMovieList(), s, null, this);

		IsValid = probs.isEmpty();
		Problems = "<html>" + CCStreams.iterate(probs).stringjoin(UserDataProblem::getText, "\n<br/>") + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
