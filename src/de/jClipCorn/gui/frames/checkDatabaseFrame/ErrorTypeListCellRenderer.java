package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.jClipCorn.database.databaseErrors.DatabaseErrorType;
import de.jClipCorn.gui.localization.LocaleBundle;

public class ErrorTypeListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -7242329712702777086L;

	private final Color defColor;
	
	public ErrorTypeListCellRenderer() {
		defColor = getForeground();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		DefaultListCellRenderer result = (DefaultListCellRenderer) super.getListCellRendererComponent(paramlist, value, index, isSelected, cellHasFocus);
		
		if (value == null) {
			result.setText(LocaleBundle.getString("CheckDatabaseDialog.lsCategories.All")); //$NON-NLS-1$
			
			result.setForeground(defColor);
		} else {
			result.setText(value.toString());
		
			result.setForeground((((DatabaseErrorType)value).isAutoFixable()) ? defColor : Color.RED);
		}
		return result;
	}
}
