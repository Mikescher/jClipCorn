package de.jClipCorn.gui.guiComponents;

import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.Document;

public class ReadableTextField extends JTextField {
	private static final long serialVersionUID = -6919571638705521853L;

	public ReadableTextField() {
		super();
		setReadable();
	}

	public ReadableTextField(String arg0) {
		super(arg0);
		setReadable();
	}

	public ReadableTextField(int arg0) {
		super(arg0);
		setReadable();
	}

	public ReadableTextField(String arg0, int arg1) {
		super(arg0, arg1);
		setReadable();
	}

	public ReadableTextField(Document arg0, String arg1, int arg2) {
		super(arg0, arg1, arg2);
		setReadable();
	}
	
	private void setReadable() {
		super.setEnabled(false);
		setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$;
		setDisabledTextColor(UIManager.getColor("TextField.foreground")); //$NON-NLS-1$
	}
	
	@Override
	public void setEnabled(boolean e) {		
		setReadable();
	}
}
