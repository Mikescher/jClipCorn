package de.jClipCorn.gui.frames.previewMovieFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.database.history.CCHistorySingleChange;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;

import java.util.ArrayList;
import java.util.List;

public class PMHistoryTableEntries extends JCCSimpleTable<CCCombinedHistoryEntry> {
	private static final long serialVersionUID = -7175124665697003916L;

	private final List<PMHSelectionListener> _handler = new ArrayList<>();

	@DesignCreate
	private static PMHistoryTableEntries designCreate() { return new PMHistoryTableEntries(ICCWindow.Dummy.frame()); }

	public PMHistoryTableEntries(ICCWindow f) {
		super(f);
	}

	public void addSelectionListener(final PMHSelectionListener l) {
		_handler.add(l);
	}

	public void removeSelectionListener(final PMHSelectionListener l) {
		_handler.remove(l);
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<CCCombinedHistoryEntry> configureColumns() {
		JCCSimpleColumnList<CCCombinedHistoryEntry> r = new JCCSimpleColumnList<>(this);

		r.add(Str.Empty)
				.withSize("auto")
				.withIcon(e -> e.Table.getIcon())
				.withTooltip(e -> Str.toProperCase(e.Table.Name));

		r.add("DatabaseHistoryFrame.Table.ColumnAction")
				.withSize("auto")
				.withText(e -> Str.toProperCase(e.Action.Name));

		r.add("DatabaseHistoryFrame.Table.ColumnTime")
				.withSize("auto")
				.withText(CCCombinedHistoryEntry::formatTime);

		r.add("DatabaseHistoryFrame.Table.ColumnChangeCount")
				.withSize("*,min=auto")
				.withText(e -> Integer.toString(e.Changes.size()));
		
		return r;
	}

	@Override
	protected void OnDoubleClickElement(CCCombinedHistoryEntry element) {
		//
	}

	@Override
	protected void OnSelectElement(CCCombinedHistoryEntry element) {
		for (var h : _handler) h.actionPerformed(new PMHSelectionListener.PMHSelectionEvent(this, element));
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
