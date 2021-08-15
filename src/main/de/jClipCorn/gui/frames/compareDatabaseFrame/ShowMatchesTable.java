package de.jClipCorn.gui.frames.compareDatabaseFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;

import java.util.ArrayList;
import java.util.List;

public class ShowMatchesTable extends JCCSimpleTable<ComparisonMatch> {
	@DesignCreate
	private static ShowMatchesTable designCreate() { return new ShowMatchesTable(null, true, true); }

	private final CompareDatabaseFrame _owner;

	private final boolean _hasLoc;
	private final boolean _hasExt;

	public ShowMatchesTable(CompareDatabaseFrame f, boolean hasLoc, boolean hasExt) {
		super(f);

		_hasLoc = hasLoc;
		_hasExt = hasExt;
		_owner  = f;

		setColumnConfig(configureColumns()); // config columns again, because this time _hasLoc and _hasExt are actually set
	}

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<ComparisonMatch>> configureColumns() {
		List<JCCSimpleColumnPrototype<ComparisonMatch>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(this, "auto", "CompareDatabaseFrame.table.column_Type", ComparisonMatch::getTypeStr, null, null, true));

		if (_hasLoc) r.add(new JCCSimpleColumnPrototype<>(this, "auto",         "CompareDatabaseFrame.table.column_LocID",    e -> String.valueOf(e.getLocID().mapOrElse(String::valueOf, Str.Empty)), null, null, true));
		if (_hasExt) r.add(new JCCSimpleColumnPrototype<>(this, "auto",         "CompareDatabaseFrame.table.column_ExtID",    e -> String.valueOf(e.getExtID().mapOrElse(String::valueOf, Str.Empty)), null, null, true));
		if (_hasLoc) r.add(new JCCSimpleColumnPrototype<>(this, "auto,max=400", "CompareDatabaseFrame.table.column_LocTitle", e -> e.getLocTitle().orElse(Str.Empty),                                  null, null, true));
		if (_hasExt) r.add(new JCCSimpleColumnPrototype<>(this, "auto,max=400", "CompareDatabaseFrame.table.column_ExtTitle", e -> e.getExtTitle().orElse(Str.Empty),                                  null, null, true));

		r.add(new JCCSimpleColumnPrototype<>(this, "auto", "CompareDatabaseFrame.table.column_Action", ComparisonMatch::getStrAction, null, null, true));

		return r;
	}

	@Override
	protected void OnDoubleClickElement(ComparisonMatch element) {
		var loc = element.getLocal();
		var ext = element.getExtern();

		if (loc != null) showFrame(loc);
		if (ext != null) showFrame(ext);
	}

	private void showFrame(ICCDatabaseStructureElement elem)
	{
		if (elem instanceof CCMovie)   PreviewMovieFrame.show(_owner,  (CCMovie)elem,   true);
		if (elem instanceof CCSeries)  PreviewSeriesFrame.show(_owner, (CCSeries)elem,  true);
		if (elem instanceof CCSeason)  PreviewSeriesFrame.show(_owner, (CCSeason)elem,  true);
		if (elem instanceof CCEpisode) PreviewSeriesFrame.show(_owner, (CCEpisode)elem, true);
	}

	@Override
	protected void OnSelectElement(ComparisonMatch element) {
		_owner.showDiffStr(element.getDiffStr());
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
