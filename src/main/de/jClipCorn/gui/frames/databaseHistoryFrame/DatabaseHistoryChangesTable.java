package de.jClipCorn.gui.frames.databaseHistoryFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.history.CCHistorySingleChange;
import de.jClipCorn.gui.frames.createSeriesFolderStructureFrame.CSFSElement;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHistoryChangesTable extends JCCSimpleTable<CCHistorySingleChange> {
	private static final long serialVersionUID = -7175124665697003916L;

	private JTextComponent edValueOld;
	private JTextComponent edValueNew;

	@DesignCreate
	private static DatabaseHistoryChangesTable designCreate() { return new DatabaseHistoryChangesTable(new DatabaseHistoryFrame(ICCWindow.Dummy.frame(), CCMovieList.createStub())); }

	public DatabaseHistoryChangesTable(DatabaseHistoryFrame frame) {
		super(frame);
	}

	public void initRefs(JTextComponent tfOldValue, JTextComponent tfNewValue)
	{
		edValueOld = tfOldValue;
		edValueNew = tfNewValue;
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<CCHistorySingleChange> configureColumns() {
		JCCSimpleColumnList<CCHistorySingleChange> r = new JCCSimpleColumnList<>(this);

		r.add("DatabaseHistoryFrame.Table.ColumnField")
				.withSize("auto")
				.withText(e -> e.Field)
				.sortable();

		r.add("DatabaseHistoryFrame.Table.ColumnOld")
				.withSize("*")
				.withText(e -> e.OldValue==null ? "<NULL>" : Str.limit(Str.firstLine(e.OldValue), 256))
				.sortable();

		r.add("DatabaseHistoryFrame.Table.ColumnNew")
				.withSize("*")
				.withText(e -> e.NewValue==null ? "<NULL>" : Str.limit(Str.firstLine(e.NewValue), 256))
				.sortable();
		
		return r;
	}

	@Override
	protected void OnDoubleClickElement(CCHistorySingleChange element) {
		// 
	}

	@Override
	protected void OnSelectElement(CCHistorySingleChange element) {
		if (edValueOld != null)
		{
			edValueOld.setVisible(element.OldValue != null);
			edValueOld.setText((element.OldValue == null) ? Str.Empty : element.OldValue);
		}

		if (edValueNew != null)
		{
			edValueNew.setVisible(element.NewValue != null);
			edValueNew.setText((element.NewValue == null) ? Str.Empty : element.NewValue);
		}
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
