package de.jClipCorn.gui.frames.extendedSettingsFrame.settingsTable;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;

import de.jClipCorn.gui.guiComponents.CCDateEditor;
import de.jClipCorn.gui.guiComponents.KeyStrokeTextfield;
import de.jClipCorn.gui.guiComponents.SpinnerCCDateModel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCBoolProperty;
import de.jClipCorn.properties.property.CCIntProperty;
import de.jClipCorn.properties.property.CCProperty;
import de.jClipCorn.properties.property.CCRIntProperty;
import de.jClipCorn.util.CCDate;

public class SettingsTableEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1087066424626302463L;

	private final static String[] BOOL_LIST = { CCBoolProperty.TYPE_BOOL_FALSE, CCBoolProperty.TYPE_BOOL_TRUE };

	private final CCProperties properties;
	private JComboBox<String> cbxBoolean;
	private JSpinner spinner;
	private JTextField edit;
	private KeyStrokeTextfield	ksEdit;

	private int currMode = -1;

	public SettingsTableEditor(CCProperties properties) {
		super();
		this.properties = properties;
		init();
	}

	private void init() {
		cbxBoolean = new JComboBox<String>(new DefaultComboBoxModel<String>(BOOL_LIST));

		spinner = new JSpinner();

		edit = new JTextField();
		
		ksEdit = new KeyStrokeTextfield();
	}

	@Override
	public Object getCellEditorValue() {
		switch (currMode) {
		case 0:
			return new Boolean(cbxBoolean.getSelectedIndex() == 1);
		case 1:
			return edit.getText();
		case 2:
			return spinner.getValue();
		case 3:
			return spinner.getValue();
		case 4:
			return ksEdit.getKeyStroke();
		default:
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.STEUnknownPropertyType", currMode + "")); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		CCProperty<?> prop = properties.getPropertyList().get(table.convertRowIndexToModel(row));
		if (prop.getValue().getClass().equals(Boolean.class)) {
			currMode = 0;

			cbxBoolean.setSelectedIndex(((Boolean) prop.getValue()) ? 1 : 0);

			return cbxBoolean;
		} else if (prop.getValue().getClass().equals(String.class)) {
			currMode = 1;

			edit.setText((String) prop.getValue());

			return edit;
		} else if (prop.getValue().getClass().equals(Integer.class)) {
			currMode = 2;

			if (prop instanceof CCRIntProperty) {
				spinner.setModel(new SpinnerNumberModel(((Integer) prop.getValue()).intValue(), ((CCRIntProperty) prop).getMin(), ((CCRIntProperty) prop).getMax() - 1, 1));
			} else if (prop instanceof CCIntProperty){
				spinner.setModel(new SpinnerNumberModel(((Integer) prop.getValue()).intValue(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
			}

			return spinner;
		} else if (prop.getValue().getClass().equals(Double.class)) {
			currMode = 3;
			
			spinner.setModel(new SpinnerNumberModel(((Double) prop.getValue()).doubleValue(), Double.MIN_VALUE, Double.MAX_VALUE, 1d));
			
			return spinner;
		} else if (prop.getValue().getClass().equals(CCDate.class)) {
			currMode = 3;
			
			spinner.setModel(new SpinnerCCDateModel((CCDate) prop.getValue(), CCDate.getNewMinimumDate(), null));
			spinner.setEditor(new CCDateEditor(spinner));
			
			return spinner;
		} else if (prop.getValue().getClass().equals(KeyStroke.class)) {
			currMode = 4;
			
			ksEdit.setKeyStroke((KeyStroke) prop.getValue());
			
			return ksEdit;
		} else {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.STEUnknownPropertyType", prop.getValue().getClass().getSimpleName())); //$NON-NLS-1$
			return null;
		}
	}

}
