package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class EpisodeMatch extends ComparisonMatch {
	public final CompareState State;
	public final SeasonMatch Parent;

	public CCEpisode EpisodeLocal;
	public CCEpisode EpisodeExtern;

	public final boolean NeedsUpdateMetadata;
	public final boolean NeedsUpdateFile;
	public final boolean NeedsCreateNew;
	public final boolean NeedsDelete;
	public final boolean NeedsDeleteByParent;

	public final List<Tuple<IEProperty, IEProperty>> MetadataDiff;

	public EpisodeMatch(SeasonMatch parent, CCEpisode loc, CCEpisode ext, boolean updMeta, boolean updFile, boolean copy, boolean del, boolean delByParent, List<Tuple<IEProperty, IEProperty>> diff) {
		State               = parent.State;
		Parent              = parent;
		EpisodeLocal        = loc;
		EpisodeExtern       = ext;
		NeedsUpdateMetadata = updMeta;
		NeedsUpdateFile     = updFile;
		NeedsCreateNew      = copy;
		NeedsDelete         = del;
		MetadataDiff        = diff;
		NeedsDeleteByParent = delByParent;
	}

	@Override
	public String getStrAction() {
		var r = new ArrayList<String>();
		if (NeedsCreateNew) r.add("FullCopy");
		if (NeedsDelete) r.add("Delete");
		if (NeedsUpdateMetadata) r.add("UpdateData");
		if (NeedsUpdateFile) r.add("CopyVideo");
		if (NeedsDeleteByParent) r.add("DeleteByParent");
		return "[" + CCStreams.iterate(r).stringjoin(e -> e, ", ") + "]";
	}

	@Override
	public String getTypeStr() {
		return "Episode";
	}

	@Override
	public ICCDatabaseStructureElement getLocal() {
		return EpisodeLocal;
	}

	@Override
	public ICCDatabaseStructureElement getExtern() {
		return EpisodeExtern;
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
		return false;
	}

	@Override
	public boolean getNeedsUpdateFile() {
		return NeedsUpdateFile;
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
