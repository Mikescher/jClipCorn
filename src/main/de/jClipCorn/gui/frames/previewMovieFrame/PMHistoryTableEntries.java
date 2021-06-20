package de.jClipCorn.gui.frames.previewMovieFrame;

import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.lambda.Func1to0;
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
				"auto",
				Str.Empty,
				null,
				e -> e.Table.getIcon(),
				e -> Str.toProperCase(e.Table.Name)));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"DatabaseHistoryFrame.Table.ColumnAction",
				e -> Str.toProperCase(e.Action.Name),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"DatabaseHistoryFrame.Table.ColumnTime",
				CCCombinedHistoryEntry::formatTime,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"*,min=auto",
				"DatabaseHistoryFrame.Table.ColumnChangeCount",
				e -> Integer.toString(e.Changes.size()),
				null,
				null));
		
		return r;
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
	protected boolean isMultiselect() {
		return false;
	}
}
