package de.jClipCorn.util.enumextension;

import javax.swing.*;
import java.awt.*;

public class ContinoousEnumComboBoxRenderer<T extends ContinoousEnum<T>> extends DefaultListCellRenderer {

	private final EnumWrapper<T> _wrapper;

	public ContinoousEnumComboBoxRenderer(EnumWrapper<T> w) {
		_wrapper = w;
	}

	@SuppressWarnings("unchecked")
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value != null) value = _wrapper.asDisplayString((T)value);
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		return this;
	}
}
