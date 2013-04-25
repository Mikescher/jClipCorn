package de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCProperty;

public class SettingsTableEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1087066424626302463L;

	private final CCProperties properties;
	
	private Component curr = null;

	public SettingsTableEditor(CCProperties properties) {
		super();
		this.properties = properties;
	}

	@Override
	public Object getCellEditorValue() {
		return curr;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		CCProperty<Object> prop = properties.getPropertyList().get(table.convertRowIndexToModel(row));
		curr = prop.getAlternativeComponent();
		prop.setAlternativeComponentValueToValue(curr, prop.getValue());
		return curr;
	}

}
