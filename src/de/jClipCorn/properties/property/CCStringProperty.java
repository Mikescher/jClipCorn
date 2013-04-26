package de.jClipCorn.properties.property;

import java.awt.Component;

import javax.swing.JTextField;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;

public class CCStringProperty extends CCProperty<String> {
	public CCStringProperty(int cat, CCProperties prop, String ident, String standard) {
		super(cat, String.class, prop, ident, standard);
	}

	@Override
	public Component getComponent() {
		return new JTextField(1);
	}

	@Override
	public void setComponentValueToValue(Component c, String val) {
		((JTextField)c).setText(val);
	}

	@Override
	public String getComponentValue(Component c) {
		return ((JTextField)c).getText();
	}

	@Override
	public String getValue() {
		String val = properties.getProperty(identifier);
		
		if (val == null) {
			CCLog.addInformation(LocaleBundle.getFormattedString("LogMessage.PropNotFound", identifier)); //$NON-NLS-1$
			setDefault();
			return standard;
		}
		
		return val;
	}

	@Override
	public String setValue(String val) {
		properties.setProperty(identifier, val);
		
		return getValue();
	}
}
