package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.util.TableColumnAdjuster;
import de.jClipCorn.util.datatypes.ITuple;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple0;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("restriction")
public abstract class JCCSimpleTable<TData> extends JScrollPane implements IJCCSimpleTable, ListSelectionListener, MouseListener {
	private static final long serialVersionUID = -87600498201225357L;
	
	private List<JCCSimpleColumnPrototype<TData>> columns;
	private JCCSimpleTableModel<TData> model;
	private JCCSimpleSFixTable<TData> table;
	private TableRowSorter<JCCSimpleTableModel<TData>> sorter;

	private String _autoResizeConfig;
	private TableColumnAdjuster adjuster;

	protected final CCMovieList movielist;
	protected final ITuple initOptions;

	public JCCSimpleTable(@NotNull ICCWindow mlo) {
		this(mlo.getMovieList(), Tuple0.Inst);
	}

	public JCCSimpleTable(@NotNull ICCWindow mlo, ITuple opt) {
		this(mlo.getMovieList(), opt);
	}

	public JCCSimpleTable(CCMovieList ml) {
		this(ml, Tuple0.Inst);
	}

	public JCCSimpleTable(CCMovieList ml, ITuple opt) {
		super();
		movielist = ml;
		initOptions = opt;

		init(configureColumns());
	}

	public CCMovieList getMovielist() {
		return movielist;
	}

	@SuppressWarnings("unchecked")
	protected <T> T initOptions() {
		return (T)initOptions;
	}

	protected void setColumnConfig(JCCSimpleColumnList<TData> cfg) {
		init(cfg);
	}

	private void init(JCCSimpleColumnList<TData> cfg) {
		columns = cfg.get();

		model = new JCCSimpleTableModel<>(columns);

		sorter = new TableRowSorter<>(model);
		sorter.setSortsOnUpdates(true);

		table = new JCCSimpleSFixTable<>(movielist, model, columns);
		configureTable();

		this.setViewportView(table);

		_autoResizeConfig = CCStreams.iterate(columns).stringjoin(e -> e.AutoResizeConfig, "|"); //$NON-NLS-1$

		adjuster = new TableColumnAdjuster(this, table);

		autoResize();
	}

	private void configureTable() {
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowHeight(18);
		table.MultiSelect = isMultiselect();

		table.getSelectionModel().addListSelectionListener(this);
		table.addMouseListener(this);

		table.setRowSorter(sorter);

		for (int i = 0; i < columns.size(); i++) sorter.setSortable(i, columns.get(i).IsSortable);
	}

	public void addListSelectionListener(ListSelectionListener listener)
	{
		table.getSelectionModel().addListSelectionListener(listener);
	}

	public void autoResize() {
		adjuster.adjustColumns(_autoResizeConfig);
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
		return CCStreams.iterate(getSelectedRows()).map(model::getElementAtRow).enumerate();
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

	public void clearData() {
		model.setData(new ArrayList<>());
	}

	public List<TData> getDataCopy() {
		return model.getDataCopy();
	}

	public List<TData> getDataDirect() {
		return model.getDataDirect();
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

	public void addData(TData newData) {
		model.addData(Collections.singletonList(newData));
	}

	public void addData(List<TData> newData) {
		model.addData(newData);
	}

	public void changeData(TData oldData, TData newData) {
		model.changeData(oldData, newData);
	}

	public void scrollIntoView(TData data) {
		int idx = model.getIndex(data);
		if (idx >= 0) {
			Rectangle rect = table.getCellRect(idx, 0, true);
			if (rect.width>0 && rect.height>0) table.scrollRectToVisible(rect);
		}
	}
	
	public void forceFullRedraw() {
		model.fireTableStructureChanged();
	}
	
	public void forceDataChangedRedraw() {
		model.fireTableDataChanged();
	}

	public void setSelectedRow(int idx, boolean scrollTo) {
		table.getSelectionModel().setSelectionInterval(idx, idx);
		if (scrollTo) scrollToRow(idx);
	}

	public void scrollToRow(int idx) {
		table.getSelectionModel().setSelectionInterval(idx, idx);
		table.scrollRectToVisible(new Rectangle(table.getCellRect(idx, 0, true)));
	}

	protected abstract JCCSimpleColumnList<TData> configureColumns();
	protected abstract void OnDoubleClickElement(TData element);
	protected abstract void OnSelectElement(TData element);
	protected abstract boolean isMultiselect();
}
