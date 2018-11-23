package de.jClipCorn.gui.frames.customFilterEditDialog;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.table.filter.AbstractCustomFilter;

public class CustomFilterEditTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -1327828202075208415L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (value != null && value instanceof DefaultMutableTreeNode) {
			AbstractCustomFilter node = (AbstractCustomFilter)((DefaultMutableTreeNode)value).getUserObject();
			if (node != null) {
				
				setIcon(CachedResourceLoader.getIcon(node.getListIcon()));
				
				setText(node.getName());
				
				setToolTipText(null);
			}
		}
		
		return this;
	}
}