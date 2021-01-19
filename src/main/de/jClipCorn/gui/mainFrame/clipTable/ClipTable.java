package de.jClipCorn.gui.mainFrame.clipTable;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.TableCustomFilter;
import de.jClipCorn.features.table.filter.customFilter.CustomUserScoreFilter;
import de.jClipCorn.features.table.filter.customFilter.CustomZyklusFilter;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.MainFrameColumn;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.TableColumnAdjuster;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SuppressWarnings("restriction")
public class ClipTable extends JScrollPane implements CCDBUpdateListener, ListSelectionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -1226727910191440220L;

	private final SFixClipTable table;
	private final ClipTableModel model;
	private final MainFrame owner;

	private String _adjusterConfig = Str.Empty;
	private final TableColumnAdjuster adjuster;
	
	private TableCustomFilter currentFilter = null;

	private boolean suppressRowFilterResetEvents = false;

	public ClipTable(CCMovieList ml, MainFrame owner) {
		super();
		this.owner = owner;

		model = new ClipTableModel(ml);

		table = new SFixClipTable(model);
		model.setTable(table);
		configureTable();

		this.setViewportView(table);

		adjuster = new TableColumnAdjuster(this, table);

		var icfg = CCProperties.getInstance().PROP_MAINFRAME_COLUMN_SIZE_CACHE.getValue();
		if (Str.isNullOrWhitespace(icfg)) icfg = CCStreams.iterate(MainFrameColumn.values()).stringjoin(e-> "keep", "|"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!adjuster.isValidConfig(icfg)) icfg = CCStreams.iterate(MainFrameColumn.values()).stringjoin(e-> "10", "|"); //$NON-NLS-1$ //$NON-NLS-2$

		adjuster.adjustColumns(icfg);

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
		table.addMouseMotionListener(this);
	}

	public void configureColumnVisibility(Set<MainFrameColumn> data, boolean initial) {
		String[] cfg = new String[MainFrameColumn.values().length];
		Arrays.fill(cfg, "auto"); //$NON-NLS-1$

		for (MainFrameColumn c : MainFrameColumn.values()) {
			if (data.contains(c)) {
				cfg[c.ColumnIndex] = c.AdjusterConfig;

				if (initial) continue;
				TableColumn column = table.getColumn(c);
				column.setMinWidth(0);
				column.setMaxWidth(Integer.MAX_VALUE);
				column.setPreferredWidth(128);
				if (column.getWidth() == 0) column.setWidth(50);

			} else {
				TableColumn column = table.getColumn(c);
				column.setMinWidth(0);
				column.setMaxWidth(0);
				column.setPreferredWidth(0);

				cfg[c.ColumnIndex] = "0"; //$NON-NLS-1$
			}
		}

		_adjusterConfig = CCStreams.iterate(cfg).stringjoin(e->e, "|"); //$NON-NLS-1$
	}

	public void autoResize() {
		adjuster.adjustColumns(_adjusterConfig);
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
		int row = el.getMovieListPosition();
		
		if (row > 0)
			model.fireTableRowsUpdated(row, row);
	}
	
	@Override
	public void onRefresh() {
		model.fireTableDataChanged();
	}

	@Override
	public void onAfterLoad() {
		model.fireTableDataChanged();
		initialSort();
		autoResize();

		var columnconfig = adjuster.getCurrentStateAsConfig();
		CCProperties.getInstance().PROP_MAINFRAME_COLUMN_SIZE_CACHE.setValueIfDiff(columnconfig);
	}
	
	private void initialSort() {
		@SuppressWarnings("unchecked")
		TableRowSorter<ClipTableModel> sorter = ((TableRowSorter<ClipTableModel>)table.getRowSorter());
		List<SortKey> list = new ArrayList<>();	
		
		switch (CCProperties.getInstance().PROP_VIEW_DB_START_SORT.getValue()) {
		case LOCALID:
			//DO nothing
			return;
		case TITLE:
			list.add( new RowSorter.SortKey(ClipTableModel.COLUMN_TITLE, SortOrder.ASCENDING) );
			break;
		case ADDDATE:
			list.add( new RowSorter.SortKey(ClipTableModel.COLUMN_DATE, SortOrder.DESCENDING) );
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
		if (selrow >= 0 && selrow < table.getRowCount()) {
			return table.convertRowIndexToModel(selrow);
		}
		return -1;
	}

	public CCDatabaseElement getSelectedDatabaseElement() {
		int selrow = getSelectedRow();
		if (selrow >= 0) {
			return model.getDatabaseElementByRow(selrow);
		}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			int row = table.rowAtPoint(e.getPoint());
			if (row < 0) return; // click on whitespace
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
			int row = table.rowAtPoint(e.getPoint());
			if (row >= 0 && row < table.getRowCount()) {
				table.setRowSelectionInterval(row, row);
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
		
		CCMovieZyklus zyklus = getZyklusUnderMouse(e.getPoint());
		if (e.getButton() == MouseEvent.BUTTON1 && zyklus != null) {
			setRowFilter(CustomZyklusFilter.create(zyklus), RowFilterSource.TABLE_CLICKED);
		}
		
		CCUserScore score = getScoreUnderMouse(e.getPoint());
		if (e.getButton() == MouseEvent.BUTTON1 && score != null) {
			setRowFilter(CustomUserScoreFilter.create(score), RowFilterSource.TABLE_CLICKED);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// Do nothing ...
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		CCMovieZyklus zyklus = getZyklusUnderMouse(e.getPoint());
		CCUserScore score = getScoreUnderMouse(e.getPoint());
	
		if (zyklus == null && score == null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setRowFilter(AbstractCustomFilter filterimpl, RowFilterSource source) { // Source kann null sein
		model.clearMapping();
		
		if (!suppressRowFilterResetEvents) {
			
			suppressRowFilterResetEvents = true;
			
			if (source != RowFilterSource.CHARSELECTOR) {
				owner.resetCharSelector();
			}
			if (source != RowFilterSource.SIDEBAR) {
				owner.resetSidebar();
			}
			if (source != RowFilterSource.TEXTFIELD) {
				owner.resetSearchField(true);
			}
			
			suppressRowFilterResetEvents = false;
			
		}
		
		TableRowSorter<ClipTableModel> sorter = ((TableRowSorter<ClipTableModel>) table.getRowSorter());
		
		if (filterimpl == null) {
			currentFilter = null;
			sorter.setRowFilter(null);
		} else {
			currentFilter = new TableCustomFilter(filterimpl);
			sorter.setRowFilter(currentFilter);
		}
		
		if (! hasSortOrder()) {
			initialSort();
		}
		
		owner.getStatusBar().updateLables_Movies();
	}
	
	public TableCustomFilter getRowFilter() {
		return currentFilter;
	}
	
	private boolean hasSortOrder() {
		@SuppressWarnings("unchecked")
		TableRowSorter<ClipTableModel> sorter = ((TableRowSorter<ClipTableModel>) table.getRowSorter());
		
		for (SortKey skey : sorter.getSortKeys()) {
			if (skey.getSortOrder() != SortOrder.UNSORTED) return true;
		}
		
		return false;
	}

	public int getRowCount() {
		return table.getRowCount();
	}
	
	public MainFrame getMainFrame() {
		return owner;
	}
	
	private CCMovieZyklus getZyklusUnderMouse(Point p) {
		if (! CCProperties.getInstance().PROP_MAINFRAME_CLICKABLEZYKLUS.getValue()) return null;
		
		int vcol = table.columnAtPoint(p);
		int vrow = table.rowAtPoint(p);
		
		if (vcol == -1 || vrow == -1) {
			return null;
		}
		
		int mcol = table.convertColumnIndexToModel(vcol);
		int mrow = table.convertRowIndexToModel(vrow);
		
		if (mcol != ClipTableModel.COLUMN_ZYKLUS) {
			return null;
		}
		
		CCMovieZyklus zyklus = (CCMovieZyklus) table.getValueAt(vrow, vcol);
		
		if (zyklus.isEmpty()) return null;

		Component renderer = table.getCellRenderer(vrow, vcol).getTableCellRendererComponent(table, zyklus, false, false, mrow, mcol);
		int width = renderer.getFontMetrics(renderer.getFont()).stringWidth(zyklus.getFormatted());
		Rectangle clip = table.getCellRect(vrow, vcol, true);
		clip.width = width;
		
		if (clip.contains(p)) {
			return zyklus;
		} else {
			return null;
		}
	}
	
	private CCUserScore getScoreUnderMouse(Point p) {
		if (! CCProperties.getInstance().PROP_MAINFRAME_CLICKABLESCORE.getValue()) return null;
		
		int vcol = table.columnAtPoint(p);
		int vrow = table.rowAtPoint(p);
		
		if (vcol == -1 || vrow == -1) {
			return null;
		}
		
		int mcol = table.convertColumnIndexToModel(vcol);
		int mrow = table.convertRowIndexToModel(vrow);
		
		if (mcol != ClipTableModel.COLUMN_SCORE) {
			return null;
		}
		
		CCUserScore score = (CCUserScore) table.getValueAt(vrow, vcol);
		
		if (score == CCUserScore.RATING_NO) return null;

		Component renderer = table.getCellRenderer(vrow, vcol).getTableCellRendererComponent(table, score, false, false, mrow, mcol);
		int width = renderer.getFontMetrics(renderer.getFont()).stringWidth(score.asString());
		Rectangle clip = table.getCellRect(vrow, vcol, true);
		clip.width = width;
		
		if (clip.contains(p)) {
			return score;
		} else {
			return null;
		}
	}

	public void shuffle() {
		model.shuffle();
	}
}
