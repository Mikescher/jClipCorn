package de.jClipCorn.gui.guiComponents.dndList;

import java.awt.Cursor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.DropMode;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class DnDList<T> extends JList<T> implements DragSourceListener, DragGestureListener {
	private static final long serialVersionUID = -268336445288960741L;
	
	private DragSource ds;
	
	private StringSelection transferable;
	
	public DnDList() {
		super();
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setDragEnabled(true);
	    setDropMode(DropMode.INSERT);
		ds = new DragSource();
	    ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
	    setTransferHandler(new DnDHandler<>(this));
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent e) {
		transferable = new StringSelection(Integer.toString(getSelectedIndex()));
		ds.startDrag(e, Cursor.getDefaultCursor(), transferable, this);
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent e) {
		// Unused
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		// Unused
	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		// Unused
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		// Unused
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent arg0) {
		// Unused
	}
}
