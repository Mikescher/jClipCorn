package de.jClipCorn.gui.guiComponents.enumComboBox;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

import javax.swing.*;
import java.awt.*;

public class ContinoousEnumComboBoxRenderer<T extends ContinoousEnum<T>> extends DefaultListCellRenderer {
	private static final long serialVersionUID = -504930006813784064L;
	
	private final EnumWrapper<T> _wrapper;

	public ContinoousEnumComboBoxRenderer(EnumWrapper<T> w) {
		_wrapper = w;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value != null) value = _wrapper.asDisplayString((T)value);
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		return this;
	}
}
