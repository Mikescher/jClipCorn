package de.jClipCorn.gui.guiComponents.jSimpleTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class SimpleTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -1327828202075208415L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (value instanceof DefaultMutableTreeNode) {
			SimpleTreeObject node = (SimpleTreeObject)((DefaultMutableTreeNode)value).getUserObject();
			if (node != null) {
				setIcon(node.getIcon());
				setText(node.getText());
				setToolTipText(null);
			}
		}
		
		return this;
	}
}