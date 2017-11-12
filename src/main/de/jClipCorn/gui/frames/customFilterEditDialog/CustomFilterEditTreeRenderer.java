package de.jClipCorn.gui.frames.customFilterEditDialog;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.customFilter.aggregators.CustomAggregator;
import de.jClipCorn.table.filter.customFilter.operators.CustomOperator;

public class CustomFilterEditTreeRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -1327828202075208415L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		if (value != null && value instanceof DefaultMutableTreeNode) {
			AbstractCustomFilter node = (AbstractCustomFilter)((DefaultMutableTreeNode)value).getUserObject();
			if (node != null) {
				
				if (node instanceof CustomOperator)
					setIcon(CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_CUSTOM_OPERATOR));
				else if (node instanceof CustomAggregator)
					setIcon(CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_CUSTOM_AGGREGATOR));
				else
					setIcon(CachedResourceLoader.getIcon(Resources.ICN_SIDEBAR_CUSTOM));
				
				setText(node.getName());
				
				setToolTipText(null);
			}
		}
		
		return this;
	}
}