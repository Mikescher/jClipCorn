package de.jClipCorn.gui.guiComponents.jCCPrimaryTable;

import de.jClipCorn.gui.guiComponents.SFixTable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class JCCPrimarySFixTable<TData, TEnum> extends SFixTable {

	private final TableRowSorter<JCCPrimaryTableModel<TData, TEnum>> sorter;

	private final JCCPrimaryTable<TData, TEnum> owner;

	public JCCPrimarySFixTable(JCCPrimaryTable<TData, TEnum> owner) {
		super(owner.getMovielist(), owner.model);
		this.owner = owner;

		sorter = new TableRowSorter<>(owner.model);
		setRowSorter(sorter);

		init();

		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "null"); //$NON-NLS-1$
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), "null"); //$NON-NLS-1$

		this.getTableHeader().setReorderingAllowed(false);
	}

	private void init() {
		for (var col=0; col<owner.config.size(); col++)
		{
			sorter.setSortable(col, owner.config.get(col).IsSortable);
			sorter.setComparator(col, owner.config.get(col).createComparator());
		}
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		var modelcol = convertColumnIndexToModel(column);

		return owner.config.get(modelcol).createRenderer(movielist);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return owner.getUnitScrollIncrement().orElse(super.getScrollableUnitIncrement(visibleRect, orientation, direction));
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return owner.getBlockScrollIncrement().orElse(super.getScrollableBlockIncrement(visibleRect, orientation, direction));
	}

	@Override
	@SuppressWarnings("unchecked")
	protected String getTooltip(int modelcol, int modelrow, Object value) {
		return owner.config.get(modelcol).Tooltip.invoke((TData)value, modelrow);
	}

	public int getSelectedRowInModelIndex() {
		int selrow = getSelectedRow();
		if (selrow >= 0 && selrow < getRowCount()) {
			return convertRowIndexToModel(selrow);
		}
		return -1;
	}

	public boolean isSortedByColumn() {
		for (RowSorter.SortKey skey : sorter.getSortKeys()) {
			if (skey.getSortOrder() != SortOrder.UNSORTED) return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public void setRowFilter(RowFilter<JCCPrimaryTableModel<TData, TEnum>, Object> filterimpl) {
		sorter.setRowFilter(filterimpl);
	}

	public void setSortKey(RowSorter.SortKey skey) {
		sorter.setSortKeys(List.of(skey));
		sorter.sort();
	}
}
