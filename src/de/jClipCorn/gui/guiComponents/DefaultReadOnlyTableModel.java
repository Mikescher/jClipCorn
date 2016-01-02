package de.jClipCorn.gui.guiComponents;

import javax.swing.table.DefaultTableModel;

public class DefaultReadOnlyTableModel extends DefaultTableModel {
	private static final long serialVersionUID = 7862492122047099052L;

	@Override
	public boolean isCellEditable(int row, int column){  
        return false;  
    }
}
