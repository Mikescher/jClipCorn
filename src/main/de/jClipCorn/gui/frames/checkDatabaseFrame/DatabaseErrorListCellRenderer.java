package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.jClipCorn.features.databaseErrors.DatabaseError;

public class DatabaseErrorListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -7242329712702777086L;

	private final Color defColor;
	
	public DatabaseErrorListCellRenderer() {
		defColor = getForeground();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		DefaultListCellRenderer result = (DefaultListCellRenderer) super.getListCellRendererComponent(paramlist, value, index, isSelected, cellHasFocus);
		
		result.setText(value.toString());
		
		result.setForeground((((DatabaseError)value).isAutoFixable()) ? defColor : Color.RED);

		return result;
	}
}