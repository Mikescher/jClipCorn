package de.jClipCorn.gui.frames.mainFrame.filterTree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FilterRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -1327828202075208415L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (value != null && value instanceof DefaultMutableTreeNode) {
			FilterTreeNode node = (FilterTreeNode)((DefaultMutableTreeNode)value).getUserObject();
			if (node != null) {
				setIcon(node.getIcon());
				setText(node.getText());
				setToolTipText(null);
			}
		}
		
		return this;
	}
}
