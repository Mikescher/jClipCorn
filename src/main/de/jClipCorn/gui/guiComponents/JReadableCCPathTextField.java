package de.jClipCorn.gui.guiComponents;

import de.jClipCorn.util.filesystem.CCPath;

public class JReadableCCPathTextField extends ReadableTextField {
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
