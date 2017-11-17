package de.jClipCorn.gui.guiComponents.jSimpleTree;

import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.tree.TreePath;

public class SimpleTreeObject {
	public class SimpleTreeEvent { public boolean ctrlDown; public SimpleTreeObject source; public TreePath path; }
	
	private final Icon icon;
	private final String text;
	private final Consumer<SimpleTreeEvent> listener;
	
	public SimpleTreeObject(Icon icon, String text, Consumer<SimpleTreeEvent> listener) {
		this.icon = icon;
		this.text = text;
		this.listener = listener;
	}

	public SimpleTreeObject(Icon icon, String text) {
		this.icon = icon;
		this.text = text;
		this.listener = null;
	}

	public Icon getIcon() {
		return icon;
	}
	
	public String getText() {
		return text;
	}
	
	public void execute(TreePath p, boolean ctrlDown) {
		
		SimpleTreeEvent e = new SimpleTreeEvent();
		
		e.ctrlDown = ctrlDown;
		e.source = this;
		e.path = p;
		
		if (listener != null) listener.accept(e);
	}
}
