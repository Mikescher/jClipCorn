package de.jClipCorn.gui.frames.databaseHistoryFrame;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

public class DatabaseHistoryTable extends JCCSimpleTable<CCCombinedHistoryEntry> {
	private static final long serialVersionUID = -7175124665697003916L;

	@Override
	protected List<JCCSimpleColumnPrototype<CCCombinedHistoryEntry>> configureColumns() {
		List<JCCSimpleColumnPrototype<CCCombinedHistoryEntry>> r = new ArrayList<>();

		//r.add(new JCCSimpleColumnPrototype<>(
		//		"UpdateCodecFrame.Table.ColumnQualityNew",
		//		e -> e.getNewMediaInfo().getCategory(e.getSourceGenres()).getLongText(),
		//		e -> e.getNewMediaInfo().getCategory(e.getSourceGenres()).getIcon(),
		//		e -> e.getNewMediaInfo().getCategory(e.getSourceGenres()).getTooltip()));
		
		return r;
	}

	@Override
	protected void OnDoubleClickElement(CCCombinedHistoryEntry element) {
		// 
	}

	@Override
	protected void OnSelectElement(CCCombinedHistoryEntry element) {
		// 
	}

	@Override
	protected int getColumnAdjusterMaxWidth() {
		return 500;
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}

	@Override
	protected boolean isSortable(int col) {
		return false;
	}

}
