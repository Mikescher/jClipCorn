package de.jClipCorn.util;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {
	private final static int COLUMNBORDER_WIDTH = 6; //Wert durch testen ermittelt ...
	
	private JTable table;
	private int spacing;
	private boolean isColumnHeaderIncluded;
	private boolean isColumnDataIncluded;
	private boolean isOnlyAdjustLarger;
	private boolean isDynamicAdjustment;
	private boolean isResizeAdjuster;
	private Map<TableColumn, Integer> columnSizes = new HashMap<TableColumn, Integer>();

	/*
	 * Specify the table and use default spacing
	 */
	public TableColumnAdjuster(JTable table) {
		this(table, 6);
	}

	/*
	 * Specify the table and spacing
	 */
	public TableColumnAdjuster(JTable table, int spacing) {
		this.table = table;
		this.spacing = spacing;
		configureTable();
		setColumnHeaderIncluded(true);
		setColumnDataIncluded(true);
		setOnlyAdjustLarger(true);
		setDynamicAdjustment(false);
		setResizeAdjuster(false);
		installActions();
		installResizeAdjuster();
	}
	
	private void configureTable() {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	
	private void installResizeAdjuster() {
		table.getParent().addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent arg0) {
				// <epmty>
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				if (isResizeAdjuster) {
					resizeLastColumn();
				}
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
				// <epmty>
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
				// <epmty>
			}
		});
	}
	
	private void resizeLastColumn() {	
		int column = table.getColumnCount() - 1;
		
		TableColumn tableColumn = table.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable())
			return;

		int columnHeaderWidth = getColumnHeaderWidth(column);
		int columnDataWidth = getColumnDataWidth(column);
		int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);
		
		int tabPos = getColumnPosition(column);
		int tabLeft = tabPos + preferredWidth - COLUMNBORDER_WIDTH;
		int contWid = getContainerWidth();
		
		if (tabLeft < contWid) {
			preferredWidth = contWid - tabPos - COLUMNBORDER_WIDTH;
			updateTableColumn(column, preferredWidth);
		}
	}
	
	private int getColumnPosition(int column) {
		int x = 0;
		
		for (int i = column - 1; i >= 0; i--) {
			TableColumn tableColumn = table.getColumnModel().getColumn(i);
			x += tableColumn.getWidth();
		}
		
		return x;
	}
	
	private int getContainerWidth() {
		return table.getParent().getWidth();
	}

	public void adjustColumns() {
		TableColumnModel tcm = table.getColumnModel();

		for (int i = 0; i < tcm.getColumnCount(); i++) {
			adjustColumn(i);
		}
		
		if (isResizeAdjuster) {
			resizeLastColumn();
		}
	}

	public void adjustColumn(final int column) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable())
			return;

		int columnHeaderWidth = getColumnHeaderWidth(column);
		int columnDataWidth = getColumnDataWidth(column);
		int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);

		updateTableColumn(column, preferredWidth);
	}

	private int getColumnHeaderWidth(int column) {
		if (!isColumnHeaderIncluded)
			return 0;

		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		Object value = tableColumn.getHeaderValue();
		TableCellRenderer renderer = tableColumn.getHeaderRenderer();

		if (renderer == null) {
			renderer = table.getTableHeader().getDefaultRenderer();
		}

		Component c = renderer.getTableCellRendererComponent(table, value, false, false, -1, column);
		return c.getPreferredSize().width;
	}

	private int getColumnDataWidth(int column) {
		if (!isColumnDataIncluded)
			return 0;

		int preferredWidth = 0;
		int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();

		for (int row = 0; row < table.getRowCount(); row++) {
			preferredWidth = Math.max(preferredWidth, getCellDataWidth(row, column));

			if (preferredWidth >= maxWidth) {
				break;
			}
		}

		return preferredWidth;
	}

	private int getCellDataWidth(int row, int column) {
		TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
		Component c = table.prepareRenderer(cellRenderer, row, column);
		int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
		return width;
	}

	private void updateTableColumn(int column, int width) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);

		if (!tableColumn.getResizable())
			return;

		width += spacing;

		if (isOnlyAdjustLarger) {
			width = Math.max(width, tableColumn.getPreferredWidth());
		}

		columnSizes.put(tableColumn, new Integer(tableColumn.getWidth()));

		setColumnWidth(tableColumn, width);
	}

	private void setColumnWidth(final TableColumn tableColumn, final int width) {
		table.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(width);
	}

	public void restoreColumns() {
		TableColumnModel tcm = table.getColumnModel();

		for (int i = 0; i < tcm.getColumnCount(); i++) {
			restoreColumn(i);
		}
	}

	private void restoreColumn(int column) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		Integer width = columnSizes.get(tableColumn);

		if (width != null) {
			table.getTableHeader().setResizingColumn(tableColumn);
			tableColumn.setWidth(width.intValue());
		}
	}

	public void setColumnHeaderIncluded(boolean isColumnHeaderIncluded) {
		this.isColumnHeaderIncluded = isColumnHeaderIncluded;
	}

	public void setColumnDataIncluded(boolean isColumnDataIncluded) {
		this.isColumnDataIncluded = isColumnDataIncluded;
	}

	public void setOnlyAdjustLarger(boolean isOnlyAdjustLarger) {
		this.isOnlyAdjustLarger = isOnlyAdjustLarger;
	}
	
	public void setResizeAdjuster(boolean isResizeAdjuster) {
		this.isResizeAdjuster = isResizeAdjuster;
	}

	public void setDynamicAdjustment(boolean isDynamicAdjustment) {
		if (this.isDynamicAdjustment != isDynamicAdjustment) {
			if (isDynamicAdjustment) {
				table.addPropertyChangeListener(this);
				table.getModel().addTableModelListener(this);
			} else {
				table.removePropertyChangeListener(this);
				table.getModel().removeTableModelListener(this);
			}
		}

		this.isDynamicAdjustment = isDynamicAdjustment;
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if ("model".equals(e.getPropertyName())) //$NON-NLS-1$
		{
			TableModel model = (TableModel) e.getOldValue();
			model.removeTableModelListener(this);

			model = (TableModel) e.getNewValue();
			model.addTableModelListener(this);
			adjustColumns();
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (!isColumnDataIncluded)
			return;

		if (e.getType() == TableModelEvent.UPDATE) {
			int column = table.convertColumnIndexToView(e.getColumn());

			if (isOnlyAdjustLarger) {
				int row = e.getFirstRow();
				TableColumn tableColumn = table.getColumnModel().getColumn(column);

				if (tableColumn.getResizable()) {
					int width = getCellDataWidth(row, column);
					updateTableColumn(column, width);
				}
			}

			else {
				adjustColumn(column);
			}
		}

		else {
			adjustColumns();
		}
	}

	private void installActions() {
		installColumnAction(true, true, "adjustColumn", "control ADD"); //$NON-NLS-1$//$NON-NLS-2$
		installColumnAction(false, true, "adjustColumns", "control shift ADD"); //$NON-NLS-1$ //$NON-NLS-2$
		installColumnAction(true, false, "restoreColumn", "control SUBTRACT"); //$NON-NLS-1$ //$NON-NLS-2$
		installColumnAction(false, false, "restoreColumns", "control shift SUBTRACT"); //$NON-NLS-1$ //$NON-NLS-2$

		installToggleAction(true, false, "toggleDynamic", "control MULTIPLY"); //$NON-NLS-1$ //$NON-NLS-2$
		installToggleAction(false, true, "toggleLarger", "control DIVIDE"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void installColumnAction(boolean isSelectedColumn, boolean isAdjust, String key, String keyStroke) {
		Action action = new ColumnAction(isSelectedColumn, isAdjust);
		KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
		table.getInputMap().put(ks, key);
		table.getActionMap().put(key, action);
	}

	private void installToggleAction(boolean isToggleDynamic, boolean isToggleLarger, String key, String keyStroke) {
		Action action = new ToggleAction(isToggleDynamic, isToggleLarger);
		KeyStroke ks = KeyStroke.getKeyStroke(keyStroke);
		table.getInputMap().put(ks, key);
		table.getActionMap().put(key, action);
	}

	class ColumnAction extends AbstractAction {
		private static final long serialVersionUID = -8331954197187413892L;
		private boolean isSelectedColumn;
		private boolean isAdjust;

		public ColumnAction(boolean isSelectedColumn, boolean isAdjust) {
			this.isSelectedColumn = isSelectedColumn;
			this.isAdjust = isAdjust;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (isSelectedColumn) {
				int[] columns = table.getSelectedColumns();

				for (int i = 0; i < columns.length; i++) {
					if (isAdjust)
						adjustColumn(columns[i]);
					else
						restoreColumn(columns[i]);
				}
			} else {
				if (isAdjust)
					adjustColumns();
				else
					restoreColumns();
			}
		}
	}

	class ToggleAction extends AbstractAction {
		private static final long serialVersionUID = 2493947359219892211L;
		private boolean isToggleDynamic;
		private boolean isToggleLarger;

		public ToggleAction(boolean isToggleDynamic, boolean isToggleLarger) {
			this.isToggleDynamic = isToggleDynamic;
			this.isToggleLarger = isToggleLarger;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (isToggleDynamic) {
				setDynamicAdjustment(!isDynamicAdjustment);
				return;
			}

			if (isToggleLarger) {
				setOnlyAdjustLarger(!isOnlyAdjustLarger);
				return;
			}
		}
	}
}
