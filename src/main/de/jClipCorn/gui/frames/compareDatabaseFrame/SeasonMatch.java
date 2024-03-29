package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class SeasonMatch extends ComparisonMatch {
	public final CompareState State;
	public final SeriesMatch Parent;

	public final CCSeason SeasonLocal;
	public final CCSeason SeasonExtern;

	public final List<EpisodeMatch> Episodes = new ArrayList<>();

	public final boolean NeedsUpdateMetadata;
	public final boolean NeedsUpdateCover;
	public final boolean NeedsCreateNew;
	public final boolean NeedsDelete;
	public final boolean NeedsDeleteByParent;

	public final List<Tuple<IEProperty, IEProperty>> MetadataDiff;

	public SeasonMatch(SeriesMatch parent, CCSeason loc, CCSeason ext, boolean updMeta, boolean updCover, boolean copy, boolean del, boolean delByParent, List<Tuple<IEProperty, IEProperty>> diff) {
		State               = parent.State;
		Parent              = parent;
		SeasonLocal         = loc;
		SeasonExtern        = ext;
		NeedsUpdateMetadata = updMeta;
		NeedsUpdateCover    = updCover;
		NeedsCreateNew      = copy;
		NeedsDelete         = del;
		MetadataDiff        = diff;
		NeedsDeleteByParent = delByParent;
	}

	public EpisodeMatch addEpisodeLocalOnly(CCEpisode loc) {
		var docopy = State.Ruleset.ShouldAddLocal(loc.getLocalID());
		var match = new EpisodeMatch(this, loc, null, false, false, docopy, false, false, new ArrayList<>());
		Episodes.add(match);
		State.AllEpisodes.add(match);
		State.ProgressCallback.stepSub(1, loc.getLocalID() + "");
		return match;
	}

	public EpisodeMatch addEpisodeExternOnly(CCEpisode ext) {
		var dodel = State.Ruleset.ShouldDeleteExtern(ext.getLocalID());
		var match = new EpisodeMatch(this, null, ext, false, false, false, dodel, false, new ArrayList<>());
		Episodes.add(match);
		State.AllEpisodes.add(match);
		State.ProgressCallback.stepSub(1, ext.getLocalID() + "");
		return match;
	}

	public EpisodeMatch addEpisodeDeleteByParent(CCEpisode ext) {
		var match = new EpisodeMatch(this, null, ext, false, false, false, false, true, new ArrayList<>());
		Episodes.add(match);
		State.AllEpisodes.add(match);
		State.ProgressCallback.stepSub(1, ext.getLocalID() + "");
		return match;
	}

	public EpisodeMatch addEpisodeMatch(CCEpisode loc, CCEpisode ext) {
		var updateFile = State.Ruleset.ShouldUpdateFiles(loc.getLocalID(), ext.getLocalID()) &&
						 !loc.MediaInfo.get().Checksum.isEqual(ext.MediaInfo.get().Checksum, Str::equals);

		var propLoc = CCStreams.iterate(loc.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var propExt = CCStreams.iterate(ext.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var diffMeta = CCStreams.zip(propLoc, propExt)
				                .filter(p -> !Str.equals(p.Item1.serializeToString(), p.Item2.serializeToString()))
				                .filter(p -> State.Ruleset.ShouldUpdateMetadata(loc.getLocalID(), ext.getLocalID(), p.Item1, p.Item2))
				                .toList();

		var match = new EpisodeMatch(this, loc, ext, !diffMeta.isEmpty(), updateFile, false, false, false, diffMeta);
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
		if (NeedsDeleteByParent) r.add("DeleteByParent");
		return "[" + CCStreams.iterate(r).stringjoin(e -> e, ", ") + "]";
	}

	@Override
	public String getTypeStr() {
		return "Season";
	}

	@Override
	public ICCDatabaseStructureElement getLocal() {
		return SeasonLocal;
	}

	@Override
	public ICCDatabaseStructureElement getExtern() {
		return SeasonExtern;
	}

	@Override
	public List<Tuple<IEProperty, IEProperty>> getMetaDiff() {
		return MetadataDiff;
	}

	@Override
	public boolean getNeedsDelete() {
		return NeedsDelete;
	}

	@Override
	public boolean getNeedsDeleteRecursive() {
		return NeedsDeleteByParent;
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
}
