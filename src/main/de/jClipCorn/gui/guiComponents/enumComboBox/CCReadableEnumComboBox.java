package de.jClipCorn.gui.guiComponents.enumComboBox;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

import javax.swing.*;

public class CCReadableEnumComboBox<T extends ContinoousEnum<T>> extends CCEnumComboBox<T> {

	public CCReadableEnumComboBox(EnumWrapper<T> wrapper) {
		super(wrapper);
		setEditor(new CCReadableEnumComboBoxEditor(wrapper));
		setReadable();
	}

	@SuppressWarnings("deprecation")
	private void setReadable() {
		//noinspection deprecation
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
