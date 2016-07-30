package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.CCPropertyCategory;
import de.jClipCorn.util.exceptions.BooleanFormatException;

public class CCBoolProperty extends CCProperty<Boolean> {
	public final static String TYPE_BOOL_TRUE = "true"; //$NON-NLS-1$
	public final static String TYPE_BOOL_FALSE = "false"; //$NON-NLS-1$
	private final static String[] BOOL_LIST = { TYPE_BOOL_FALSE, TYPE_BOOL_TRUE };
	
	public CCBoolProperty(CCPropertyCategory cat, CCProperties prop, String ident, boolean standard) {
		super(cat, Boolean.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new JCheckBox();
	}

	@Override
	public void setComponentValueToValue(Component c, Boolean val) {
		((JCheckBox)c).setSelected(val);
	}

	@Override
	public Boolean getComponentValue(Component c) {
		return ((JCheckBox)c).isSelected();
	}
	
	@Override
	public Component getAlternativeComponent() {
		return new JComboBox<>(new DefaultComboBoxModel<>(BOOL_LIST));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void setAlternativeComponentValueToValue(Component c, Boolean val) {
		((JComboBox<String>)c).setSelectedIndex((val) ? (1) : (0));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Boolean getAlternativeComponentValue(Component c) {
		return ((JComboBox<String>)c).getSelectedIndex() == 1;
	}

	@Override
	public Boolean getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		try {
			boolean bval = stringToBool(val);
			return bval;
		} catch (BooleanFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorBool", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}
	
	private static String boolToString(boolean b) {
		return (b) ? (TYPE_BOOL_TRUE) : (TYPE_BOOL_FALSE);
	}
	
	private static boolean stringToBool(String b) throws BooleanFormatException {
		if (TYPE_BOOL_TRUE.equals(b)) {
			return true;
		} else if (TYPE_BOOL_FALSE.equals(b)) {
			return false;
		} else {
			throw new BooleanFormatException(b);
		}
	}

	@Override
	public Boolean setValue(Boolean val) {
		properties.setProperty(identifier, boolToString(val));
		
		return getValue();
	}
}
