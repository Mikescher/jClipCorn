package de.jClipCorn.gui.frames.customFilterEditDialog;

import javax.swing.tree.DefaultMutableTreeNode;

import de.jClipCorn.features.table.filter.AbstractCustomFilter;

public class CustomFilterEditTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1093634468154407062L;

	public final AbstractCustomFilter filter;
	
	public String textCache = ""; //$NON-NLS-1$
	
	public CustomFilterEditTreeNode(AbstractCustomFilter f) {
		super(f);
		
		filter = f;
	}
}
