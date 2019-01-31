package de.jClipCorn.gui.guiComponents.dndList;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;

class DnDHandler<T> extends TransferHandler {
	private static final long serialVersionUID = -6298554952267257444L;

	private DnDList<T> list;

	public DnDHandler(DnDList<T> list) {
		this.list = list;
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
		if (dl.getIndex() == -1) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}

		Transferable transferable = support.getTransferable();
		String indexString;
		try {
			indexString = (String) transferable.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			return false;
		}

		int index = Integer.parseInt(indexString);
		JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
		int dropTargetIndex = dl.getIndex();

		if (list.getModel() instanceof DefaultListModel) {
			DefaultListModel<T> dlm = (DefaultListModel<T>) list.getModel();
			
			if (index > dropTargetIndex) {
				T el = dlm.getElementAt(index);
				dlm.remove(index);
				dlm.add(dropTargetIndex, el);
			} else if (dropTargetIndex > index) {
				dlm.add(dropTargetIndex, dlm.getElementAt(index));
				dlm.remove(index);
			}
		}
		
		return true;
	}
}
