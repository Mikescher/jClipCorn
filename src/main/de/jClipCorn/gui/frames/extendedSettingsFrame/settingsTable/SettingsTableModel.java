package de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCProperty;

import javax.swing.table.AbstractTableModel;
import java.awt.*;

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
	public void setValueAt(Object value, int row, int col) {
		CCProperty<Object> prop = properties.getPropertyList().get(row);
		if (prop != null) {
			prop.setValue(prop.getAlternativeComponentValue((Component)value));
		
			fireTableDataChanged();
		}
	}
	
	@Override
	public String getColumnName(int col) {
		return COLUMN_NAMES[col];
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
