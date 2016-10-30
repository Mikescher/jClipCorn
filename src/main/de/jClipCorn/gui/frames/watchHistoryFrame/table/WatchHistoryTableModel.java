package de.jClipCorn.gui.frames.watchHistoryFrame.table;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.guiComponents.tableRenderer.TableModelRowColorInterface;
import de.jClipCorn.gui.localization.LocaleBundle;

public class WatchHistoryTableModel extends AbstractTableModel implements TableModelRowColorInterface, TableModelListener {
	private static final long serialVersionUID = -9030827698760632090L;
	
	public final static int COLUMN_NAME = 0;
	public final static int COLUMN_DATE = 1;
	public final static int COLUMN_QUALITY = 2;
	public final static int COLUMN_LANGUAGE = 3;
	public final static int COLUMN_LENGTH = 4;
	public final static int COLUMN_TAGS = 5;
	public final static int COLUMN_FORMAT = 6;
	public final static int COLUMN_SIZE = 7;
	
	private static final String[] COLUMN_NAMES = {
			LocaleBundle.getString("ClipTableModel.Title"), 				//$NON-NLS-1$
			LocaleBundle.getString("WatchHistoryFrame.tableHeaders.Date"),  //$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Quality"),  				//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Language"),  			//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Length"),  				//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Tags"),  				//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Format"), 				//$NON-NLS-1$
			LocaleBundle.getString("ClipTableModel.Size")  					//$NON-NLS-1$
	};
	
	private List<WatchHistoryElement> elements = new ArrayList<>();
	
	public WatchHistoryTableModel() {
		super();
		
		this.addTableModelListener(this);
	}

	@Override
	public String getColumnName(int col) {
		return COLUMN_NAMES[col].toString();
	}

	@Override
	public int getRowCount() {
		return elements.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return elements.get(row);
	}
	
	public WatchHistoryElement getElementAtRow(int row) {
		return elements.get(row);
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		// NOP
	}

	@Override
	public Color getRowColor(int row) {
		return Color.WHITE;
	}

	public void setData(List<WatchHistoryElement> newdata) {
		elements = newdata;
		fireTableDataChanged();
	}
}
