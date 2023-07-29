package de.jClipCorn.gui.frames.parseWatchDataFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.parser.watchdata.WatchDataChangeSet;

public class ParseWatchDataTable extends JCCSimpleTable<WatchDataChangeSet> {

	@DesignCreate
	private static ParseWatchDataTable designCreate() { return new ParseWatchDataTable(ICCWindow.Dummy.frame()); }

	public ParseWatchDataTable(ICCWindow f) {
		super(f);
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<WatchDataChangeSet> configureColumns() {
		JCCSimpleColumnList<WatchDataChangeSet> r = new JCCSimpleColumnList<>(this);

		r.add("ParseWatchDataFrame.tableResults.header_0")
				.withSize("auto,min=64")
				.withText(WatchDataChangeSet::getDate)
				.unsortable();

		r.add("ParseWatchDataFrame.tableResults.header_1")
				.withSize("*")
				.withText(WatchDataChangeSet::getName)
				.unsortable();

		r.add("ParseWatchDataFrame.tableResults.header_2")
				.withSize("auto,min=64")
				.withText(WatchDataChangeSet::getSubInfo)
				.unsortable();

		r.add("ParseWatchDataFrame.tableResults.header_3")
				.withSize("auto,min=64")
				.withText(WatchDataChangeSet::getChange)
				.unsortable();

		return r;
	}

	@Override
	protected void OnDoubleClickElement(WatchDataChangeSet element) {
		// nothing
	}

	@Override
	protected void OnSelectElement(WatchDataChangeSet element) {
		// nothing
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
