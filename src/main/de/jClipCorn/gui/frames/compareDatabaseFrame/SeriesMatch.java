package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class SeriesMatch extends ComparisonMatch {
	public final CompareState State;

	public final CCSeries SeriesLocal;
	public final CCSeries SeriesExtern;

	public final List<SeasonMatch> Seasons = new ArrayList<>();

	public final boolean NeedsUpdateMetadata;
	public final boolean NeedsUpdateCover;
	public final boolean NeedsCreateNew;
	public final boolean NeedsDelete;

	public final List<Tuple<IEProperty, IEProperty>> MetadataDiff;

	public SeriesMatch(CompareState state, CCSeries loc, CCSeries ext, boolean updMeta, boolean updCover, boolean copy, boolean del, List<Tuple<IEProperty, IEProperty>> diff) {
		State               = state;
		SeriesLocal         = loc;
		SeriesExtern        = ext;
		NeedsUpdateMetadata = updMeta;
		NeedsUpdateCover    = updCover;
		NeedsCreateNew      = copy;
		NeedsDelete         = del;
		MetadataDiff        = diff;
	}

	public SeasonMatch addSeasonLocalOnly(CCSeason loc) {
		var docopy = State.Ruleset.ShouldAddLocal(loc.getLocalID());
		var match = new SeasonMatch(this, loc, null, false, false, docopy, false, new ArrayList<>());
		Seasons.add(match);
		State.AllSeasons.add(match);
		State.ProgressCallback.stepSub(1, loc.getLocalID() + "");
		return match;
	}

	public SeasonMatch addSeasonExternOnly(CCSeason ext) {
		var dodel = State.Ruleset.ShouldDeleteExtern(ext.getLocalID());
		var match = new SeasonMatch(this, null, ext, false, false, false, dodel, new ArrayList<>());
		Seasons.add(match);
		State.AllSeasons.add(match);
		State.ProgressCallback.stepSub(1, ext.getLocalID() + "");
		return match;
	}

	public SeasonMatch addSeasonMatch(CCSeason loc, CCSeason ext) {
		var updateCover = State.Ruleset.ShouldUpdateCover(loc.getLocalID(), ext.getLocalID()) && Str.equals(loc.getCoverInfo().Checksum, ext.getCoverInfo().Checksum);

		var propLoc = CCStreams.iterate(loc.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var propExt = CCStreams.iterate(ext.getProperties()).filter(e -> e.getValueType() == EPropertyType.OBJECTIVE_METADATA);
		var diffMeta = CCStreams.zip(propLoc, propExt)
				                .filter(p -> !Str.equals(p.Item1.serializeToString(), p.Item2.serializeToString()))
				                .filter(p -> State.Ruleset.ShouldUpdateMetadata(loc.getLocalID(), ext.getLocalID(), p.Item1, p.Item2))
				                .toList();

		var match = new SeasonMatch(this, loc, ext, !diffMeta.isEmpty(), updateCover, false, false, diffMeta);
		Seasons.add(match);
		State.AllSeasons.add(match);
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
		return "Series";
	}

	@Override
	public ICCDatabaseStructureElement getLocal() {
		return SeriesLocal;
	}

	@Override
	public ICCDatabaseStructureElement getExtern() {
		return SeriesExtern;
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
