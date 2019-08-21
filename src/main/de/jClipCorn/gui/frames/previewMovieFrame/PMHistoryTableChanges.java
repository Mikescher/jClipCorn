package de.jClipCorn.gui.frames.previewMovieFrame;

import de.jClipCorn.database.history.CCHistorySingleChange;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

import java.util.ArrayList;
import java.util.List;

public class PMHistoryTableChanges extends JCCSimpleTable<CCHistorySingleChange> {
	private static final long serialVersionUID = -7175124665697003916L;

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<CCHistorySingleChange>> configureColumns() {
		List<JCCSimpleColumnPrototype<CCHistorySingleChange>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				"DatabaseHistoryFrame.Table.ColumnField",
				e -> e.Field,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"DatabaseHistoryFrame.Table.ColumnOld",
				e -> e.OldValue==null ? "<NULL>" : limit(e.OldValue),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"DatabaseHistoryFrame.Table.ColumnNew",
				e -> e.NewValue==null ? "<NULL>" : limit(e.NewValue),
				null,
				null));
		
		return r;
	}

	private String limit(String str) {
		str = str.split("\\r?\\n")[0]; //$NON-NLS-1$
		if (str.length() > 131) return str.substring(128)+"..."; //$NON-NLS-1$
		return str;
	}

	@Override
	protected void OnDoubleClickElement(CCHistorySingleChange element) {
		// 
	}

	@Override
	protected void OnSelectElement(CCHistorySingleChange element) {
		// 
	}

	@Override
	protected int getColumnAdjusterMaxWidth() {
		return 500;
	}

	@Override
	protected boolean isMultiselect() {
		return true;
	}

	@Override
	protected boolean isSortable(int col) {
		return true;
	}

}
