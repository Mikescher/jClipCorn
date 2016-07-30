package de.jClipCorn.gui.guiComponents.jCheckBoxList;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class CBCellRenderer implements ListCellRenderer<JCheckBox> {

	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	
	@Override
	public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index, boolean isSelected, boolean cellHasFocus) {
		JCheckBox checkbox = value;
		checkbox.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
		checkbox.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
		checkbox.setEnabled(list.isEnabled());
		checkbox.setFont(list.getFont());
		checkbox.setFocusPainted(false);
		checkbox.setBorderPainted(true);
		checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder); //$NON-NLS-1$
		return checkbox;
	}
}
