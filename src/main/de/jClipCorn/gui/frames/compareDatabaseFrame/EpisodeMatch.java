package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;

public class EpisodeMatch extends ComparisonMatch {
	public final CompareState State;

	public CCEpisode EpisodeLocal;
	public CCEpisode EpisodeExtern;

	public final boolean NeedsUpdateMetadata;
	public final boolean NeedsUpdateFile;
	public final boolean NeedsCreateNew;
	public final boolean NeedsDelete;

	public EpisodeMatch(CompareState state, CCEpisode loc, CCEpisode ext, boolean updMeta, boolean updFile, boolean copy, boolean del) {
		State = state;
		EpisodeLocal = loc;
		EpisodeExtern = ext;
		NeedsUpdateMetadata = updMeta;
		NeedsUpdateFile = updFile;
		NeedsCreateNew = copy;
		NeedsDelete = del;
	}

	@Override
	public String getStrAction() {
		var r = new ArrayList<String>();
		if (NeedsCreateNew) r.add("FullCopy");
		if (NeedsDelete) r.add("Delete");
		if (NeedsUpdateMetadata) r.add("UpdateData");
		if (NeedsUpdateFile) r.add("CopyVideo");
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
	public boolean getNeedsDelete() {
		return NeedsDelete;
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
