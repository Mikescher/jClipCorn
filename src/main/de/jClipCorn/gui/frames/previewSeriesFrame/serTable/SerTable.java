package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.features.actionTree.menus.impl.ClipEpisodePopup;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.util.TableColumnAdjuster;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class SerTable extends JScrollPane implements ListSelectionListener, MouseListener {
	private static final long serialVersionUID = 6640341234698681428L;
	
	private final SFixSerTable table;
	private final TableColumnAdjuster adjuster;
	private final SerTableModel model;
	private final PreviewSeriesFrame owner;

	private CCSeason season;

	@DesignCreate
	private static SerTable designCreate() { return new SerTable(null); }

	public SerTable(PreviewSeriesFrame owner) {
		super();
		this.owner = owner;

		model = new SerTableModel(null);
		
		table = new SFixSerTable(model);
		configureTable();
		
		setViewportView(table);
		
		adjuster = new TableColumnAdjuster(this, table);

		autoResize();
	}

	private void configureTable() {
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowHeight(18);
		
		table.getSelectionModel().addListSelectionListener(this);
		table.addMouseListener(this);
	}
	
	public void autoResize() {
		adjuster.adjustColumns("auto|*,min=auto|auto|auto|auto|auto|auto|auto|auto|auto|auto"); //$NON-NLS-1$
	}
	
	public void changeSeason(CCSeason s) {
		this.season = s;
		
		getVerticalScrollBar().setValue(0);
		
		model.changeSeason(s);
		
		model.fireTableDataChanged();
		
		autoResize();
	}
	
	public int getSelectedRow() {
		int selrow = table.getSelectedRow();
		if (selrow >= 0) {
			return table.convertRowIndexToModel(table.getSelectedRow());
		}
		return -1;
	}
	
	public void setSelectedRow(int row) {
		if (row < 0) return;
		
		row = table.convertRowIndexToView(row);
		
		table.getSelectionModel().setSelectionInterval(row, row);
	}
	
	public CCEpisode getSelectedEpisode() {
		int selrow = getSelectedRow();
		if (selrow >= 0) {
			return season.getEpisodeByArrayIndex(selrow);
		}
		return null;
	}

	public CCSeason getSeason() {
		return season;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			owner.onEpisodeDblClick(getSelectedEpisode());
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Do Nothing
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Do Nothing
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// Do Nothing
	}

	public void select(CCEpisode e) {
		if (e == null) {
			changeSeason(null);
			table.getSelectionModel().clearSelection();
		} else {
			changeSeason(e.getSeason());
			table.getSelectionModel().setSelectionInterval(e.getEpisodeIndexInSeason(), e.getEpisodeIndexInSeason());
			table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), table.getSelectedColumn(), false));
		}
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
					if (getSelectedEpisode() != null) {
						new ClipEpisodePopup(owner, getSelectedEpisode()).show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		}
	}

	@Override
	public void requestFocus() {
		table.requestFocus();
		if (table.getRowCount() > 0 && table.getColumnCount() > 0 && table.getSelectedRow() == -1) table.changeSelection(0, 0, false, false);
		else if (table.getRowCount() > 0 && table.getColumnCount() > 0 && table.getSelectedRow() >= 0) table.changeSelection(table.getSelectedRow(), 0, false, false);
	}

	@Override
	public boolean isFocusOwner() {
		return table.isFocusOwner();
	}
}
