package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;

import de.jClipCorn.gui.mainFrame.clipTable.ClipVerticalScrollbarUI;
import de.jClipCorn.util.TableColumnAdjuster;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;

public abstract class JCCSimpleTable<TData> extends JScrollPane implements ListSelectionListener, MouseListener {
	private static final long serialVersionUID = -87600498201225357L;
	
	private final List<JCCSimpleColumnPrototype<TData>> columns;
	private final JCCSimpleTableModel<TData> model;
	private final JCCSimpleSFixTable<TData> table;
	private final TableRowSorter<JCCSimpleTableModel<TData>> sorter;

	private final TableColumnAdjuster adjuster;
	
	public JCCSimpleTable() {
		super();
		
		columns = configureColumns();
		model = new JCCSimpleTableModel<>(columns);
		sorter = new TableRowSorter<>(model);

		table = new JCCSimpleSFixTable<>(model, columns);
		configureTable();

		this.setViewportView(table);

		adjuster = new TableColumnAdjuster(table);
		adjuster.setMaxAdjustWidth(getColumnAdjusterMaxWidth());
		adjuster.setOnlyAdjustLarger(false);
	}

	private void configureTable() {
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowHeight(18);
		table.MultiSelect = isMultiselect();

		table.getSelectionModel().addListSelectionListener(this);
		table.addMouseListener(this);
		
		table.setRowSorter(sorter);
		
		if (this.getVerticalScrollBar().getUI() instanceof WindowsScrollBarUI)
			this.getVerticalScrollBar().setUI(new ClipVerticalScrollbarUI(32));
	}

	public void autoResize() {
		adjuster.adjustColumns();
		adjuster.setResizeAdjuster(true);
	}

	public int getSelectedRow() {
		int selrow = table.getSelectedRow();
		if (selrow >= 0 && selrow < table.getRowCount()) {
			return table.convertRowIndexToModel(selrow);
		}
		return -1;
	}

	public List<Integer> getSelectedRows() {
		int[] selrow = table.getSelectedRows();
		
		List<Integer> result = new ArrayList<>();
		
		for (int i = 0; i < selrow.length; i++) {
			if (selrow[i] >= 0 && selrow[i] < table.getRowCount()) {
				result.add(table.convertRowIndexToModel(selrow[i]));
			}
		}
		
		return result;
	}

	public TData getSelectedElement() {
		int selrow = getSelectedRow();
		if (selrow >= 0) {
			return model.getElementAtRow(selrow);
		}
		return null;
	}

	public List<TData> getSelectedElements() {
		return CCStreams.iterate(getSelectedRows()).map(r -> model.getElementAtRow(r)).enumerate();
	}

	public List<TData> getSelectedDataCopy() {
		return getSelectedElements();
	}

	public int getRowCount() {
		return table.getRowCount();
	}

	public void setData(List<TData> newdata) {
		model.setData(newdata);
	}

	public List<TData> getDataCopy() {
		return model.getDataCopy();
	}
	
	public void setFilter(Func1to1<TData, Boolean> filter) {
		sorter.setRowFilter(new JCCSimpleRowFilter<>(filter));
	}
	
	public void resetFilter() {
		sorter.setRowFilter(null);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			TData el = getSelectedElement();
			if (el != null) OnDoubleClickElement(el);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// NOP
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// NOP
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// NOP
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// NOP
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting() && getSelectedRow() >= 0) {
			TData el = getSelectedElement();
			if (el != null) OnSelectElement(el);
		}
	}

	public void changeData(int idx, TData newData) {
		model.changeData(idx, newData);
	}

	public void changeData(TData oldData, TData newData) {
		model.changeData(oldData, newData);
	}
	
	public void forceFullRedraw() {
		model.fireTableStructureChanged();
	}
	
	public void forceDataChangedRedraw() {
		model.fireTableDataChanged();
	}

	protected abstract List<JCCSimpleColumnPrototype<TData>> configureColumns();
	protected abstract void OnDoubleClickElement(TData element);
	protected abstract void OnSelectElement(TData element);
	protected abstract int getColumnAdjusterMaxWidth();
	protected abstract boolean isMultiselect();
}
