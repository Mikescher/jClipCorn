package de.jClipCorn.gui.frames.previewSeriesFrame.serTable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.util.TableColumnAdjuster;

public class SerTable extends JScrollPane implements ListSelectionListener, MouseListener {
	private static final long serialVersionUID = 6640341234698681428L;
	
	private SFixSerTable table;
	private TableColumnAdjuster adjuster;
	private SerTableModel model;
	private CCSeason season;
	private PreviewSeriesFrame owner;
		
	public SerTable(CCSeason ser, PreviewSeriesFrame owner) {
		super();
		this.season = ser;
		this.owner = owner;
		
		model = new SerTableModel(season);
		
		table = new SFixSerTable(model);
		configureTable();
		
		setViewportView(table);
		
		adjuster = new TableColumnAdjuster(table);
		adjuster.setOnlyAdjustLarger(false);
		
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
		adjuster.setResizeAdjuster(true);
		adjuster.adjustColumns();
	}
	
	public void changeSeason(CCSeason s) {
		this.season = s;
		
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
	
	public CCEpisode getSelectedEpisode() {
		int selrow = getSelectedRow();
		if (selrow >= 0) {
			return season.getEpisode(selrow);
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
	public void mousePressed(MouseEvent arg0) {
		// Do Nothing
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// Do Nothing
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// Do Nothing
	}
}
