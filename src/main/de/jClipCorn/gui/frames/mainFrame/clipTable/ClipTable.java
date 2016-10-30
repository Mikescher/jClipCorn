package de.jClipCorn.gui.frames.mainFrame.clipTable;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.util.CCDBUpdateListener;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.guiComponents.tableFilter.TableScoreFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.TableZyklusFilter;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.TableColumnAdjuster;

public class ClipTable extends JScrollPane implements CCDBUpdateListener, ListSelectionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -1226727910191440220L;

	private SFixClipTable table;
	private ClipTableModel model;
	private MainFrame owner;

	private TableColumnAdjuster adjuster;

	private boolean suppressRowFilterResetEvents = false;

	public ClipTable(CCMovieList ml, MainFrame owner) {
		super();
		this.owner = owner;

		model = new ClipTableModel(ml);

		table = new SFixClipTable(model);
		model.setTable(table);
		configureTable();

		this.setViewportView(table);

		adjuster = new TableColumnAdjuster(table);
		adjuster.setMaxAdjustWidth(550);
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
		table.addMouseMotionListener(this);
		
		if (this.getVerticalScrollBar().getUI() instanceof WindowsScrollBarUI)
			this.getVerticalScrollBar().setUI(new ClipVerticalScrollbarUI(32));
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
			setRowFilter(new TableZyklusFilter(zyklus), RowFilterSource.TABLE_CLICKED);
		}
		
		CCMovieScore score = getScoreUnderMouse(e.getPoint());
		if (e.getButton() == MouseEvent.BUTTON1 && score != null) {
			setRowFilter(new TableScoreFilter(score), RowFilterSource.TABLE_CLICKED);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// Do nothing ...
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		CCMovieZyklus zyklus = getZyklusUnderMouse(e.getPoint());
		CCMovieScore score = getScoreUnderMouse(e.getPoint());
	
		if (zyklus == null && score == null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setRowFilter(RowFilter<ClipTableModel, Object> filter, RowFilterSource source) { // Source kann null sein
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
		
		sorter.setRowFilter(filter);
		
		if (! hasSortOrder()) {
			initialSort();
		}
		
		owner.getStatusBar().updateLables_Movies();
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
	
	private CCMovieScore getScoreUnderMouse(Point p) {
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
		
		CCMovieScore score = (CCMovieScore) table.getValueAt(vrow, vcol);
		
		if (score == CCMovieScore.RATING_NO) return null;

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
