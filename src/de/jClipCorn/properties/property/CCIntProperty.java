package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;

public class CCIntProperty extends CCProperty<Integer> {
	CCIntProperty(int cat, CCProperties prop, String ident, Integer standard) {
		super(cat, Integer.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
	}

	@Override
	public void setComponentValueToValue(Component c, Integer val) {
		((JSpinner)c).setValue(val);
	}

	@Override
	public Integer getComponentValue(Component c) {
		return (Integer) ((JSpinner)c).getValue();
	}

	@Override
	public Integer getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}

		try {
			int ival = Integer.parseInt(val);
			return ival;
		} catch(NumberFormatException e) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.PropFormatErrorNumber", identifier, mclass.getName())); //$NON-NLS-1$
			setDefault();
			return standard;
		}
	}

	@Override
	public Integer setValue(Integer val) {
		properties.setProperty(identifier, "" + val); //$NON-NLS-1$
		
		return getValue();
	}
}
