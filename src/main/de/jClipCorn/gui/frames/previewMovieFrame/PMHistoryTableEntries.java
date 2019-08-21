package de.jClipCorn.gui.frames.previewMovieFrame;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.database.history.CCHistorySingleChange;
import de.jClipCorn.database.history.CCHistoryTable;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.lambda.Func1to0;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public class PMHistoryTableEntries extends JCCSimpleTable<CCCombinedHistoryEntry> {
	private static final long serialVersionUID = -7175124665697003916L;

	private final Func1to0<CCCombinedHistoryEntry> _handler;

	public PMHistoryTableEntries(Func1to0<CCCombinedHistoryEntry> handler) {
		_handler = handler;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<CCCombinedHistoryEntry>> configureColumns() {
		List<JCCSimpleColumnPrototype<CCCombinedHistoryEntry>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				Str.Empty,
				null,
				e -> e.Table.getIcon(),
				e -> Str.toProperCase(e.Table.Name)));

		r.add(new JCCSimpleColumnPrototype<>(
				"DatabaseHistoryFrame.Table.ColumnAction",
				e -> Str.toProperCase(e.Action.Name),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"DatabaseHistoryFrame.Table.ColumnTime",
				CCCombinedHistoryEntry::formatTime,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"DatabaseHistoryFrame.Table.ColumnChangeCount",
				e -> Integer.toString(e.Changes.size()),
				null,
				null));
		
		return r;
	}

	private String format(CCCombinedHistoryEntry entry, ICCDatabaseStructureElement e) {
		if (e == null) {

			if (entry.Table == CCHistoryTable.COVERS) {
				CCHistorySingleChange n = CCStreams.iterate(entry.Changes).firstOrNull(c -> Str.equals(c.Field, "FILENAME")); //$NON-NLS-1$
				if (n != null && n.NewValue != null) return n.NewValue;
				if (n != null && n.OldValue != null) return n.OldValue;
			} else if (entry.Table == CCHistoryTable.ELEMENTS || entry.Table == CCHistoryTable.SEASONS || entry.Table == CCHistoryTable.EPISODES) {
				CCHistorySingleChange n = CCStreams.iterate(entry.Changes).firstOrNull(c -> Str.equals(c.Field, "NAME")); //$NON-NLS-1$
				if (n != null && n.NewValue != null) return n.NewValue;
				if (n != null && n.OldValue != null) return n.OldValue;
			} else if (entry.Table == CCHistoryTable.GROUPS) {
				return entry.ID;
			} else if (entry.Table == CCHistoryTable.INFO) {
				return entry.ID;
			}

			return "{{ID := "+entry.ID+"}}"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return e.getQualifiedTitle();
	}

	@Override
	protected void OnDoubleClickElement(CCCombinedHistoryEntry element) {
		//
	}

	@Override
	protected void OnSelectElement(CCCombinedHistoryEntry element) {
		_handler.invoke(element);
	}

	@Override
	protected int getColumnAdjusterMaxWidth() {
		return 800;
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
