package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class SeasonMatch extends ComparisonMatch {
	public final CompareState State;

	public final CCSeason SeasonLocal;
	public final CCSeason SeasonExtern;

	public final List<EpisodeMatch> Episodes = new ArrayList<>();

	public final boolean NeedsUpdateMetadata;
	public final boolean NeedsUpdateCover;
	public final boolean NeedsCreateNew;
	public final boolean NeedsDelete;

	public SeasonMatch(CompareState state, CCSeason loc, CCSeason ext, boolean updMeta, boolean updCover, boolean copy, boolean del) {
		State = state;
		SeasonLocal = loc;
		SeasonExtern = ext;
		NeedsUpdateMetadata = updMeta;
		NeedsUpdateCover = updCover;
		NeedsCreateNew = copy;
		NeedsDelete = del;
	}

	public EpisodeMatch addEpisodeLocalOnly(CCEpisode loc) {
		var match = new EpisodeMatch(State, loc, null, false, false, true, false);
		Episodes.add(match);
		State.AllEpisodes.add(match);
		State.ProgressCallback.stepSub(1, loc.getLocalID() + "");
		return match;
	}

	public EpisodeMatch addEpisodeExternOnly(CCEpisode ext) {
		var match = new EpisodeMatch(State, null, ext, false, false, false, true);
		Episodes.add(match);
		State.AllEpisodes.add(match);
		State.ProgressCallback.stepSub(1, ext.getLocalID() + "");
		return match;
	}

	public EpisodeMatch addEpisodeMatch(CCEpisode loc, CCEpisode ext) {
		var updateFile = (loc.MediaInfo.get().isSet() && ext.MediaInfo.get().isSet() && !Str.equals(loc.MediaInfo.get().getChecksum(), ext.MediaInfo.get().getChecksum()));

		var propLoc = CCStreams.iterate(loc.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var propExt = CCStreams.iterate(ext.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var updateMeta = !CCStreams.equalsElementwise(propLoc, propExt, (a, b) -> Str.equals(a.serializeValueToString(), b.serializeValueToString()));

		var match = new EpisodeMatch(State, loc, ext, updateMeta, updateFile, false, false);
		Episodes.add(match);
		State.AllEpisodes.add(match);
		State.ProgressCallback.stepSub(2, loc.getLocalID() + "|" + ext.getLocalID());
		return match;
	}

	@Override
	public String getStrAction() {
		var r = new ArrayList<String>();
		if (NeedsCreateNew) r.add("FullCopy");
		if (NeedsDelete) r.add("Delete");
		if (NeedsUpdateMetadata) r.add("UpdateData");
		if (NeedsUpdateCover) r.add("CopyCover");
		return "[" + CCStreams.iterate(r).stringjoin(e -> e, ", ") + "]";
	}

	@Override
	public String getTypeStr() {
		return "Season";
	}

	@Override
	public boolean getNeedsDelete() {
		return NeedsDelete;
	}

	@Override
	public boolean getNeedsUpdateCover() {
		return NeedsUpdateCover;
	}

	@Override
	public boolean getNeedsUpdateFile() {
		return false;
	}

	@Override
	public boolean getNeedsUpdateMetadata() {
		return NeedsUpdateMetadata;
	}

	@Override
	public boolean getNeedsCreateNew() {
		return NeedsCreateNew;
	}

	@Override
	public ICCDatabaseStructureElement getLocal() {
		return SeasonLocal;
	}

	@Override
	public ICCDatabaseStructureElement getExtern() {
		return SeasonExtern;
	}
}
