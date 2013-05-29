package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

import de.jClipCorn.database.databaseErrors.DatabaseErrorType;
import de.jClipCorn.gui.localization.LocaleBundle;

public class ErrorTypeListCellRenderer extends SubstanceDefaultListCellRenderer {
	private static final long serialVersionUID = -7242329712702777086L;

	private final Color defColor;
	
	public ErrorTypeListCellRenderer() {
		defColor = getForeground();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value == null) {
			setText(LocaleBundle.getString("CheckDatabaseDialog.lsCategories.All")); //$NON-NLS-1$
			
			setForeground(defColor);
		} else {
			setText(value.toString());
		
			setForeground((((DatabaseErrorType)value).isAutoFixable()) ? defColor : Color.RED);
		}
		return this;
	}
}
