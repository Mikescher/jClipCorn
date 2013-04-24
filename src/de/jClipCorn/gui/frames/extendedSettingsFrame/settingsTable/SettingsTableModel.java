package de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable;

import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCBoolProperty;
import de.jClipCorn.properties.property.CCDateProperty;
import de.jClipCorn.properties.property.CCDoubleProperty;
import de.jClipCorn.properties.property.CCIntProperty;
import de.jClipCorn.properties.property.CCKeyStrokeProperty;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.properties.property.CCStringProperty;
import de.jClipCorn.util.CCDate;

public class SettingsTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1222130050102832876L;

	private static final String[] COLUMN_NAMES = {
		LocaleBundle.getString("extendedSettingsFrame.COL_1"),  //$NON-NLS-1$
		LocaleBundle.getString("extendedSettingsFrame.COL_2")   //$NON-NLS-1$
	};
	
	private final CCProperties properties;
	
	public SettingsTableModel(CCProperties p) {
		super();
		this.properties = p;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) { //TODO Replace Code like this in ExtendedSettingsFrame with better Code (use CCProperty Methods)
		CCProperty<?> prop = properties.getPropertyList().get(row);
		if (prop instanceof CCBoolProperty) {
			((CCBoolProperty)prop).setValue((Boolean)value);
		} else if (prop instanceof CCDoubleProperty) {
			((CCDoubleProperty)prop).setValue((Double)value);
		} else if (prop instanceof CCIntProperty) {
			((CCIntProperty)prop).setValue((Integer)value);
		} else if (prop instanceof CCStringProperty) {
			((CCStringProperty)prop).setValue((String)value);
		} else if (prop instanceof CCDateProperty) {
			((CCDateProperty)prop).setValue((CCDate)value);
		} else if (prop instanceof CCKeyStrokeProperty) {
			((CCKeyStrokeProperty)prop).setValue((KeyStroke)value);
		}  else {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.STEUnknownPropertyType", prop.getClass().getSimpleName())); //$NON-NLS-1$
		}
		
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int col) {
		return COLUMN_NAMES[col].toString();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return properties.getPropertyList().size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return properties.getPropertyList().get(row).getIdentifier();
		case 1:
			return properties.getPropertyList().get(row).getValueAsString();
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 1;
	}
}
