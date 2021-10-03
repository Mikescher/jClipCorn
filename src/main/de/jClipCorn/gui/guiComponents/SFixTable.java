package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class SFixTable extends JTable {
	private static final long serialVersionUID = 1082882838948078289L;

	public boolean MultiSelect = false;

	protected final CCMovieList movielist;

	public SFixTable(CCMovieList ml, TableModel dm) {
		super(dm);
		movielist = ml;

		fixTableSort();

		initListener();

		initLnF();
	}

	public SFixTable(CCMovieList ml) {
		super();
		movielist = ml;

		fixTableSort();

		initListener();
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	private void initListener() {
		addMouseListener(new MouseAdapter() {
			final int defaultTimeout = ToolTipManager.sharedInstance().getInitialDelay();

			@Override
			public void mouseExited(MouseEvent e) {
				ToolTipManager.sharedInstance().setInitialDelay(defaultTimeout);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				ToolTipManager.sharedInstance().setInitialDelay(0);
			}
		});
	}

	private void initLnF() {
		if (LookAndFeelManager.isFlatLaf())
		{
			setShowHorizontalLines(true);
			setShowVerticalLines(true);
		}
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		
		if (MultiSelect) {
			
			super.changeSelection(rowIndex, columnIndex, toggle, extend);
			
		} else {
			
			super.changeSelection(rowIndex, columnIndex, toggle && (!extend), false);

			if (getSelectedRowCount() > 1) {
				super.changeSelection(rowIndex, columnIndex, false, false);
			}
			
		}
		
	}

	private void fixTableSort() {
		// this causes column resizing to no longer work :/
		// http://stackoverflow.com/questions/5477965/how-to-restore-the-original-row-order-with-jtables-row-sorter
		/*
		for (MouseListener mouseListener : getTableHeader().getMouseListeners()) {
			if (mouseListener instanceof javax.swing.plaf.basic.BasicTableHeaderUI.MouseInputHandler) {
				getTableHeader().removeMouseListener(mouseListener);
			}
		}
		*/

		getTableHeader().addMouseListener(new MouseAdapter() {
			private SortOrder currentOrder = SortOrder.UNSORTED;

			@Override
			public void mouseClicked(MouseEvent e) {
				int column = getTableHeader().columnAtPoint(e.getPoint());
				if (column == -1) return;
				RowSorter<?> sorter = getRowSorter();

				if (sorter == null) return;
				if (sorter instanceof TableRowSorter<?> && !((TableRowSorter<?>)sorter).isSortable(column)) return;

				List<SortKey> sortKeys = new ArrayList<>();
				switch (currentOrder) {
				case UNSORTED:
					sortKeys.add(new RowSorter.SortKey(column, currentOrder = SortOrder.ASCENDING));
					break;
				case ASCENDING:
					sortKeys.add(new RowSorter.SortKey(column, currentOrder = SortOrder.DESCENDING));
					break;
				case DESCENDING:
					sortKeys.add(new RowSorter.SortKey(column, currentOrder = SortOrder.UNSORTED));
					break;
				}
				sorter.setSortKeys(sortKeys);
			}
		});
	}

	public void resetSort() {
		for (int i = 0; i < getColumnCount(); i++) {
			RowSorter<?> sorter = getRowSorter();
			List<SortKey> sortKeys = new ArrayList<>();
			sortKeys.add(new RowSorter.SortKey(i, SortOrder.UNSORTED));
			sorter.setSortKeys(sortKeys);
		}
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);

		if (c instanceof JComponent) {
			Object value = getValueAt(row, column);
			int realColumn = convertColumnIndexToModel(column); // So you can move the positions of the Columns ...
			int realRow    = convertRowIndexToModel(row); // So you can move the positions of the Columns ...
			JComponent jc = (JComponent) c;

			String tip = getTooltip(realColumn, realRow, value);

			if (tip != null && tip.trim().isEmpty()) tip = null;
			
			if (tip != null && !tip.toLowerCase().startsWith("<html")) //$NON-NLS-1$
			{
				StringBuilder b = new StringBuilder();
				b.append("<html>"); //$NON-NLS-1$
				b.append(tip);

				b.append(Str.repeat("<!--HACK-->", Math.max(0, row))); //$NON-NLS-1$
				b.append("</html>"); //$NON-NLS-1$
				
				tip = b.toString();
			}
			else if (tip != null && tip.toLowerCase().startsWith("<html")) //$NON-NLS-1$
			{
				StringBuilder b = new StringBuilder();
				b.append(Str.repeat("<!--HACK-->", Math.max(0, row))); //$NON-NLS-1$

				int idx = tip.toLowerCase().indexOf("</html>"); //$NON-NLS-1$
				if (idx >= 0) tip = tip.substring(0, idx) + b.toString() + tip.substring(idx);
			}

			jc.setToolTipText(tip);
		}

		return c;
	}

	protected abstract String getTooltip(int modelcolumn, int modelrow, Object value);
}
