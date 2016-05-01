package de.jClipCorn.gui.guiComponents;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;

public class SFixTable extends JTable {
	private static final long serialVersionUID = 1082882838948078289L;
	
	public SFixTable(TableModel dm) {
		super(dm);
		
		fixTableSort();
	}
	
	public SFixTable() {
		super();
		
		fixTableSort();
	}

	public SFixTable(Vector<?> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
		
		fixTableSort();
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		super.changeSelection(rowIndex, columnIndex, toggle && (! extend), false);
		
		if (getSelectedRowCount() > 1) {
			super.changeSelection(rowIndex, columnIndex, false, false);
		}
	}
	
	private void fixTableSort() {
		for (MouseListener mouseListener : getTableHeader().getMouseListeners()) {
		    if (mouseListener instanceof javax.swing.plaf.basic.BasicTableHeaderUI.MouseInputHandler) {
		        getTableHeader().removeMouseListener(mouseListener);
		    }
		}
		
		getTableHeader().addMouseListener(new MouseAdapter() {
		    private SortOrder currentOrder = SortOrder.UNSORTED;

		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int column = getTableHeader().columnAtPoint(e.getPoint());
		        RowSorter<?> sorter = getRowSorter();
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
}
