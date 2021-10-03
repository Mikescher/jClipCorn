package de.jClipCorn.gui.guiComponents.jCCPrimaryTable;

import de.jClipCorn.features.table.renderer.TableModelRowColorInterface;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class JCCPrimaryTableModel<TData, TEnum> extends AbstractTableModel implements TableModelRowColorInterface, TableModelListener {

	private final JCCPrimaryTable<TData, TEnum> owner;

	private Tuple<List<Integer>, Boolean> rowIndexMap = null; // [modelrow] -> datastoragerow  // second parameter is IsVolatile
	private boolean lockRowIndexMap = false;

	public JCCPrimaryTableModel(JCCPrimaryTable<TData, TEnum> owner) {
		super();
		this.owner = owner;

		this.addTableModelListener(this);
	}

	@Override
	public String getColumnName(int col) {
		return owner.config.get(col).Header;
	}

	@Override
	public int getColumnCount() {
		return owner.config.size();
	}

	@Override
	public int getRowCount() {
		if (rowIndexMap != null) return rowIndexMap.Item1.size();
		else return owner.getElementCountInDatastore();
	}

	@Override
	public Object getValueAt(int modelrow, int modelcol) {
		return getElementByModelRowIndex(modelrow);
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (rowIndexMap != null && rowIndexMap.Item2) clearRowIndexMapping();
		fireTableCellUpdated(row, col);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		if (rowIndexMap != null && rowIndexMap.Item2) clearRowIndexMapping();
	}

	@Override
	public Opt<Color> getRowColor(int visualrow) {
		var modelrow = owner.table.convertRowIndexToModel(visualrow);
		var element = getElementByModelRowIndex(modelrow);

		return owner.getRowColor(visualrow, modelrow, element);
	}

	public TData getElementByModelRowIndex(int modelrow) {
		if (modelrow < 0 || modelrow >= getRowCount()) return null;

		var dsrow = modelrow;

		if (rowIndexMap != null) {
			if (modelrow >= rowIndexMap.Item1.size()) {
				if (rowIndexMap.Item2) clearRowIndexMapping();
				return null;
			}
			dsrow = rowIndexMap.Item1.get(modelrow);
		} else {
			if (modelrow >= getRowCount()) return null;
		}

		return owner.getElementFromDatastoreByIndex(dsrow);
	}

	public void setRowIndexMapping(List<Integer> m, boolean isvolatile) {
		lockRowIndexMap = true;
		rowIndexMap = Tuple.Create(m, isvolatile);
		fireTableDataChanged();
		lockRowIndexMap = false;
	}

	public void clearRowIndexMapping() {
		if (! lockRowIndexMap && rowIndexMap != null) {
			rowIndexMap = null;
			fireTableDataChanged();
		}
	}

	public boolean hasVolatileRowIndexMapping() {
		return rowIndexMap != null && rowIndexMap.Item2;
	}
}
