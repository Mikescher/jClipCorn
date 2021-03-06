package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class MovieMatch extends ComparisonMatch {
	public final CompareState State;

	public final CCMovie MovieLocal;
	public final CCMovie MovieExtern;

	public final boolean NeedsUpdateMetadata;
	public final boolean NeedsUpdateCover;
	public final boolean NeedsUpdateFile;
	public final boolean NeedsCreateNew;
	public final boolean NeedsDelete;

	public final List<Tuple<IEProperty, IEProperty>> MetadataDiff;

	public MovieMatch(CompareState state, CCMovie loc, CCMovie ext, boolean updMeta, boolean updCover, boolean updFile, boolean copy, boolean del, List<Tuple<IEProperty, IEProperty>> diff) {
		State               = state;
		MovieLocal          = loc;
		MovieExtern         = ext;
		NeedsUpdateMetadata = updMeta;
		NeedsUpdateCover    = updCover;
		NeedsUpdateFile     = updFile;
		NeedsCreateNew      = copy;
		NeedsDelete         = del;
		MetadataDiff        = diff;
	}

	@Override
	public String getTypeStr() {
		return "Movie";
	}

	@Override
	public String getStrAction() {
		var r = new ArrayList<String>();
		if (NeedsCreateNew) r.add("FullCopy");
		if (NeedsDelete) r.add("Delete");
		if (NeedsUpdateMetadata) r.add("UpdateData");
		if (NeedsUpdateCover) r.add("CopyCover");
		if (NeedsUpdateFile) r.add("CopyVideo");
		return "[" + CCStreams.iterate(r).stringjoin(e -> e, ", ") + "]";
	}

	@Override
	public ICCDatabaseStructureElement getLocal() {
		return MovieLocal;
	}

	@Override
	public ICCDatabaseStructureElement getExtern() {
		return MovieExtern;
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
		return false;
	}

	@Override
	public boolean getNeedsUpdateCover() {
		return NeedsUpdateCover;
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
