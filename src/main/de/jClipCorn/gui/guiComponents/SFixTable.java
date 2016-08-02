package de.jClipCorn.gui.guiComponents;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.ToolTipManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class SFixTable extends JTable {
	private static final long serialVersionUID = 1082882838948078289L;
	
	public SFixTable(TableModel dm) {
		super(dm);
		
		fixTableSort();
		
		initListener();
	}
	
	public SFixTable() {
		super();
		
		fixTableSort();
		
		initListener();
	}

	public SFixTable(Vector<?> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
		
		fixTableSort();
		
		initListener();
	}
	
	private void initListener() {
		addMouseListener(new MouseAdapter() {
		    final int defaultTimeout = ToolTipManager.sharedInstance().getInitialDelay();

			@Override
			public void mouseExited(MouseEvent e) {
		        ToolTipManager.sharedInstance().setInitialDelay(defaultTimeout);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
		        ToolTipManager.sharedInstance().setInitialDelay(0);
			}
		});
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
		super.changeSelection(rowIndex, columnIndex, toggle && (! extend), false);
		
		if (getSelectedRowCount() > 1) {
			super.changeSelection(rowIndex, columnIndex, false, false);
		}
	}
	
	private void fixTableSort() {
		for (MouseListener mouseListener : getTableHeader().getMouseListeners()) {
		    if (mouseListener instanceof javax.swing.plaf.basic.BasicTableHeaderUI.MouseInputHandler) {
		        getTableHeader().removeMouseListener(mouseListener);
		    }
		}
		
		getTableHeader().addMouseListener(new MouseAdapter() {
		    private SortOrder currentOrder = SortOrder.UNSORTED;

		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int column = getTableHeader().columnAtPoint(e.getPoint());
		        RowSorter<?> sorter = getRowSorter();
		        if (sorter == null) return;
		        List<SortKey> sortKeys = new ArrayList<>();
		        switch (currentOrder) {
		            case UNSORTED:
		                sortKeys.add(new RowSorter.SortKey(column, currentOrder = SortOrder.ASCENDING));
		                break;
		            case ASCENDING:
		                sortKeys.add(new RowSorter.SortKey(column, currentOrder = SortOrder.DESCENDING));
		                break;
		            case DESCENDING:
		                sortKeys.add(new RowSorter.SortKey(column, currentOrder = SortOrder.UNSORTED));
		                break;
		        }
		        sorter.setSortKeys(sortKeys);
		    }
		});
	}
	
	public void resetSort() {
		for (int i = 0; i < getColumnCount(); i++) {
		    RowSorter<?> sorter = getRowSorter();
		    List<SortKey> sortKeys = new ArrayList<>();
		    sortKeys.add(new RowSorter.SortKey(i, SortOrder.UNSORTED));
		    sorter.setSortKeys(sortKeys);
		}
	}

    @Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);

        if (c instanceof JComponent) {
            Object value = getValueAt(row, column);
    		int realColumn = convertColumnIndexToModel(column); // So you can move the positions of the Columns ...
            JComponent jc = (JComponent) c;
            
            jc.setToolTipText(null);
            
    		switch (realColumn) {
			case ClipTableModel.COLUMN_SCORE: // Score
				break;
			case ClipTableModel.COLUMN_TITLE: // Name
				break;
			case ClipTableModel.COLUMN_VIEWED: // Viewed
    			if (((ExtendedViewedState)value).getHistory().any()) jc.setToolTipText(((ExtendedViewedState)value).getHistory().getHTMLListFormatted(row));
				break;
			case ClipTableModel.COLUMN_ZYKLUS: // Zyklus
				break;
			case ClipTableModel.COLUMN_QUALITY: // Quality
				break;
			case ClipTableModel.COLUMN_LANGUAGE: // Language
				break;
			case ClipTableModel.COLUMN_GENRE: // Genres
				break;
			case ClipTableModel.COLUMN_PARTCOUNT: // Partcount
				break;
			case ClipTableModel.COLUMN_LENGTH: // Length
				break;
			case ClipTableModel.COLUMN_DATE: // Date
				break;
			case ClipTableModel.COLUMN_ONLINESCORE: // OnlineScore
				break;
			case ClipTableModel.COLUMN_TAGS: // Tags
				break;
			case ClipTableModel.COLUMN_FSK: // FSK
				break;
			case ClipTableModel.COLUMN_FORMAT: // Format
				break;
			case ClipTableModel.COLUMN_YEAR: // Year
				break;
			case ClipTableModel.COLUMN_SIZE: // Filesize
				break;
			default:
    			System.out.println("Mysterious switch jump in [SFixTable.java]"); //$NON-NLS-1$
    		}
        }
        
        return c;
    }
}
