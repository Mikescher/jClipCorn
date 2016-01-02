package de.jClipCorn.gui.guiComponents;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class DefaultReadOnlyTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 7862492122047099052L;

	public DefaultReadOnlyTableModel() {
		super();
	}
	
	public DefaultReadOnlyTableModel(Vector<String> columnNames, int rowCount) {
		super(columnNames, rowCount);
	}
	
	public DefaultReadOnlyTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}

	@Override
	public boolean isCellEditable(int row, int column){  
        return false;  
    }
}
