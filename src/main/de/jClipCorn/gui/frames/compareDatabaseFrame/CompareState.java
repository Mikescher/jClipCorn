package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
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

	public CompareState(DoubleProgressCallbackListener cb) {
		ProgressCallback = cb;
	}

	public MovieMatch addMovieMatch(CCMovie loc, CCMovie ext) {
		var updateFile = (loc.MediaInfo.get().isSet() && ext.MediaInfo.get().isSet() && !Str.equals(loc.MediaInfo.get().getChecksum(), ext.MediaInfo.get().getChecksum()));

		var updateCover = Str.equals(loc.getCoverInfo().Checksum, ext.getCoverInfo().Checksum);

		var propLoc = CCStreams.iterate(loc.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var propExt = CCStreams.iterate(ext.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var updateMeta = !CCStreams.equalsElementwise(propLoc, propExt, (a, b) -> Str.equals(a.serializeValueToString(), b.serializeValueToString()));

		var match = new MovieMatch(this, loc, ext, updateMeta, updateCover, updateFile, false, false);
		Movies.add(match);
		ProgressCallback.stepSub(2, loc.getLocalID()+"|"+ext.getLocalID());
		return match;
	}

	public MovieMatch addMovieLocalOnly(CCMovie loc) {
		var match = new MovieMatch(this, loc, null, false, false, false, true, false);
		Movies.add(match);
		ProgressCallback.stepSub(1, loc.getLocalID()+"");
		return match;
	}

	public MovieMatch addMovieExternOnly(CCMovie ext) {
		var match = new MovieMatch(this, null, ext, false, false, false, false, true);
		Movies.add(match);
		ProgressCallback.stepSub(1, ext.getLocalID()+"");
		return match;
	}

	public SeriesMatch addSeriesMatch(CCSeries loc, CCSeries ext) {
		var updateCover = Str.equals(loc.getCoverInfo().Checksum, ext.getCoverInfo().Checksum);

		var propLoc = CCStreams.iterate(loc.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var propExt = CCStreams.iterate(ext.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var updateMeta = !CCStreams.equalsElementwise(propLoc, propExt, (a, b) -> Str.equals(a.serializeValueToString(), b.serializeValueToString()));

		var match = new SeriesMatch(this, loc, ext, updateMeta, updateCover, false, false);
		Series.add(match);
		ProgressCallback.stepSub(2, loc.getLocalID()+"|"+ext.getLocalID());
		return match;
	}

	public SeriesMatch addSeriesLocalOnly(CCSeries loc) {
		var match = new SeriesMatch(this, loc, null, false, false, true, false);
		Series.add(match);
		ProgressCallback.stepSub(1, loc.getLocalID()+"");
		return match;
	}

	public SeriesMatch addSeriesExternOnly(CCSeries ext) {
		var match = new SeriesMatch(this, null, ext, false, false, false, true);
		Series.add(match);
		ProgressCallback.stepSub(1, ext.getLocalID()+"");
		return match;
	}

}
