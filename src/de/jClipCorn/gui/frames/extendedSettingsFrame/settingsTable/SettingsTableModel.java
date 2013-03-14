package de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable;

import javax.swing.table.AbstractTableModel;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCBoolProperty;
import de.jClipCorn.properties.property.CCDateProperty;
import de.jClipCorn.properties.property.CCDoubleProperty;
import de.jClipCorn.properties.property.CCIntProperty;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.properties.property.CCStringProperty;
import de.jClipCorn.util.CCDate;

public class SettingsTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1222130050102832876L;

	private static final String[] columnNames = {
		LocaleBundle.getString("extendedSettingsFrame.COL_1"),  //$NON-NLS-1$
		LocaleBundle.getString("extendedSettingsFrame.COL_2")   //$NON-NLS-1$
	};
	
	private final CCProperties properties;
	
	public SettingsTableModel(CCProperties p) {
		super();
		this.properties = p;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		CCProperty<?> p = properties.getPropertyList().get(row);
		if (p instanceof CCBoolProperty) {
			((CCBoolProperty)p).setValue((Boolean)value);
		} else if (p instanceof CCDoubleProperty) {
			((CCDoubleProperty)p).setValue((Double)value);
		} else if (p instanceof CCIntProperty) {
			((CCIntProperty)p).setValue((Integer)value);
		} else if (p instanceof CCStringProperty) {
			((CCStringProperty)p).setValue((String)value);
		} else if (p instanceof CCDateProperty) {
			((CCDateProperty)p).setValue((CCDate)value);
		} else {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.STEUnknownPropertyType", p.getClass().getSimpleName())); //$NON-NLS-1$
		}
		
		fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int col) {
		return columnNames[col].toString();
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
			return properties.getPropertyList().get(row).getValue();
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 1;
	}
}
