package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;

import javax.swing.*;

public class JCCPathTextField extends JTextField {

	public JCCPathTextField() { }
	public JCCPathTextField(FSPath text) { super(text.toString()); }
	public JCCPathTextField(int columns) { super(columns); }
	public JCCPathTextField(FSPath text, int columns) { super(text.toString(), columns); }

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

	public CCPath getPath() {
		return CCPath.create(super.getText());
	}

	public void setPath(CCPath t) {
		super.setText(t.toString());
	}
}
