package de.jClipCorn.gui.guiComponents;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class SFixTable extends JTable {
	private static final long serialVersionUID = 1082882838948078289L;
	
	public SFixTable(TableModel dm) {
		super(dm);
	}
	
	public SFixTable() {
		super();
	}

	public SFixTable(Vector<?> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		super.changeSelection(rowIndex, columnIndex, toggle && (! extend), false);
		
		if (getSelectedRowCount() > 1) {
			super.changeSelection(rowIndex, columnIndex, false, false);
		}
	}
}
