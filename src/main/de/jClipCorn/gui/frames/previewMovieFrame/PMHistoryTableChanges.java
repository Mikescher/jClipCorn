package de.jClipCorn.gui.frames.previewMovieFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.history.CCHistorySingleChange;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;

import java.util.ArrayList;
import java.util.List;

public class PMHistoryTableChanges extends JCCSimpleTable<CCHistorySingleChange> {
	private static final long serialVersionUID = -7175124665697003916L;

	@DesignCreate
	private static PMHistoryTableChanges designCreate() { return new PMHistoryTableChanges(ICCWindow.Dummy.frame()); }

	public PMHistoryTableChanges(ICCWindow f) {
		super(f);
	}

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<CCHistorySingleChange>> configureColumns() {
		List<JCCSimpleColumnPrototype<CCHistorySingleChange>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"DatabaseHistoryFrame.Table.ColumnField",
				e -> e.Field,
				null,
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"*,expandonly",
				"DatabaseHistoryFrame.Table.ColumnOld",
				e -> e.OldValue==null ? "<NULL>" : Str.limit(Str.firstLine(e.OldValue), 131),
				null,
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"*,expandonly",
				"DatabaseHistoryFrame.Table.ColumnNew",
				e -> e.NewValue==null ? "<NULL>" : Str.limit(Str.firstLine(e.NewValue), 131),
				null,
				null,
				true));
		
		return r;
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
	protected boolean isMultiselect() {
		return true;
	}
}
