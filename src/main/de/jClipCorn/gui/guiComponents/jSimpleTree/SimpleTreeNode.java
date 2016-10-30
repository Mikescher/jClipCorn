package de.jClipCorn.gui.guiComponents.jSimpleTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

public class SimpleTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = -9210313559698406304L;

	public SimpleTreeNode() {
		super(new SimpleTreeObject(null, "")); //$NON-NLS-1$
	}
	
	public SimpleTreeNode(Icon icon, String text, ActionListener listener) {
		super(new SimpleTreeObject(icon, text, listener));
	}
	
	public SimpleTreeNode(Icon icon, String text, Runnable listener) {
		super(new SimpleTreeObject(icon, text, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.run();
			}
		}));
	}

	public SimpleTreeNode(Icon icon, String text) {
		super(new SimpleTreeObject(icon, text));
	}
}
