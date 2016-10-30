package de.jClipCorn.gui.guiComponents.jSimpleTree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class SimpleTreeSelectionListener implements TreeSelectionListener {

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Object o = e.getNewLeadSelectionPath().getLastPathComponent();
		
		if (o instanceof DefaultMutableTreeNode) {		
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
			
			Object user = node.getUserObject();
			if (user != null) {
				((SimpleTreeObject) user).execute();
			}
		}
	}

}
