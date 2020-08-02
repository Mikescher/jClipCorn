package de.jClipCorn.gui.frames.databaseHistoryFrame;

import de.jClipCorn.database.history.CCHistorySingleChange;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHistoryChangesTable extends JCCSimpleTable<CCHistorySingleChange> {
	private static final long serialVersionUID = -7175124665697003916L;

	private final JTextComponent edValueOld;
	private final JTextComponent edValueNew;
	
	public DatabaseHistoryChangesTable(JTextComponent tfOldValue, JTextComponent tfNewValue)
	{
		edValueOld = tfOldValue;
		edValueNew = tfNewValue;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<CCHistorySingleChange>> configureColumns() {
		List<JCCSimpleColumnPrototype<CCHistorySingleChange>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"DatabaseHistoryFrame.Table.ColumnField",
				e -> e.Field,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"*",
				"DatabaseHistoryFrame.Table.ColumnOld",
				e -> e.OldValue==null ? "<NULL>" : limit(e.OldValue),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"*",
				"DatabaseHistoryFrame.Table.ColumnNew",
				e -> e.NewValue==null ? "<NULL>" : limit(e.NewValue),
				null,
				null));
		
		return r;
	}

	private String limit(String str) {
		str = str.split("\\r?\\n")[0]; //$NON-NLS-1$
		if (str.length() > 256) return str.substring(0, 256-3)+"..."; //$NON-NLS-1$
		return str;
	}

	@Override
	protected void OnDoubleClickElement(CCHistorySingleChange element) {
		// 
	}

	@Override
	protected void OnSelectElement(CCHistorySingleChange element) {
		edValueOld.setVisible(element.OldValue != null);
		edValueOld.setText((element.OldValue == null) ? "" : element.OldValue);

		edValueNew.setVisible(element.NewValue != null);
		edValueNew.setText((element.NewValue == null) ? "" : element.NewValue);
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}

	@Override
	protected boolean isSortable(int col) {
		return true;
	}

}
