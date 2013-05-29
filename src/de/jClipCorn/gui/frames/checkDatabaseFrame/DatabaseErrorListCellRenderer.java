package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

import de.jClipCorn.database.databaseErrors.DatabaseError;

public class DatabaseErrorListCellRenderer extends SubstanceDefaultListCellRenderer {
	private static final long serialVersionUID = -7242329712702777086L;

	private final Color defColor;
	
	public DatabaseErrorListCellRenderer() {
		defColor = getForeground();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setText(value.toString());
		
		setForeground((((DatabaseError)value).isAutoFixable()) ? defColor : Color.RED);

		return this;
	}
}