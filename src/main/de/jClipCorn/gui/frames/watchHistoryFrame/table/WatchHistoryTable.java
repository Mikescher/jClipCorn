package de.jClipCorn.gui.frames.watchHistoryFrame.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipVerticalScrollbarUI;
import de.jClipCorn.gui.frames.watchHistoryFrame.WatchHistoryFrame;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.util.TableColumnAdjuster;

public class WatchHistoryTable extends JScrollPane implements ListSelectionListener, MouseListener {
	private static final long serialVersionUID = 4348817188505924336L;

	private SFixWatchHistoryTable table;
	private WatchHistoryTableModel model;
	private WatchHistoryFrame owner;

	private TableColumnAdjuster adjuster;

	public WatchHistoryTable(WatchHistoryFrame owner) {
		super();
		this.owner = owner;

		model = new WatchHistoryTableModel();

		table = new SFixWatchHistoryTable(model);
		configureTable();

		this.setViewportView(table);

		adjuster = new TableColumnAdjuster(table);
		adjuster.setOnlyAdjustLarger(false);
	}

	private void configureTable() {
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowHeight(18);

		table.getSelectionModel().addListSelectionListener(this);
		table.addMouseListener(this);
		
		if (this.getVerticalScrollBar().getUI() instanceof WindowsScrollBarUI)
			this.getVerticalScrollBar().setUI(new ClipVerticalScrollbarUI(32));
	}

	public void autoResize() {
		adjuster.adjustColumns();
		adjuster.setResizeAdjuster(true);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting() && getSelectedRow() >= 0) {
			owner.onSelect(getSelectedElement());
		}
	}

	public int getSelectedRow() {
		int selrow = table.getSelectedRow();
		if (selrow >= 0 && selrow < table.getRowCount()) {
			return table.convertRowIndexToModel(selrow);
		}
		return -1;
	}

	public WatchHistoryElement getSelectedElement() {
		int selrow = getSelectedRow();
		if (selrow >= 0) {
			return model.getElementAtRow(selrow);
		}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
			WatchHistoryElement el = getSelectedElement();
			if (el != null) el.open(owner);
		}
	}

	public int getRowCount() {
		return table.getRowCount();
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

	public void setData(List<WatchHistoryElement> newdata) {
		model.setData(newdata);
	}
}
