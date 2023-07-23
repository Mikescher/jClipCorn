package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class CompareState {

	public final List<MovieMatch>  Movies = new ArrayList<>();
	public final List<SeriesMatch> Series = new ArrayList<>();

	public final List<SeasonMatch>  AllSeasons  = new ArrayList<>();
	public final List<EpisodeMatch> AllEpisodes = new ArrayList<>();

	public final DoubleProgressCallbackListener ProgressCallback;
	public final CompareDatabaseRuleset Ruleset;

	private final CCMovieList movielistLocal;
	private final CCMovieList movielistExtern;

	public CompareState(CCMovieList mlLoc, CCMovieList mlExt, DoubleProgressCallbackListener cb, CompareDatabaseRuleset ruleset) {
		ProgressCallback = cb;
		Ruleset          = ruleset;
		movielistLocal   = mlLoc;
		movielistExtern  = mlExt;
	}

	public CCProperties ccpropsLocal() {
		return movielistLocal.ccprops();
	}

	public CCProperties ccpropsExtern() {
		return movielistExtern.ccprops();
	}

	public MovieMatch addMovieMatch(CCMovie loc, CCMovie ext) {
		var updateFile = Ruleset.ShouldUpdateFiles(loc.getLocalID(), ext.getLocalID()) &&
				         !loc.MediaInfo.get().Checksum.isEqual(ext.MediaInfo.get().Checksum, Str::equals);

		var updateCover = Ruleset.ShouldUpdateCover(loc.getLocalID(), ext.getLocalID()) && !Str.equals(loc.getCoverInfo().Checksum, ext.getCoverInfo().Checksum);

		var propLoc = CCStreams.iterate(loc.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var propExt = CCStreams.iterate(ext.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var diffMeta = CCStreams.zip(propLoc, propExt)
				                .filter(p -> !Str.equals(p.Item1.serializeToString(), p.Item2.serializeToString()))
				                .filter(p -> Ruleset.ShouldUpdateMetadata(loc.getLocalID(), ext.getLocalID(), p.Item1, p.Item2))
				                .toList();

		var match = new MovieMatch(this, loc, ext, !diffMeta.isEmpty(), updateCover, updateFile, false, false, diffMeta);
		Movies.add(match);
		ProgressCallback.stepSub(2, loc.getLocalID()+"|"+ext.getLocalID());
		return match;
	}

	public MovieMatch addMovieLocalOnly(CCMovie loc) {
		var docopy = Ruleset.ShouldAddLocal(loc.getLocalID());
		var match = new MovieMatch(this, loc, null, false, false, false, docopy, false, new ArrayList<>());
		Movies.add(match);
		ProgressCallback.stepSub(1, loc.getLocalID()+"");
		return match;
	}

	public MovieMatch addMovieExternOnly(CCMovie ext) {
		var dodel = Ruleset.ShouldDeleteExtern(ext.getLocalID());
		var match = new MovieMatch(this, null, ext, false, false, false, false, dodel, new ArrayList<>());
		Movies.add(match);
		ProgressCallback.stepSub(1, ext.getLocalID()+"");
		return match;
	}

	public SeriesMatch addSeriesMatch(CCSeries loc, CCSeries ext) {
		var updateCover = Ruleset.ShouldUpdateCover(loc.getLocalID(), ext.getLocalID()) && !Str.equals(loc.getCoverInfo().Checksum, ext.getCoverInfo().Checksum);

		var propLoc = CCStreams.iterate(loc.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var propExt = CCStreams.iterate(ext.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var diffMeta = CCStreams.zip(propLoc, propExt)
								.filter(p -> !Str.equals(p.Item1.serializeToString(), p.Item2.serializeToString()))
								.filter(p -> Ruleset.ShouldUpdateMetadata(loc.getLocalID(), ext.getLocalID(), p.Item1, p.Item2))
								.toList();

		var match = new SeriesMatch(this, loc, ext, !diffMeta.isEmpty(), updateCover, false, false, diffMeta);
		Series.add(match);
		ProgressCallback.stepSub(2, loc.getLocalID()+"|"+ext.getLocalID());
		return match;
	}

	public SeriesMatch addSeriesLocalOnly(CCSeries loc) {
		var docopy = Ruleset.ShouldAddLocal(loc.getLocalID());
		var match = new SeriesMatch(this, loc, null, false, false, docopy, false, new ArrayList<>());
		Series.add(match);
		ProgressCallback.stepSub(1, loc.getLocalID()+"");
		return match;
	}

	public SeriesMatch addSeriesExternOnly(CCSeries ext) {
		var dodel = Ruleset.ShouldDeleteExtern(ext.getLocalID());
		var match = new SeriesMatch(this, null, ext, false, false, false, dodel, new ArrayList<>());
		Series.add(match);
		ProgressCallback.stepSub(1, ext.getLocalID()+"");
		return match;
	}

	public CCFileSize estimatePatchSize() {
		long fs = 0;

		for (var m: Movies) if (m.NeedsCreateNew || m.NeedsUpdateFile) fs += m.MovieLocal.FileSize.get().getBytes();
		for (var m: Movies) if (m.NeedsCreateNew || m.NeedsUpdateCover) fs += m.MovieLocal.getCoverInfo().Filesize.getBytes();

		for (var m: Series) if (m.NeedsCreateNew || m.NeedsUpdateCover) fs += m.SeriesLocal.getCoverInfo().Filesize.getBytes();

		for (var m: AllSeasons) if (m.NeedsCreateNew || m.NeedsUpdateCover) fs += m.SeasonLocal.getCoverInfo().Filesize.getBytes();

		for (var m: AllEpisodes) if (m.NeedsCreateNew || m.NeedsUpdateFile) fs += m.EpisodeLocal.FileSize.get().getBytes();

		return new CCFileSize(fs);
	}
}
