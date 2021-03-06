package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import de.jClipCorn.features.databaseErrors.DatabaseErrorType;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.CountAppendix;

public class ErrorTypeListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -7242329712702777086L;

	private final Color defColor;
	
	public ErrorTypeListCellRenderer() {
		defColor = getForeground();
	}
	
	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		DefaultListCellRenderer result = (DefaultListCellRenderer) super.getListCellRendererComponent(paramlist, value, index, isSelected, cellHasFocus);
		
		if (value == null) {
			result.setText(LocaleBundle.getString("CheckDatabaseDialog.lsCategories.All")); //$NON-NLS-1$
			
			result.setForeground(defColor);
		} else {
			result.setText(value.toString());
		
			result.setForeground((((CountAppendix<DatabaseErrorType>)value).Value.isAutoFixable()) ? defColor : Color.RED);
		}
		return result;
	}
}
