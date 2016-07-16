package de.jClipCorn.gui.guiComponents.referenceChooser;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;

public class RefChooserComboBoxRenderer implements ListCellRenderer<CCOnlineRefType> {
	
	private final DefaultListCellRenderer standardRenderer = new DefaultListCellRenderer();
	
	@Override
	public Component getListCellRendererComponent(JList<? extends CCOnlineRefType> list, CCOnlineRefType value, int index, boolean isSelected, boolean cellHasFocus) {
		Component lbl = standardRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if (lbl instanceof JLabel) {
			((JLabel)lbl).setText(value.asString());
			
			((JLabel)lbl).setIcon(value.getIcon());
			
			return lbl;
		} else {
			JLabel label = new JLabel();

			label.setText(value.asString());
			
			label.setIcon(value.getIcon());
			
			return label;
		}
	}

}
