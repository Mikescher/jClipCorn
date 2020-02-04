package de.jClipCorn.gui.guiComponents;

import javax.swing.*;
import java.util.Vector;

public class ReadableCombobox<T> extends JComboBox<T> {
	private static final long serialVersionUID = 2094003237821712036L;

	public ReadableCombobox() {
		super();
		setReadable();
	}

	public ReadableCombobox(ComboBoxModel<T> arg0) {
		super(arg0);
		setReadable();
	}

	public ReadableCombobox(T[] arg0) {
		super(arg0);
		setReadable();
	}

	public ReadableCombobox(Vector<T> arg0) {
		super(arg0);
		setReadable();
	}
	
	private void setReadable() {
		setEditable(true);
		ComboBoxEditor cbxEditor = getEditor();
		JTextField etf = (JTextField)cbxEditor.getEditorComponent();
		etf.setDisabledTextColor(UIManager.getColor("ComboBox.foreground")); //$NON-NLS-1$
		etf.setBackground(UIManager.getColor("ComboBox.background")); //$NON-NLS-1$
		super.setEnabled(false);
	}

	@Override
	public void setEnabled(boolean e) {		
		setReadable();
	}
}
