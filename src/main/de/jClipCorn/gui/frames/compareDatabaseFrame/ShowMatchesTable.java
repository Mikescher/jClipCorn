package de.jClipCorn.gui.frames.compareDatabaseFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ShowMatchesTable extends JCCSimpleTable<ComparisonMatch> {
	@DesignCreate
	private static ShowMatchesTable designCreate() { return new ShowMatchesTable(new CompareDatabaseFrame(ICCWindow.Dummy.frame(), CCMovieList.createStub()), true, true); }

	private final CompareDatabaseFrame _owner;

	private final boolean _hasLoc;
	private final boolean _hasExt;

	public ShowMatchesTable(@NotNull CompareDatabaseFrame f, boolean hasLoc, boolean hasExt) {
		super(f);

		_hasLoc = hasLoc;
		_hasExt = hasExt;
		_owner  = f;

		setColumnConfig(configureColumns()); // config columns again, because this time _hasLoc and _hasExt are actually set
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<ComparisonMatch> configureColumns() {
		JCCSimpleColumnList<ComparisonMatch> r = new JCCSimpleColumnList<>(this);

		r.add("CompareDatabaseFrame.table.column_Type")
		 .withSize("auto")
		 .withText(ComparisonMatch::getTypeStr)
		 .sortable();

		if (_hasLoc) r.add("CompareDatabaseFrame.table.column_LocID")
				      .withSize("auto")
				      .withText(e -> String.valueOf(e.getLocID().mapOrElse(String::valueOf, Str.Empty)))
				      .sortable();

		if (_hasExt) r.add("CompareDatabaseFrame.table.column_ExtID")
				      .withSize("auto")
				      .withText(e -> String.valueOf(e.getExtID().mapOrElse(String::valueOf, Str.Empty)))
				      .sortable();

		if (_hasLoc) r.add("CompareDatabaseFrame.table.column_LocTitle")
				      .withSize("auto,max=400")
				      .withText(e -> e.getLocTitle().orElse(Str.Empty))
				      .sortable();

		if (_hasExt) r.add("CompareDatabaseFrame.table.column_ExtTitle")
				      .withSize("auto,max=400")
				      .withText(e -> e.getExtTitle().orElse(Str.Empty))
				      .sortable();

		r.add("CompareDatabaseFrame.table.column_Action")
		 .withSize("auto")
		 .withText(ComparisonMatch::getStrAction)
		 .sortable();

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
