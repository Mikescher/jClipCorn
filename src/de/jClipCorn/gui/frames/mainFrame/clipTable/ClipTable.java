package de.jClipCorn.gui.frames.mainFrame.clipTable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.TableColumnAdjuster;

public class ClipTable extends JScrollPane implements CCDBUpdateListener, ListSelectionListener, MouseListener {
	private static final long serialVersionUID = -1226727910191440220L;

	private SFixClipTable table;
	private ClipTableModel model;
	private CCMovieList movielist;
	private MainFrame owner;

	private TableColumnAdjuster adjuster;

	public ClipTable(CCMovieList ml, MainFrame owner) {
		super();
		this.owner = owner;
		this.movielist = ml;

		model = new ClipTableModel(ml);

		table = new SFixClipTable(model);
		model.setTable(table);
		configureTable();

		this.setViewportView(table);

		adjuster = new TableColumnAdjuster(table);
		adjuster.setOnlyAdjustLarger(false);

		if (ml != null) { // Sonst meckert der WindowsBuilder
			ml.addChangeListener(this);
		}
	}

	private void configureTable() {
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowHeight(18);

		table.getSelectionModel().addListSelectionListener(this);
		table.addMouseListener(this);
	}

	public void autoResize() {
		adjuster.adjustColumns();
		adjuster.setResizeAdjuster(true);
	}

	@Override
	public void onAddDatabaseElement(CCDatabaseElement mov) {
		model.fireTableDataChanged();
	}

	@Override
	public void onRemMovie(CCDatabaseElement el) {
		model.fireTableDataChanged();
	}

	@Override
	public void onChangeDatabaseElement(CCDatabaseElement el) {
		model.fireTableDataChanged();
	}

	@Override
	public void onAfterLoad() {
		model.fireTableDataChanged();
		initialSort();
		autoResize();
	}
	
	private void initialSort() {
		@SuppressWarnings("unchecked")
		TableRowSorter<ClipTableModel> sorter = ((TableRowSorter<ClipTableModel>)table.getRowSorter());
    	ArrayList<SortKey> list = new ArrayList<>();	
		
		switch (CCProperties.getInstance().PROP_VIEW_DB_START_SORT.getValue()) {
		case 0:
			//DO nothing
			return;
		case 1:
			list.add( new RowSorter.SortKey(1, SortOrder.ASCENDING) );
			break;
		case 2:
			list.add( new RowSorter.SortKey(9, SortOrder.DESCENDING) );
			break;
		}
		
		sorter.setSortKeys(list);
    	sorter.sort();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting() && getSelectedRow() >= 0) {
			owner.onClipTableSelectionChanged(getSelectedDatabaseElement());
		}
	}

	public int getSelectedRow() {
		int selrow = table.getSelectedRow();
		if (selrow >= 0) {
			return table.convertRowIndexToModel(table.getSelectedRow());
		}
		return -1;
	}

	public CCDatabaseElement getSelectedDatabaseElement() {
		int selrow = getSelectedRow();
		if (selrow >= 0) {
			return movielist.getDatabaseElementBySort(selrow);
		}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			owner.onClipTableExecute(getSelectedDatabaseElement());
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Reserved for later use
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Reserved for later use
	}

	@Override
	public void mousePressed(MouseEvent e) {
		onMouseAction(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		onMouseAction(e);
	}

	private void onMouseAction(MouseEvent e) {
		if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
			int r = table.rowAtPoint(e.getPoint());
			if (r >= 0 && r < table.getRowCount()) {
				table.setRowSelectionInterval(r, r);
			} else {
				table.clearSelection();
			}

			int rowindex = table.getSelectedRow();
			if (rowindex >= 0) {
				if (e.isPopupTrigger()) {
					owner.onClipTableSecondaryExecute(getSelectedDatabaseElement(), e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setRowFilter(RowFilter<ClipTableModel, Object> filter, RowFilterSource source) {
		((TableRowSorter<ClipTableModel>) table.getRowSorter()).setRowFilter(filter);

		owner.getStatusBar().updateLables_Movies();

		if (source != RowFilterSource.CHARSELECTOR) {
			owner.resetCharSelector();
		}
		if (source != RowFilterSource.SIDEBAR) {
			owner.resetSidebar();
		}
		if (source != RowFilterSource.TEXTFIELD) {
			owner.resetSearchField();
		}
	}

	public int getRowCount() {
		return table.getRowCount();
	}
}
