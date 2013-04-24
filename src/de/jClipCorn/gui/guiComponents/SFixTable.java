package de.jClipCorn.gui.guiComponents;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

public class SFixTable extends JTable {
	private static final long serialVersionUID = 1082882838948078289L;
	
	private boolean pressed = false;
	private int currSRow = -100;
	
	public SFixTable(TableModel dm) {
		super(dm);
	}
	
	public SFixTable() {
		super();
	}

	public SFixTable(Vector<?> rowData, Vector<?> columnNames) {
		super(rowData, columnNames);
	}

	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown() && !e.isControlDown()) {
			int row = rowAtPoint(e.getPoint());
			int col = columnAtPoint(e.getPoint());
			
			boolean isDragRelease = (e.getID() == MouseEvent.MOUSE_RELEASED) && row != currSRow;
			boolean isStartClick = (e.getID() == MouseEvent.MOUSE_PRESSED);
			
			if (e.getID() == MouseEvent.MOUSE_CLICKED || e.getID() == MouseEvent.MOUSE_PRESSED || e.getID() == MouseEvent.MOUSE_RELEASED) {
				if (row >= 0 && col >= 0) {
					if (isStartClick) {
						super.changeSelection(row, col, false, false);
					} else if (isDragRelease && currSRow >= 0) {
						super.changeSelection(currSRow, col, false, false);
					}
				}
				
				pressed = (e.getID() == MouseEvent.MOUSE_PRESSED);
				if (pressed) {
					currSRow = row;
				} else {
					currSRow = -100;
				}
			}
		}
		
		super.processMouseEvent(e);
	}
	
	@Override
	protected void processKeyEvent(KeyEvent e) {
		super.processKeyEvent(e);
		
		if (! e.isShiftDown()) {
			return;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_NUMPAD2 || e.getKeyCode() == KeyEvent.VK_DOWN) {
			super.changeSelection(getSelectionModel().getMaxSelectionIndex(), getSelectedColumn(), false, false);	
		} else if (e.getKeyCode() == KeyEvent.VK_NUMPAD8 || e.getKeyCode() == KeyEvent.VK_UP) {
			super.changeSelection(getSelectionModel().getMinSelectionIndex(), getSelectedColumn(), false, false);	
		}
	}
	
	@Override
	public boolean isCellSelected(int row, int col) {
		return (pressed)? (row == currSRow) : super.isCellSelected(row, col);
	}

}
