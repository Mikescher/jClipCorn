package de.jClipCorn.gui.guiComponents.jSimpleTree;

import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import de.jClipCorn.gui.guiComponents.jSimpleTree.SimpleTreeObject.SimpleTreeEvent;

public class SimpleTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = -9210313559698406304L;

	public SimpleTreeNode() {
		super(new SimpleTreeObject(null, "")); //$NON-NLS-1$
	}
	
	public SimpleTreeNode(Icon icon, String text, Consumer<SimpleTreeEvent> listener) {
		super(new SimpleTreeObject(icon, text, listener));
	}
	
	public SimpleTreeNode(Icon icon, String text, Runnable listener) {
		super(new SimpleTreeObject(icon, text, e -> listener.run()));
	}

	public SimpleTreeNode(Icon icon, String text) {
		super(new SimpleTreeObject(icon, text));
	}
}
