package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.util.filesystem.FSPath;

import javax.swing.*;

public class JFSPathTextField extends JTextField {

	public JFSPathTextField() { }
	public JFSPathTextField(FSPath text) { super(text.toString()); }
	public JFSPathTextField(int columns) { super(columns); }
	public JFSPathTextField(FSPath text, int columns) { super(text.toString(), columns); }

	@Override
	@Deprecated
	public String getText() {
		return super.getText();
	}

	@Override
	@Deprecated
	public void setText(String t) {
		super.setText(t);
	}

	public FSPath getPath() {
		return FSPath.create(super.getText());
	}

	public void setPath(FSPath t) {
		super.setText(t.toString());
	}
}
