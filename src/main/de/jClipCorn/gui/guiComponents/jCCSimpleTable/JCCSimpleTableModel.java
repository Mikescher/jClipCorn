package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import de.jClipCorn.gui.guiComponents.tableRenderer.TableModelRowColorInterface;

public class JCCSimpleTableModel<TData> extends AbstractTableModel implements TableModelRowColorInterface, TableModelListener {
	private static final long serialVersionUID = 6149171502941225947L;
	
	private final List<JCCSimpleColumnPrototype<TData>> columns;
	
	private final List<TData> data = new ArrayList<>();
	
	public JCCSimpleTableModel(List<JCCSimpleColumnPrototype<TData>> _columns) {
		super();
		
		columns = _columns;
	}
	
	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnName(int col) {
		return columns.get(col).Caption;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex);
	}

	public TData getElementAtRow(int row) {
		return data.get(row);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		// NOP
	}

	@Override
	public Color getRowColor(int row) {
		return Color.WHITE;
	}

	public void setData(List<TData> newdata) {
		data.clear();
		data.addAll(newdata);
		
		fireTableDataChanged();
	}

	public void changeData(int idx, TData newData) {
		data.set(idx, newData);
		
		for (int col = 0; col < columns.size(); col++) fireTableCellUpdated(idx, col);
	}
}
