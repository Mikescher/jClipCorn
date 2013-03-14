package de.jClipCorn.gui.frames.mainFrame.filterTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;

public class FilterTreeNode {
	private final Icon icon;
	private final String text;
	private final ActionListener listener;
	
	public FilterTreeNode(Icon icon, String text, ActionListener listener) {
		this.icon = icon;
		this.text = text;
		this.listener = listener;
	}

	public Icon getIcon() {
		return icon;
	}
	
	public String getText() {
		return text;
	}
	
	public void execute() {
		if (listener != null) {
			listener.actionPerformed(new ActionEvent(this, 0, null));
		}
	}
}
