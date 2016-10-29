package de.jClipCorn.gui.guiComponents.jSimpleTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;

public class SimpleTreeObject {
	private final Icon icon;
	private final String text;
	private final ActionListener listener;
	
	public SimpleTreeObject(Icon icon, String text, ActionListener listener) {
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
	
	public void execute() {
		if (listener != null) listener.actionPerformed(new ActionEvent(this, 0, null));
	}
}
